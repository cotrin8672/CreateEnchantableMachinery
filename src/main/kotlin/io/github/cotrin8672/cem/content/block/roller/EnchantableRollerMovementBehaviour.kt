package io.github.cotrin8672.cem.content.block.roller

import com.simibubi.create.content.contraptions.actors.roller.RollerMovementBehaviour
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.render.ActorVisual
import com.simibubi.create.content.contraptions.render.ContraptionMatrices
import com.simibubi.create.foundation.utility.BlockHelper
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld
import dev.engine_room.flywheel.api.visualization.VisualizationContext
import dev.engine_room.flywheel.api.visualization.VisualizationManager
import io.github.cotrin8672.cem.util.EnchantedItemFactory
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.tags.BlockTags
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.Enchantments

class EnchantableRollerMovementBehaviour : RollerMovementBehaviour() {
    override fun createVisual(
        visualizationContext: VisualizationContext,
        simulationWorld: VirtualRenderWorld,
        movementContext: MovementContext,
    ): ActorVisual {
        return EnchantableRollerActorVisual(visualizationContext, simulationWorld, movementContext)
    }

    override fun destroyBlock(context: MovementContext?, breakingPos: BlockPos) {
        context ?: return
        val blockState = context.world.getBlockState(breakingPos)
        val noHarvest = (
                blockState.`is`(BlockTags.NEEDS_IRON_TOOL)
                        || blockState.`is`(BlockTags.NEEDS_STONE_TOOL)
                        || blockState.`is`(BlockTags.NEEDS_DIAMOND_TOOL)
                )

        if (context.temporaryData == null) {
            context.temporaryData = EnchantedItemFactory.getPickaxeItemStack(context)
        }

        val stack = context.temporaryData as ItemStack
        BlockHelper.destroyBlockAs(context.world, breakingPos, null, stack, 1f) {
            if ((noHarvest || context.world.random.nextBoolean()))
                return@destroyBlockAs
            this.dropItem(context, it)
        }

        super.destroyBlock(context, breakingPos)
    }

    override fun getBlockBreakingSpeed(context: MovementContext): Float {
        val enchantments = EnchantmentHelper.getEnchantments(ItemStack.EMPTY.apply {
            tag = context.blockEntityData
        })
        return super.getBlockBreakingSpeed(context) * ((enchantments[Enchantments.BLOCK_EFFICIENCY] ?: 0) + 1)
    }

    override fun renderInContraption(
        context: MovementContext,
        renderWorld: VirtualRenderWorld,
        matrices: ContraptionMatrices,
        buffers: MultiBufferSource,
    ) {
        if (!VisualizationManager.supportsVisualization(context.world))
            EnchantableRollerRenderer.renderInContraption(context, renderWorld, matrices, buffers)
    }
}
