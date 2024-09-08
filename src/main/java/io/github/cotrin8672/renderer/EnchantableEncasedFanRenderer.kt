package io.github.cotrin8672.renderer

import com.jozufozu.flywheel.backend.Backend
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator
import com.mojang.blaze3d.vertex.VertexConsumer
import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
import com.simibubi.create.foundation.render.CachedBufferer
import com.simibubi.create.foundation.utility.AnimationTickHolder
import io.github.cotrin8672.blockentity.fan.EnchantableEncasedFanBlockEntity
import io.github.cotrin8672.config.Config
import io.github.cotrin8672.util.use
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.client.model.data.ModelData

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
        val consumer = SheetedDecalTextureGenerator(
            buffer.getBuffer(EnchantedRenderType.GLINT),
            ms.last().pose(),
            ms.last().normal(),
            0.007125f
        )
        ms.use {
            if (Config.renderGlint.get()) {
                context.blockRenderDispatcher.renderBatched(
                    be.blockState, be.blockPos, be.level!!, ms, consumer, true, RANDOM, ModelData.EMPTY, null
                )
                renderOrigin(be, ms, buffer, consumer)
            }
            renderOrigin(be, ms, buffer)
        }
    }

    private fun renderOrigin(
        be: EnchantableEncasedFanBlockEntity,
        ms: PoseStack,
        buffer: MultiBufferSource,
        consumer: VertexConsumer? = null,
    ) {
        if (Backend.canUseInstancing(be.level)) return

        val direction = be.blockState.getValue(BlockStateProperties.FACING)
        val vb = consumer ?: buffer.getBuffer(RenderType.cutoutMipped())

        val lightBehind = LevelRenderer.getLightColor(be.level!!, be.blockPos.relative(direction.opposite))
        val lightInFront = LevelRenderer.getLightColor(be.level!!, be.blockPos.relative(direction))

        val shaftHalf =
            CachedBufferer.partialFacing(AllPartialModels.SHAFT_HALF, be.blockState, direction.opposite)
        val fanInner =
            CachedBufferer.partialFacing(AllPartialModels.ENCASED_FAN_INNER, be.blockState, direction.opposite)

        val time = AnimationTickHolder.getRenderTime(be.level)
        var speed = be.speed * 5
        if (speed > 0) speed = Mth.clamp(speed, 80f, (64 * 20).toFloat())
        if (speed < 0) speed = Mth.clamp(speed, (-64 * 20).toFloat(), -80f)
        var angle = (time * speed * 3 / 10f) % 360
        angle = angle / 180f * Math.PI.toFloat()

        standardKineticRotationTransform(shaftHalf, be, lightBehind).renderInto(ms, vb)
        kineticRotationTransform(fanInner, be, direction.axis, angle, lightInFront).renderInto(ms, vb)
    }

    companion object {
        private val RANDOM = RandomSource.create()
    }
}
