package io.github.cotrin8672.block

import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.kinetics.drill.DrillMovementBehaviour
import com.simibubi.create.foundation.utility.BlockHelper
import net.minecraft.core.BlockPos

class EnchantableDrillMovementBehaviour : DrillMovementBehaviour() {
    override fun destroyBlock(context: MovementContext, breakingPos: BlockPos) {
        val state = context.state
        val efficiency = state.getValue(EnchantmentProperties.EFFICIENCY_LEVEL)
        val fortune = state.getValue(EnchantmentProperties.FORTUNE_LEVEL)
        val silkTouch = state.getValue(EnchantmentProperties.SILK_TOUCH_LEVEL)
        val stack = EnchantedItemFactory.getPickaxeItemStack(efficiency, fortune, silkTouch)
        BlockHelper.destroyBlockAs(context.world, breakingPos, null, stack, 1f) {
            this.dropItem(context, it)
        }
    }

    override fun getBlockBreakingSpeed(context: MovementContext): Float {
        return super.getBlockBreakingSpeed(context) * (context.state.getValue(EnchantmentProperties.EFFICIENCY_LEVEL) + 1)
    }
}
