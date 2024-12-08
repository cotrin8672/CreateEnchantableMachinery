package io.github.cotrin8672.createenchantablemachinery.content.block.drill

import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld
import com.jozufozu.flywheel.util.transform.TransformStack
import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.render.ContraptionMatrices
import com.simibubi.create.content.contraptions.render.ContraptionRenderDispatcher
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
import com.simibubi.create.content.kinetics.drill.DrillBlock
import com.simibubi.create.foundation.render.CachedBufferer
import com.simibubi.create.foundation.render.SuperByteBuffer
import com.simibubi.create.foundation.utility.AngleHelper
import com.simibubi.create.foundation.utility.AnimationTickHolder
import com.simibubi.create.foundation.utility.VecHelper
import io.github.cotrin8672.createenchantablemachinery.config.Config
import io.github.cotrin8672.createenchantablemachinery.content.EnchantedRenderType
import io.github.cotrin8672.createenchantablemachinery.util.CustomSheetedDecalTextureGenerator
import io.github.cotrin8672.createenchantablemachinery.util.extension.use
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.level.block.state.BlockState
import java.util.*

class EnchantableDrillRenderer(
    private val context: BlockEntityRendererProvider.Context,
) : KineticBlockEntityRenderer<EnchantableDrillBlockEntity>(context) {
    override fun getRotatedModel(be: EnchantableDrillBlockEntity, state: BlockState?): SuperByteBuffer? {
        return CachedBufferer.partialFacing(AllPartialModels.DRILL_HEAD, state)
    }

    override fun renderSafe(
        be: EnchantableDrillBlockEntity,
        partialTicks: Float,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int,
    ) {
        val consumer = CustomSheetedDecalTextureGenerator(
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
        private val RANDOM = Random()

        fun renderInContraption(
            movementContext: MovementContext,
            renderWorld: VirtualRenderWorld,
            matrices: ContraptionMatrices,
            buffer: MultiBufferSource,
        ) {
            val state = movementContext.state
            val superBuffer = CachedBufferer.partial(AllPartialModels.DRILL_HEAD, state)
            val facing = state.getValue(DrillBlock.FACING)

            val time = AnimationTickHolder.getRenderTime() / 20
            val consumer = CustomSheetedDecalTextureGenerator(
                buffer.getBuffer(EnchantedRenderType.GLINT),
                matrices.viewProjection.last().pose(),
                matrices.viewProjection.last().normal(),
                0.0078125f
            )

            superBuffer
                .transform(matrices.model)
                .centre()
                .rotateY(AngleHelper.horizontalAngle(facing).toDouble())
                .rotateX(AngleHelper.verticalAngle(facing).toDouble())
                .rotateZ(
                    ((time * (if (movementContext.contraption.stalled || !VecHelper.isVecPointingTowards(
                            movementContext.relativeMotion,
                            facing.opposite
                        )
                    ) movementContext.animationSpeed else 0f)) % 360).toDouble()
                )
                .unCentre()
                .light(
                    matrices.world,
                    ContraptionRenderDispatcher.getContraptionWorldLight(movementContext, renderWorld)
                )
                .renderInto(matrices.viewProjection, consumer)

            matrices.modelViewProjection.use {
                TransformStack.cast(matrices.modelViewProjection).translate(movementContext.localPos)
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
