package io.github.cotrin8672.renderer

import com.jozufozu.flywheel.backend.Backend
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator
import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
import com.simibubi.create.foundation.render.CachedBufferer
import com.simibubi.create.foundation.utility.AnimationTickHolder
import io.github.cotrin8672.blockentity.EnchantableMechanicalMixerBlockEntity
import io.github.cotrin8672.config.Config
import io.github.cotrin8672.util.use
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraftforge.client.model.data.ModelData
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
        val superBuffer = CachedBufferer.partial(AllPartialModels.SHAFTLESS_COGWHEEL, blockState)

        val renderedHeadOffset = be.getRenderedHeadOffset(partialTicks).toDouble()
        val speed = be.getRenderedHeadRotationSpeed(partialTicks)
        val time = AnimationTickHolder.getRenderTime(be.level)
        val angle = ((time * speed * 6 / 10f) % 360) / 180 * PI
        val poleRender = CachedBufferer.partial(AllPartialModels.MECHANICAL_MIXER_POLE, blockState)

        val vbCutout = buffer.getBuffer(RenderType.cutoutMipped())
        val headRender = CachedBufferer.partial(AllPartialModels.MECHANICAL_MIXER_HEAD, blockState)

        if (!Backend.canUseInstancing(be.level)) {
            standardKineticRotationTransform(superBuffer, be, light).renderInto(ms, vb)

            poleRender.translate(0.0, -renderedHeadOffset, 0.0)
                .light(light)
                .renderInto(ms, vb)

            headRender.rotateCentered(Direction.UP, angle.toFloat())
                .translate(0.0, -renderedHeadOffset, 0.0)
                .light(light)
                .renderInto(ms, vbCutout)
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
                    be.blockState, be.blockPos, be.level!!, ms, consumer, true, RANDOM, ModelData.EMPTY, null
                )

                standardKineticRotationTransform(superBuffer, be, light).renderInto(ms, consumer)

                poleRender.translate(0.0, -renderedHeadOffset, 0.0)
                    .light(light)
                    .renderInto(ms, consumer)

                headRender.rotateCentered(Direction.UP, angle.toFloat())
                    .translate(0.0, -renderedHeadOffset, 0.0)
                    .light(light)
                    .renderInto(ms, consumer)
            }
        }
    }

    companion object {
        private val RANDOM = RandomSource.create()
    }
}