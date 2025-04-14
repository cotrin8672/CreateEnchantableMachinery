package io.github.cotrin8672.cem.content.block.saw

import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.render.ActorVisual
import com.simibubi.create.content.contraptions.render.ContraptionMatrices
import com.simibubi.create.content.kinetics.saw.SawMovementBehaviour
import com.simibubi.create.content.kinetics.saw.TreeCutter
import com.simibubi.create.foundation.utility.BlockHelper
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld
import dev.engine_room.flywheel.api.visualization.VisualizationContext
import io.github.cotrin8672.cem.util.EnchantedItemFactory
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.tags.BlockTags
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.state.BlockState

class EnchantableSawMovementBehaviour : SawMovementBehaviour() {
    override fun destroyBlock(context: MovementContext?, breakingPos: BlockPos) {
        context ?: return
        if (context.temporaryData == null) {
            context.temporaryData = EnchantedItemFactory.getPickaxeItemStack(context)
        }

        val stack = context.temporaryData as ItemStack
        BlockHelper.destroyBlockAs(context.world, breakingPos, null, stack, 1f) {
            this.dropItem(context, it)
        }
    }

    override fun onBlockBroken(context: MovementContext?, pos: BlockPos?, brokenState: BlockState) {
        if (brokenState.`is`(BlockTags.LEAVES)) return
        context ?: return
        if (context.temporaryData == null) {
            context.temporaryData = EnchantedItemFactory.getPickaxeItemStack(context)
        }

        val enchantedItem = context.temporaryData as ItemStack

        val dynamicTree = TreeCutter.findDynamicTree(brokenState.block, pos)
        if (dynamicTree.isPresent) {
            dynamicTree.get().destroyBlocks(context.world, enchantedItem, null) { stack, dropPos ->
                dropItemFromCutTree(context, stack, dropPos)
            }
            return
        }

        TreeCutter.findTree(context.world, pos, brokenState)
            .destroyBlocks(context.world, enchantedItem, null) { stack, dropPos ->
                dropItemFromCutTree(context, stack, dropPos)
            }
    }

    override fun getBlockBreakingSpeed(context: MovementContext): Float {
        val enchantments = EnchantmentHelper.getEnchantments(ItemStack.EMPTY.apply {
            tag = context.blockEntityData
        })
        return super.getBlockBreakingSpeed(context) * ((enchantments[Enchantments.BLOCK_EFFICIENCY] ?: 0) + 1)
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
