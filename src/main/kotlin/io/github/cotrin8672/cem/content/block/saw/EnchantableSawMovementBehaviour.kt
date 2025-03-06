package io.github.cotrin8672.cem.content.block.saw

import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.render.ActorVisual
import com.simibubi.create.content.contraptions.render.ContraptionMatrices
import com.simibubi.create.content.kinetics.saw.SawMovementBehaviour
import com.simibubi.create.content.kinetics.saw.TreeCutter
import com.simibubi.create.foundation.utility.BlockHelper
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld
import dev.engine_room.flywheel.api.visualization.VisualizationContext
import io.github.cotrin8672.cem.content.entity.ContraptionBlockBreaker
import io.github.cotrin8672.cem.util.getEnchantmentLevel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.BlockTags
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.state.BlockState

class EnchantableSawMovementBehaviour : SawMovementBehaviour() {
    override fun destroyBlock(context: MovementContext?, breakingPos: BlockPos) {
        val level = context?.world
        val fakePlayer = if (level is ServerLevel) {
            ContraptionBlockBreaker.getBlockBreakerForMovementContext(level, context)
        } else null
        BlockHelper.destroyBlockAs(context?.world, breakingPos, fakePlayer, fakePlayer?.mainHandItem, 1f) {
            this.dropItem(context, it)
        }
    }

    override fun onBlockBroken(context: MovementContext?, pos: BlockPos?, brokenState: BlockState) {
        if (brokenState.`is`(BlockTags.LEAVES)) return
        val level = context?.world
        val fakePlayer = if (level is ServerLevel) {
            ContraptionBlockBreaker.getBlockBreakerForMovementContext(level, context)
        } else null

        val dynamicTree = TreeCutter.findDynamicTree(brokenState.block, pos)
        if (dynamicTree.isPresent) {
            dynamicTree.get().destroyBlocks(context?.world, fakePlayer) { stack, dropPos ->
                dropItemFromCutTree(context, stack, dropPos)
            }
            return
        }

        TreeCutter.findTree(context?.world, pos, brokenState)
            .destroyBlocks(context?.world, fakePlayer) { stack, dropPos ->
                dropItemFromCutTree(context, stack, dropPos)
            }
    }

    override fun getBlockBreakingSpeed(context: MovementContext): Float {
        return super.getBlockBreakingSpeed(context) * (getEnchantmentLevel(context, Enchantments.EFFICIENCY) + 1)
    }

    override fun createVisual(
        visualizationContext: VisualizationContext,
        simulationWorld: VirtualRenderWorld,
        movementContext: MovementContext,
    ): ActorVisual {
        return EnchantableSawActorVisual(visualizationContext, simulationWorld, movementContext)
    }

    override fun renderInContraption(
        context: MovementContext,
        renderWorld: VirtualRenderWorld,
        matrices: ContraptionMatrices,
        buffer: MultiBufferSource,
    ) {
        EnchantableSawRenderer.renderInContraption(context, renderWorld, matrices, buffer)
    }
}
