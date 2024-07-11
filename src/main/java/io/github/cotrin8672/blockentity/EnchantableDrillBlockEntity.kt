package io.github.cotrin8672.blockentity

import com.simibubi.create.content.kinetics.drill.DrillBlockEntity
import com.simibubi.create.foundation.utility.BlockHelper
import com.simibubi.create.foundation.utility.Lang
import com.simibubi.create.foundation.utility.VecHelper
import io.github.cotrin8672.block.EnchantedItemFactory
import io.github.cotrin8672.block.EnchantmentProperties
import joptsimple.internal.Strings
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
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
    private val state: BlockState,
) : DrillBlockEntity(type, pos, state) {
    private val enchantedItem = EnchantedItemFactory.getPickaxeItemStack(
        efficiencyLevel = state.getValue(EnchantmentProperties.EFFICIENCY_LEVEL),
        fortuneLevel = state.getValue(EnchantmentProperties.FORTUNE_LEVEL),
        silkTouchLevel = state.getValue(EnchantmentProperties.SILK_TOUCH_LEVEL)
    )

    override fun getBreakSpeed(): Float {
        return super.getBreakSpeed() * (state.getValue(EnchantmentProperties.EFFICIENCY_LEVEL) + 1)
    }

    override fun canBreak(stateToBreak: BlockState, blockHardness: Float): Boolean {
        return isBreakable(stateToBreak, blockHardness)
    }

    override fun onBlockBroken(stateToBreak: BlockState?) {
        val nonNullLevel = checkNotNull(level)
        val vec = VecHelper.offsetRandomly(VecHelper.getCenterOf(breakingPos), nonNullLevel.random, .125f)
        BlockHelper.destroyBlockAs(
            nonNullLevel, breakingPos, null, enchantedItem, 1f
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
        val efficiency = state.getValue(EnchantmentProperties.EFFICIENCY_LEVEL)
        val fortune = state.getValue(EnchantmentProperties.FORTUNE_LEVEL)
        val silkTouch = state.getValue(EnchantmentProperties.SILK_TOUCH_LEVEL)
        if (efficiency != 0) {
            Lang.text(Strings.repeat(' ', 0))
                .add(Enchantments.BLOCK_EFFICIENCY.getFullname(efficiency).copy())
                .forGoggles(tooltip)
        }
        if (fortune != 0) {
            Lang.text(Strings.repeat(' ', 0))
                .add(Enchantments.BLOCK_FORTUNE.getFullname(fortune).copy())
                .forGoggles(tooltip)
        }
        if (silkTouch != 0) {
            Lang.text(Strings.repeat(' ', 0))
                .add(Enchantments.SILK_TOUCH.getFullname(silkTouch).copy())
                .forGoggles(tooltip)
        }
        return true
    }
}
