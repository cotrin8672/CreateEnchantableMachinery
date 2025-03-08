package io.github.cotrin8672.cem.content.block.fan

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator
import com.mojang.blaze3d.vertex.VertexConsumer
import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
import dev.engine_room.flywheel.api.visualization.VisualizationManager
import io.github.cotrin8672.cem.client.CustomRenderType
import io.github.cotrin8672.cem.config.CemConfig
import io.github.cotrin8672.cem.util.nonNullLevel
import net.createmod.catnip.animation.AnimationTickHolder
import net.createmod.catnip.render.CachedBuffers
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class EnchantableEncasedFanRenderer(
    private val context: BlockEntityRendererProvider.Context,
) : KineticBlockEntityRenderer<EnchantableEncasedFanBlockEntity>(context) {
    override fun renderSafe(
        be: EnchantableEncasedFanBlockEntity,
        partialTicks: Float,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int,
    ) {
        if (CemConfig.CONFIG.renderGlint.get()) {
            val consumer = SheetedDecalTextureGenerator(
                buffer.getBuffer(CustomRenderType.GLINT),
                ms.last(),
                0.007125f
            )

            context.blockRenderDispatcher.renderBatched(
                be.blockState, be.blockPos, be.nonNullLevel, ms, consumer, true, RANDOM
            )
            renderOrigin(be, ms, buffer, consumer)
        }
    }

    private fun renderOrigin(
        be: EnchantableEncasedFanBlockEntity,
        ms: PoseStack,
        buffer: MultiBufferSource,
        consumer: VertexConsumer? = null,
    ) {
        if (VisualizationManager.supportsVisualization(be.nonNullLevel)) return

        val direction = be.blockState.getValue(BlockStateProperties.FACING)
        val vb = consumer ?: buffer.getBuffer(RenderType.cutoutMipped())

        val lightBehind = LevelRenderer.getLightColor(be.nonNullLevel, be.blockPos.relative(direction.opposite))
        val lightInFront = LevelRenderer.getLightColor(be.nonNullLevel, be.blockPos.relative(direction))

        val shaftHalf =
            CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, be.blockState, direction.opposite)
        val fanInner =
            CachedBuffers.partialFacing(AllPartialModels.ENCASED_FAN_INNER, be.blockState, direction.opposite)

        val time = AnimationTickHolder.getRenderTime(be.nonNullLevel)
        var speed = be.speed * 5
        if (speed > 0) speed = Mth.clamp(speed, 80f, (64 * 20f))
        if (speed < 0) speed = Mth.clamp(speed, (-64 * 20f), -80f)
        var angle = (time * speed * 3 / 10f) % 360
        angle = angle / 180f * Math.PI.toFloat()

        standardKineticRotationTransform(shaftHalf, be, lightBehind).renderInto(ms, vb)
        kineticRotationTransform(fanInner, be, direction.axis, angle, lightInFront).renderInto(ms, vb)
    }

    companion object {
        private val RANDOM = RandomSource.create()
    }
}
