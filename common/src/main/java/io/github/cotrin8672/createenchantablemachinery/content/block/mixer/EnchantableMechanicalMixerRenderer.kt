package io.github.cotrin8672.createenchantablemachinery.content.block.mixer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator
import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
import dev.engine_room.flywheel.api.visualization.VisualizationManager
import io.github.cotrin8672.createenchantablemachinery.config.Config
import io.github.cotrin8672.createenchantablemachinery.content.EnchantedRenderType
import io.github.cotrin8672.createenchantablemachinery.registrate.PartialModelRegistration
import io.github.cotrin8672.createenchantablemachinery.util.SuperBufferUtil
import io.github.cotrin8672.createenchantablemachinery.util.extension.use
import net.createmod.catnip.animation.AnimationTickHolder
import net.createmod.catnip.render.CachedBuffers
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
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

        val superBuffer =
            CachedBuffers.partial(AllPartialModels.SHAFTLESS_COGWHEEL, blockState)

        val renderedHeadOffset = be.getRenderedHeadOffset(partialTicks).toDouble()
        val speed = be.getRenderedHeadRotationSpeed(partialTicks)
        val time = AnimationTickHolder.getRenderTime(be.level)
        val angle = ((time * speed * 6 / 10f) % 360) / 180 * PI

        val poleRender =
            CachedBuffers.partial(AllPartialModels.MECHANICAL_MIXER_POLE, blockState)

        val headRender =
            CachedBuffers.partial(AllPartialModels.MECHANICAL_MIXER_HEAD, blockState)

        val vbCutout = buffer.getBuffer(RenderType.cutoutMipped())

        if (!VisualizationManager.supportsVisualization(be.level)) {
            standardKineticRotationTransform(superBuffer, be, light)
                .renderInto(ms, vb)

            poleRender.translate(0.0, -renderedHeadOffset, 0.0)
            SuperBufferUtil.applyPackedLight(poleRender, light)
            poleRender.renderInto(ms, vb)

            headRender.rotateCentered(angle.toFloat(), Direction.UP)
            headRender.translate(0.0, -renderedHeadOffset, 0.0)
            SuperBufferUtil.applyPackedLight(headRender, light)
            headRender.renderInto(ms, vbCutout)
        }

        ms.use {
            if (Config.renderGlint.get()) {
                val consumer = SheetedDecalTextureGenerator(
                    buffer.getBuffer(EnchantedRenderType.GLINT),
                    ms.last().pose(),
                    ms.last().normal(),
                    0.007125f
                )

                context.blockRenderDispatcher.renderBatched(
                    be.blockState, be.blockPos, be.level!!, ms, consumer, true, RANDOM
                )

                standardKineticRotationTransform(superBuffer, be, light)
                    .renderInto(ms, consumer)

                poleRender.translate(0.0, -renderedHeadOffset, 0.0)
                SuperBufferUtil.applyPackedLight(poleRender, light)
                poleRender.renderInto(ms, consumer)

                val enchantableHead =
                    CachedBuffers.partial(
                        PartialModelRegistration.ENCHANTABLE_MECHANICAL_MIXER_HEAD,
                        blockState
                    )

                enchantableHead.rotateCentered(angle.toFloat(), Direction.UP)
                enchantableHead.translate(0.0, -renderedHeadOffset, 0.0)
                SuperBufferUtil.applyPackedLight(enchantableHead, light)
                enchantableHead.renderInto(ms, consumer)
            }
        }
    }


    companion object {
        private val RANDOM = RandomSource.create()
    }
}
