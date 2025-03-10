package io.github.cotrin8672.cem.content.block.press

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator
import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
import dev.engine_room.flywheel.api.visualization.VisualizationManager
import io.github.cotrin8672.cem.client.CustomRenderType
import io.github.cotrin8672.cem.config.CemConfig
import io.github.cotrin8672.cem.util.nonNullLevel
import net.createmod.catnip.render.CachedBuffers
import net.createmod.catnip.render.SuperByteBuffer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.state.BlockState
import thedarkcolour.kotlinforforge.neoforge.forge.use

class EnchantableMechanicalPressRenderer(
    private val context: Context,
) : KineticBlockEntityRenderer<EnchantableMechanicalPressBlockEntity>(context) {
    override fun shouldRenderOffScreen(be: EnchantableMechanicalPressBlockEntity): Boolean {
        return true
    }

    override fun renderSafe(
        be: EnchantableMechanicalPressBlockEntity,
        partialTicks: Float,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int,
    ) {
        val consumer = SheetedDecalTextureGenerator(
            buffer.getBuffer(CustomRenderType.GLINT),
            ms.last(),
            0.007125f
        )
        val blockState = be.blockState
        val headModel = CachedBuffers.partialFacing(
            AllPartialModels.MECHANICAL_PRESS_HEAD,
            blockState,
            blockState.getValue(HORIZONTAL_FACING)
        )
        val pressingBehaviour = be.getPressingBehaviour()
        val renderedHeadOffset =
            pressingBehaviour.getRenderedHeadOffset(partialTicks) * pressingBehaviour.mode.headOffset


        ms.use {
            if (CemConfig.CONFIG.renderGlint.get()) {
                context.blockRenderDispatcher.renderBatched(
                    be.blockState, be.blockPos, be.nonNullLevel, ms, consumer, true, Random
                )
            }
            if (!VisualizationManager.supportsVisualization(be.level)) {
                headModel
                    .translate(0.0, -renderedHeadOffset.toDouble(), 0.0)
                    .light<SuperByteBuffer>(light)
                    .renderInto(ms, consumer)
                headModel
                    .translate(0.0, -renderedHeadOffset.toDouble(), 0.0)
                    .light<SuperByteBuffer>(light)
                    .renderInto(ms, buffer.getBuffer(RenderType.solid()))
            }
        }
    }

    override fun getRenderedBlockState(be: EnchantableMechanicalPressBlockEntity?): BlockState {
        return shaft(getRotationAxisOf(be))
    }

    companion object {
        private val Random = RandomSource.create()
    }
}
