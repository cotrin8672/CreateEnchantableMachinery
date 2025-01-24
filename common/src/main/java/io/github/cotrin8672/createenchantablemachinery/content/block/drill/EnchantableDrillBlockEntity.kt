package io.github.cotrin8672.createenchantablemachinery.content.block.drill

import com.simibubi.create.content.kinetics.drill.DrillBlockEntity
import com.simibubi.create.foundation.utility.BlockHelper
import com.simibubi.create.foundation.utility.Lang
import com.simibubi.create.foundation.utility.VecHelper
import io.github.cotrin8672.createenchantablemachinery.content.block.EnchantableBlockEntity
import io.github.cotrin8672.createenchantablemachinery.content.block.EnchantableBlockEntityDelegate
import io.github.cotrin8672.createenchantablemachinery.platform.BlockBreaker
import joptsimple.internal.Strings
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
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
        val efficiencyLevel = getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY)
        return super.getBreakSpeed() * (efficiencyLevel + 1)
    }

    override fun canBreak(stateToBreak: BlockState, blockHardness: Float): Boolean {
        return isBreakable(stateToBreak, blockHardness)
    }

    override fun onBlockBroken(stateToBreak: BlockState?) {
        val nonNullLevel = checkNotNull(level)
        val vec = VecHelper.offsetRandomly(VecHelper.getCenterOf(breakingPos), nonNullLevel.random, .125f)
        BlockHelper.destroyBlockAs(
            nonNullLevel,
            breakingPos,
            fakePlayer,
            fakePlayer?.mainHandItem,
            1f
        ) { stack: ItemStack ->
            if (stack.isEmpty) return@destroyBlockAs
            if (!nonNullLevel.gameRules.getBoolean(GameRules.RULE_DOBLOCKDROPS)) return@destroyBlockAs
            // if (nonNullLevel.restoringBlockSnapshots) return@destroyBlockAs TODO: Forgeのみの処理

            val entity = ItemEntity(nonNullLevel, vec.x, vec.y, vec.z, stack)
            entity.setDefaultPickUpDelay()
            entity.deltaMovement = Vec3.ZERO
            nonNullLevel.addFreshEntity(entity)
        }
    }

    override fun addToGoggleTooltip(tooltip: MutableList<Component>, isPlayerSneaking: Boolean): Boolean {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking)
        for (instance in getEnchantments()) {
            val level = instance.level
            Lang.text(Strings.repeat(' ', 0))
                .add(instance.enchantment.getFullname(level).copy())
                .forGoggles(tooltip)
        }
        return true
    }

    override fun read(compound: CompoundTag, clientPacket: Boolean) {
        readEnchantments(compound)
        super.read(compound, clientPacket)
    }

    override fun write(compound: CompoundTag, clientPacket: Boolean) {
        compound.remove(ItemStack.TAG_ENCH)
        writeEnchantments(compound)
        super.write(compound, clientPacket)
    }
}
