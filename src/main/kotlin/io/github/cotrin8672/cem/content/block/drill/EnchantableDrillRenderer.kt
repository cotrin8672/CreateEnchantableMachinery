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
    override fun getRotatedModel(be: EnchantableDrillBlockEntity, state: BlockState): SuperByteBuffer? {
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
        val consumer = SheetedDecalTextureGenerator(
            buffer.getBuffer(CustomRenderType.GLINT),
            ms.last(),
            0.007125f
        )
        val state = getRenderedBlockState(be)

        ms.use {
            if (CemConfig.CONFIG.renderGlint.get()) {
                context.blockRenderDispatcher.renderBatched(
                    be.blockState, be.blockPos, be.level!!, ms, consumer, true, RANDOM
                )

                if (!VisualizationManager.supportsVisualization(be.level))
                    renderRotatingBuffer(be, getRotatedModel(be, state), ms, consumer, light)
            }
            super.renderSafe(be, partialTicks, ms, buffer, light, overlay)
        }
    }

    companion object {
        private val RANDOM = RandomSource.create()

        fun renderInContraption(
            movementContext: MovementContext,
            renderWorld: VirtualRenderWorld,
            matrices: ContraptionMatrices,
            buffer: MultiBufferSource,
        ) {
            DrillRenderer.renderInContraption(movementContext, renderWorld, matrices, buffer)
            val state = movementContext.state
            val superBuffer = CachedBuffers.partial(AllPartialModels.DRILL_HEAD, state)
            val facing = state.getValue(DrillBlock.FACING)

            val consumer = SheetedDecalTextureGenerator(
                buffer.getBuffer(CustomRenderType.GLINT),
                matrices.viewProjection.last(),
                0.0078125f
            )

            val speed = if (
                movementContext.contraption.stalled ||
                !VecHelper.isVecPointingTowards(movementContext.relativeMotion, facing.opposite)
            ) movementContext.animationSpeed else 0f
            val time = AnimationTickHolder.getRenderTime() / 20
            val angle = (time * speed) % 360

            superBuffer
                .transform(matrices.model)
                .center()
                .rotateYDegrees(AngleHelper.horizontalAngle(facing))
                .rotateXDegrees(AngleHelper.verticalAngle(facing))
                .rotateZDegrees(angle)
                .uncenter()
                .light<SuperByteBuffer>(LevelRenderer.getLightColor(renderWorld, movementContext.localPos))
                .useLevelLight<SuperByteBuffer>(movementContext.world, matrices.world)
                .renderInto(matrices.viewProjection, consumer)

            matrices.modelViewProjection.use {
                TransformStack.of(matrices.modelViewProjection).translate(movementContext.localPos)
                Minecraft.getInstance().blockRenderer.renderBatched(
                    movementContext.state,
                    movementContext.localPos,
                    movementContext.world,
                    matrices.modelViewProjection,
                    consumer,
                    true,
                    RANDOM
                )
            }
        }
    }
}
