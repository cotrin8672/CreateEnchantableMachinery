package io.github.cotrin8672.block

import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.kinetics.drill.DrillMovementBehaviour
import com.simibubi.create.foundation.utility.BlockHelper
import net.minecraft.core.BlockPos
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class EnchantableDrillMovementBehaviour : DrillMovementBehaviour() {
    override fun destroyBlock(context: MovementContext, breakingPos: BlockPos) {
        BlockHelper.destroyBlockAs(context.world, breakingPos, null, ItemStack(Items.NETHERITE_PICKAXE), 1f) {
            this.dropItem(context, it)
        }
    }

    override fun getBlockBreakingSpeed(context: MovementContext): Float {
        return super.getBlockBreakingSpeed(context)
    }
}
