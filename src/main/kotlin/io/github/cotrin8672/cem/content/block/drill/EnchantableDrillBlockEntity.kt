package io.github.cotrin8672.cem.content.block.drill

import com.simibubi.create.content.kinetics.drill.DrillBlockEntity
import com.simibubi.create.foundation.utility.BlockHelper
import com.simibubi.create.foundation.utility.CreateLang
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntityDelegate
import io.github.cotrin8672.cem.content.entity.BlockBreaker
import io.github.cotrin8672.cem.util.holderLookup
import joptsimple.internal.Strings
import net.createmod.catnip.math.VecHelper
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantment.getFullname
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3

class EnchantableDrillBlockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState,
) : DrillBlockEntity(type, pos, state), EnchantableBlockEntity by EnchantableBlockEntityDelegate() {
    private val fakePlayer by lazy {
        if (this.level is ServerLevel)
            BlockBreaker(this.level as ServerLevel, this@EnchantableDrillBlockEntity) else null
    }

    override fun getBreakSpeed(): Float {
        val efficiency = holderLookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.EFFICIENCY)
        val efficiencyLevel = getEnchantmentLevel(efficiency)
        return super.getBreakSpeed() * (efficiencyLevel + 1)
    }

    override fun canBreak(stateToBreak: BlockState, blockHardness: Float): Boolean {
        return isBreakable(stateToBreak, blockHardness)
    }

    override fun onBlockBroken(stateToBreak: BlockState?) {
        if (optimiseCobbleGen(stateToBreak)) return

        val nonNullLevel = checkNotNull(level)
        val vec = VecHelper.offsetRandomly(VecHelper.getCenterOf(breakingPos), nonNullLevel.random, .125f)
        BlockHelper.destroyBlockAs(
            nonNullLevel,
            breakingPos,
            null,
            fakePlayer?.mainHandItem,
            1f
        ) { stack: ItemStack ->
            if (stack.isEmpty) return@destroyBlockAs
            if (!nonNullLevel.gameRules.getBoolean(GameRules.RULE_DOBLOCKDROPS)) return@destroyBlockAs
            if (nonNullLevel.restoringBlockSnapshots) return@destroyBlockAs

            val entity = ItemEntity(nonNullLevel, vec.x, vec.y, vec.z, stack)
            entity.setDefaultPickUpDelay()
            entity.deltaMovement = Vec3.ZERO
            nonNullLevel.addFreshEntity(entity)
        }
    }

    override fun addToGoggleTooltip(tooltip: MutableList<Component>, isPlayerSneaking: Boolean): Boolean {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking)

        if (getEnchantments().entrySet().isEmpty()) return false
        for (instance in getEnchantments().entrySet()) {
            CreateLang.text(Strings.repeat(' ', 0))
                .add(getFullname(instance.key, instance.intValue))
                .forGoggles(tooltip)
        }
        return true
    }

    override fun read(compound: CompoundTag, registries: HolderLookup.Provider, clientPacket: Boolean) {
        readEnchantments(compound, registries)
        super.read(compound, registries, clientPacket)
    }

    override fun write(compound: CompoundTag, registries: HolderLookup.Provider, clientPacket: Boolean) {
        writeEnchantments(compound, registries)
        super.write(compound, registries, clientPacket)
    }
}
