package io.github.cotrin8672.cem.content.block.plough

import com.simibubi.create.content.contraptions.actors.plough.PloughMovementBehaviour
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.render.ContraptionMatrices
import com.simibubi.create.foundation.utility.BlockHelper
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld
import dev.engine_room.flywheel.api.visualization.VisualizationManager
import io.github.cotrin8672.cem.config.CemConfig
import io.github.cotrin8672.cem.util.EnchantedItemFactory
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.Enchantments

class EnchantablePloughMovementBehaviour : PloughMovementBehaviour() {
    override fun destroyBlock(context: MovementContext?, breakingPos: BlockPos?) {
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

    override fun renderInContraption(
        context: MovementContext,
        renderWorld: VirtualRenderWorld,
        matrices: ContraptionMatrices,
        buffer: MultiBufferSource,
    ) {
        super.renderInContraption(context, renderWorld, matrices, buffer)
        if (CemConfig.CONFIG.renderGlint.get())
            if (!VisualizationManager.supportsVisualization(context.world))
                EnchantablePloughRenderer.renderInContraption(context, matrices, buffer)
    }
}
