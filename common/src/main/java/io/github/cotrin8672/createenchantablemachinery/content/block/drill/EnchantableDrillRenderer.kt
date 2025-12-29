package io.github.cotrin8672.createenchantablemachinery.content.block.drill

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator
import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.render.ContraptionMatrices
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
import com.simibubi.create.content.kinetics.drill.DrillBlock
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld
import dev.engine_room.flywheel.lib.transform.TransformStack
import io.github.cotrin8672.createenchantablemachinery.config.Config
import io.github.cotrin8672.createenchantablemachinery.content.EnchantedRenderType
import io.github.cotrin8672.createenchantablemachinery.util.SuperBufferUtil
import io.github.cotrin8672.createenchantablemachinery.util.extension.use
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

class EnchantableDrillRenderer(
    private val context: BlockEntityRendererProvider.Context,
) : KineticBlockEntityRenderer<EnchantableDrillBlockEntity>(context) {
    override fun getRotatedModel(be: EnchantableDrillBlockEntity, state: BlockState?): SuperByteBuffer? {
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
            buffer.getBuffer(EnchantedRenderType.GLINT),
            ms.last().pose(),
            ms.last().normal(),
            0.007125f
        )
        val state = getRenderedBlockState(be)

        ms.use {
            if (Config.renderGlint.get()) {
                context.blockRenderDispatcher.renderBatched(
                    be.blockState, be.blockPos, be.level!!, ms, consumer, true, RANDOM
                )
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
            val state = movementContext.state
            val facing = state.getValue(DrillBlock.FACING)
            val superBuffer =
                CachedBuffers.partial(AllPartialModels.DRILL_HEAD, state) as SuperByteBuffer
            val time = AnimationTickHolder.getRenderTime() / 20
            val consumer = SheetedDecalTextureGenerator(
                buffer.getBuffer(EnchantedRenderType.GLINT),
                matrices.viewProjection.last().pose(),
                matrices.viewProjection.last().normal(),
                0.0078125f
            )

            superBuffer.transform(matrices.model.last())
                .center()
                .rotateY(AngleHelper.horizontalAngle(facing))
                .rotateX(AngleHelper.verticalAngle(facing))
                .rotateZ(
                    (
                            time * if (
                                movementContext.contraption.stalled ||
                                !VecHelper.isVecPointingTowards(
                                    movementContext.relativeMotion,
                                    facing.opposite
                                )
                            ) movementContext.animationSpeed else 0f
                            ) % 360f
                )
                .uncenter()

            SuperBufferUtil.applyPackedLight(
                superBuffer,
                LevelRenderer.getLightColor(renderWorld, movementContext.localPos)
            )
            superBuffer.renderInto(matrices.viewProjection, consumer)

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
