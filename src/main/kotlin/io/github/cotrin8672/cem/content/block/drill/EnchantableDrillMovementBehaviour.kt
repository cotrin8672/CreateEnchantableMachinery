package io.github.cotrin8672.cem.content.block.drill

import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.render.ActorVisual
import com.simibubi.create.content.contraptions.render.ContraptionMatrices
import com.simibubi.create.content.kinetics.drill.DrillMovementBehaviour
import com.simibubi.create.foundation.utility.BlockHelper
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld
import dev.engine_room.flywheel.api.visualization.VisualizationContext
import dev.engine_room.flywheel.api.visualization.VisualizationManager
import io.github.cotrin8672.cem.util.EnchantedItemFactory
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

class EnchantableDrillMovementBehaviour : DrillMovementBehaviour() {
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
        return EnchantableDrillActorVisual(visualizationContext, simulationWorld, movementContext)
    }

    @OnlyIn(Dist.CLIENT)
    override fun renderInContraption(
        context: MovementContext,
        renderWorld: VirtualRenderWorld,
        matrices: ContraptionMatrices,
        buffer: MultiBufferSource,
    ) {
        if (!VisualizationManager.supportsVisualization(context.world))
            EnchantableDrillRenderer.renderInContraption(context, renderWorld, matrices, buffer)
    }
}
