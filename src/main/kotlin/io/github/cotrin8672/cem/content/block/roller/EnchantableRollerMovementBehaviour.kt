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
import io.github.cotrin8672.cem.util.getEnchantmentLevel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.tags.BlockTags
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

        val stack = EnchantedItemFactory.getPickaxeItemStack(context.blockEntityData, context)
        BlockHelper.destroyBlockAs(context.world, breakingPos, null, stack, 1f) {
            if (
                getEnchantmentLevel(context, Enchantments.SILK_TOUCH) == 0 &&
                (noHarvest || context.world.random.nextBoolean())
            )
                return@destroyBlockAs
            this.dropItem(context, it)
        }

        super.destroyBlock(context, breakingPos)
    }

    override fun getBlockBreakingSpeed(context: MovementContext): Float {
        return super.getBlockBreakingSpeed(context) * (getEnchantmentLevel(context, Enchantments.EFFICIENCY) + 1)
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
