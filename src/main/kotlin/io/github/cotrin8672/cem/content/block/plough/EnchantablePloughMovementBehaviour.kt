package io.github.cotrin8672.cem.content.block.plough

import com.simibubi.create.content.contraptions.actors.plough.PloughMovementBehaviour
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.render.ContraptionMatrices
import com.simibubi.create.foundation.utility.BlockHelper
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld
import dev.engine_room.flywheel.api.visualization.VisualizationManager
import io.github.cotrin8672.cem.config.CemConfig
import io.github.cotrin8672.cem.content.entity.ContraptionBlockBreaker
import io.github.cotrin8672.cem.util.getEnchantmentLevel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.enchantment.Enchantments

class EnchantablePloughMovementBehaviour : PloughMovementBehaviour() {
    override fun destroyBlock(context: MovementContext?, breakingPos: BlockPos?) {
        val level = context?.world
        val fakePlayer = if (level is ServerLevel) {
            ContraptionBlockBreaker.getBlockBreakerForMovementContext(level, context)
        } else null
        BlockHelper.destroyBlockAs(context?.world, breakingPos, fakePlayer, fakePlayer?.mainHandItem, 1f) {
            this.dropItem(context, it)
        }
    }

    override fun getBlockBreakingSpeed(context: MovementContext): Float {
        return super.getBlockBreakingSpeed(context) * (getEnchantmentLevel(context, Enchantments.EFFICIENCY) + 1)
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
