package io.github.cotrin8672.cem.content.block.drill

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator
import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.render.ContraptionMatrices
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
import com.simibubi.create.content.kinetics.drill.DrillBlock
import com.simibubi.create.content.kinetics.drill.DrillRenderer
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld
import dev.engine_room.flywheel.api.visualization.VisualizationManager
import dev.engine_room.flywheel.lib.transform.TransformStack
import io.github.cotrin8672.cem.client.CustomRenderType
import io.github.cotrin8672.cem.config.CemConfig
import io.github.cotrin8672.cem.util.nonNullLevel
import net.createmod.catnip.animation.AnimationTickHolder
import net.createmod.catnip.math.AngleHelper
import net.createmod.catnip.math.VecHelper
import net.createmod.catnip.render.CachedBuffers
import net.createmod.catnip.render.SuperByteBuffer
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.state.BlockState
import thedarkcolour.kotlinforforge.neoforge.forge.use

class EnchantableDrillRenderer(
    private val context: BlockEntityRendererProvider.Context,
) : KineticBlockEntityRenderer<EnchantableDrillBlockEntity>(context) {
    override fun getRotatedModel(be: EnchantableDrillBlockEntity, state: BlockState): SuperByteBuffer {
        return CachedBuffers.partialFacing(AllPartialModels.DRILL_HEAD, state)
    }

    override fun renderSafe(
        be: EnchantableDrillBlockEntity,
        partialTicks: Float,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int,
    ) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay)
        val consumer = SheetedDecalTextureGenerator(
            buffer.getBuffer(CustomRenderType.GLINT),
            ms.last(),
            0.007125f
        )
        val state = getRenderedBlockState(be)

        ms.use {
            if (CemConfig.CONFIG.renderGlint.get()) {
                context.blockRenderDispatcher.renderBatched(
                    be.blockState, be.blockPos, be.nonNullLevel, ms, consumer, true, RANDOM
                )

                if (!VisualizationManager.supportsVisualization(be.level))
                    renderRotatingBuffer(be, getRotatedModel(be, state), ms, consumer, light)
            }
        }
    }

    companion object {
        private val RANDOM = RandomSource.create()

        fun renderInContraption(
            context: MovementContext,
            renderWorld: VirtualRenderWorld,
            matrices: ContraptionMatrices,
            buffer: MultiBufferSource,
        ) {
            DrillRenderer.renderInContraption(context, renderWorld, matrices, buffer)
            val state = context.state
            val superBuffer = CachedBuffers.partial(AllPartialModels.DRILL_HEAD, state)
            val facing = state.getValue(DrillBlock.FACING)

            val consumer = SheetedDecalTextureGenerator(
                buffer.getBuffer(CustomRenderType.GLINT),
                matrices.viewProjection.last(),
                0.0078125f
            )

            val speed = if (
                context.contraption.stalled ||
                !VecHelper.isVecPointingTowards(context.relativeMotion, facing.opposite)
            ) context.animationSpeed else 0f
            val time = AnimationTickHolder.getRenderTime() / 20
            val angle = (time * speed) % 360

            superBuffer
                .transform(matrices.model)
                .center()
                .rotateYDegrees(AngleHelper.horizontalAngle(facing))
                .rotateXDegrees(AngleHelper.verticalAngle(facing))
                .rotateZDegrees(angle)
                .uncenter()
                .light<SuperByteBuffer>(LevelRenderer.getLightColor(renderWorld, context.localPos))
                .useLevelLight<SuperByteBuffer>(context.world, matrices.world)
                .renderInto(matrices.viewProjection, consumer)

            matrices.modelViewProjection.use {
                TransformStack.of(matrices.modelViewProjection).translate(context.localPos)
                Minecraft.getInstance().blockRenderer.renderBatched(
                    context.state,
                    context.localPos,
                    context.world,
                    matrices.modelViewProjection,
                    consumer,
                    true,
                    RANDOM
                )
            }
        }
    }
}
