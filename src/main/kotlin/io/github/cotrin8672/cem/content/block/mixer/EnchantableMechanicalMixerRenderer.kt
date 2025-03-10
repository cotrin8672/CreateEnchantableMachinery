package io.github.cotrin8672.cem.content.block.mixer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator
import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
import dev.engine_room.flywheel.api.visualization.VisualizationManager
import io.github.cotrin8672.cem.client.CustomRenderType
import io.github.cotrin8672.cem.config.CemConfig
import io.github.cotrin8672.cem.registry.PartialModelRegistration
import io.github.cotrin8672.cem.util.nonNullLevel
import net.createmod.catnip.animation.AnimationTickHolder
import net.createmod.catnip.render.CachedBuffers
import net.createmod.catnip.render.SuperByteBuffer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import thedarkcolour.kotlinforforge.neoforge.forge.use
import kotlin.math.PI

class EnchantableMechanicalMixerRenderer(
    private val context: BlockEntityRendererProvider.Context,
) : KineticBlockEntityRenderer<EnchantableMechanicalMixerBlockEntity>(context) {
    override fun shouldRenderOffScreen(be: EnchantableMechanicalMixerBlockEntity): Boolean {
        return true
    }

    override fun renderSafe(
        be: EnchantableMechanicalMixerBlockEntity,
        partialTicks: Float,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int,
    ) {
        val blockState = be.blockState
        val vb = buffer.getBuffer(RenderType.solid())
        val superBuffer = CachedBuffers.partial(AllPartialModels.SHAFTLESS_COGWHEEL, blockState)

        val renderedHeadOffset = be.getRenderedHeadOffset(partialTicks).toDouble()
        val speed = be.getRenderedHeadRotationSpeed(partialTicks)
        val time = AnimationTickHolder.getRenderTime(be.nonNullLevel)
        val angle = ((time * speed * 6 / 10f) % 360) / 180 * PI
        val poleRender = CachedBuffers.partial(AllPartialModels.MECHANICAL_MIXER_POLE, blockState)

        if (!VisualizationManager.supportsVisualization(be.level)) {
            standardKineticRotationTransform(superBuffer, be, light).renderInto(ms, vb)

            poleRender.translate(0.0, -renderedHeadOffset, 0.0)
                .light<SuperByteBuffer>(light)
                .renderInto(ms, vb)

            val vbCutout = buffer.getBuffer(RenderType.cutoutMipped())
            val headRender = CachedBuffers.partial(AllPartialModels.MECHANICAL_MIXER_HEAD, blockState)
            headRender.rotateCentered(angle.toFloat(), Direction.UP)
                .translate(0.0, -renderedHeadOffset, 0.0)
                .light<SuperByteBuffer>(light)
                .renderInto(ms, vbCutout)
        }

        ms.use {
            if (CemConfig.CONFIG.renderGlint.get()) {
                val consumer = SheetedDecalTextureGenerator(
                    buffer.getBuffer(CustomRenderType.GLINT),
                    ms.last(),
                    0.007125f
                )

                context.blockRenderDispatcher.renderBatched(
                    be.blockState, be.blockPos, be.nonNullLevel, ms, consumer, true, RANDOM
                )
                val enchantableHead =
                    CachedBuffers.partial(PartialModelRegistration.ENCHANTABLE_MECHANICAL_MIXER_HEAD, blockState)
                enchantableHead
                    .rotateCentered(angle.toFloat(), Direction.UP)
                    .translate(0.0, -renderedHeadOffset, 0.0)
                    .light<SuperByteBuffer>(light)
                    .renderInto(ms, consumer)

                if (!VisualizationManager.supportsVisualization(be.level)) {
                    standardKineticRotationTransform(superBuffer, be, light).renderInto(ms, consumer)

                    poleRender.translate(0.0, -renderedHeadOffset, 0.0)
                        .light<SuperByteBuffer>(light)
                        .renderInto(ms, consumer)
                }
            }
        }
    }

    companion object {
        private val RANDOM = RandomSource.create()
    }
}
