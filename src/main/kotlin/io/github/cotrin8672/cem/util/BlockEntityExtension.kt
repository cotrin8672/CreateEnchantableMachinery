package io.github.cotrin8672.cem.util

import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
import com.simibubi.create.infrastructure.config.AllConfigs
import io.github.cotrin8672.cem.mixin.KineticBlockEntityMixin
import io.github.cotrin8672.cem.mixin.SmartBlockEntityMixin
import net.createmod.catnip.platform.CatnipServices
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.stats.Stats
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.IceBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.level.BlockEvent.BreakEvent


val BlockEntity.nonNullLevel: Level
    get() = checkNotNull(level)

fun <T> BlockEntity.holderLookup(registryKey: ResourceKey<out Registry<out T>>): HolderLookup<T> =
    checkNotNull(level).holderLookup(registryKey)

fun SmartBlockEntity.smartBlockEntityTick() {
    if (!(this as SmartBlockEntityMixin).initialized && hasLevel()) {
        initialize()
        initialized = true
    }
    if (lazyTickCounter-- <= 0) {
        lazyTickCounter = lazyTickRate
        lazyTick()
    }

    forEachBehaviour(BlockEntityBehaviour::tick)
}

fun KineticBlockEntity.kineticBlockEntityTick() {
    if (!nonNullLevel.isClientSide && needsSpeedUpdate()) attachKinetics()
    smartBlockEntityTick()
    (this as KineticBlockEntityMixin).effects.tick()

    preventSpeedUpdate = 0
    if (nonNullLevel.isClientSide) {
        CatnipServices.PLATFORM.executeOnClientOnly { Runnable(this::tickAudio) }
        return
    }

    if (this.validationCountdown-- <= 0) {
        validationCountdown = AllConfigs.server().kinetics.kineticValidationFrequency.get()
        (this as KineticBlockEntityMixin).invokeValidateKinetics()
    }

    if (flickerScore > 0) {
        flickerTally = flickerScore - 1
    }

    if (networkDirty) {
        if (hasNetwork()) orCreateNetwork.updateNetwork()
        networkDirty = false
    }
}

fun MechanicalMixerBlockEntity.basinOperatingBlockEntityTick() {
    if (basinRemoved) {
        basinRemoved = false
        if (!running) return
        runningTicks = 40
        running = false
        sendData()
        return
    }
    kineticBlockEntityTick()
}

fun destroyBlockAs(
    world: Level,
    pos: BlockPos,
    player: Player?,
    usedTool: ItemStack?,
    effectChance: Float,
    droppedItemCallback: (ItemStack) -> Unit,
) {
    var fluidState = world.getFluidState(pos)
    val state = world.getBlockState(pos)
    if (world.random.nextFloat() < effectChance) {
        world.levelEvent(2001, pos, Block.getId(state))
    }

    val blockEntity = if (state.hasBlockEntity()) world.getBlockEntity(pos) else null
    if (player != null) {
        val event = BreakEvent(world, pos, state, player)
        MinecraftForge.EVENT_BUS.post(event)
        if (event.isCanceled) {
            return
        }

        usedTool?.mineBlock(world, state, pos, player)
        player.awardStat(Stats.BLOCK_MINED[state.block])
    }

    if (world is ServerLevel && world.getGameRules()
            .getBoolean(GameRules.RULE_DOBLOCKDROPS) && !world.restoringBlockSnapshots && (player == null || !player.isCreative)
    ) {
        for (itemStack in Block.getDrops(state, world, pos, blockEntity, player, usedTool ?: ItemStack.EMPTY)) {
            droppedItemCallback(itemStack)
        }

        if (state.block is IceBlock && usedTool?.getEnchantmentLevel(Enchantments.SILK_TOUCH) == 0 && !world.dimensionType()
                .ultraWarm()
        ) {
            val below = world.getBlockState(pos.below())
            if (below.blocksMotion() || below.liquid()) {
                fluidState = IceBlock.meltsInto().fluidState
            }
        }

        state.spawnAfterBreak(world, pos, ItemStack.EMPTY, false)
    }

    world.setBlockAndUpdate(pos, fluidState.createLegacyBlock())
}
