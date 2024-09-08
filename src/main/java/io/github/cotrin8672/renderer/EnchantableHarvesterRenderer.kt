package io.github.cotrin8672.renderer

import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld
import com.jozufozu.flywheel.util.transform.TransformStack
import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterBlock
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterRenderer
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.render.ContraptionMatrices
import com.simibubi.create.content.contraptions.render.ContraptionRenderDispatcher
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer
import com.simibubi.create.foundation.render.CachedBufferer
import com.simibubi.create.foundation.utility.VecHelper
import io.github.cotrin8672.blockentity.EnchantableHarvesterBlockEntity
import io.github.cotrin8672.config.Config
import io.github.cotrin8672.registry.PartialModelRegistration
import io.github.cotrin8672.util.use
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.util.RandomSource
import net.minecraft.world.phys.Vec3
import net.minecraftforge.client.model.data.ModelData

class EnchantableHarvesterRenderer(
    private val context: BlockEntityRendererProvider.Context,
) : SafeBlockEntityRenderer<EnchantableHarvesterBlockEntity>() {
    override fun renderSafe(
        be: EnchantableHarvesterBlockEntity?,
        partialTicks: Float,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int,
    ) {
        val blockState = be!!.blockState
        val superBuffer = CachedBufferer.partial(PartialModelRegistration.ENCHANTABLE_HARVESTER_BLADE, blockState)
        val consumer = CustomSheetedDecalTextureGenerator(
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
                HarvesterRenderer.transform(
                    be.level!!,
                    blockState.getValue(HarvesterBlock.FACING),
                    superBuffer,
                    be.animatedSpeed,
                    PIVOT
                )
                superBuffer.light(light).renderInto(ms, consumer)
            }
            val superBufferOriginal = CachedBufferer.partial(AllPartialModels.HARVESTER_BLADE, blockState)
            HarvesterRenderer.transform(
                be.level!!,
                blockState.getValue(HarvesterBlock.FACING),
                superBufferOriginal,
                be.animatedSpeed,
                PIVOT
            )
            superBufferOriginal.light(light).renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()))
        }
    }

    companion object {
        private val PIVOT = Vec3(0.0, 6.0, 9.0)
        private val RANDOM = RandomSource.create()

        fun renderInContraption(
            movementContext: MovementContext,
            renderWorld: VirtualRenderWorld,
            matrices: ContraptionMatrices,
            buffers: MultiBufferSource,
        ) {
            val blockState = movementContext.state
            val facing = blockState.getValue(HORIZONTAL_FACING)
            val superBuffer = CachedBufferer.partial(PartialModelRegistration.ENCHANTABLE_HARVESTER_BLADE, blockState)
            val superBufferOriginal = CachedBufferer.partial(AllPartialModels.HARVESTER_BLADE, blockState)
            var speed = if (!VecHelper.isVecPointingTowards(movementContext.relativeMotion, facing.opposite))
                movementContext.animationSpeed else 0f
            if (movementContext.contraption.stalled) speed = 0f

            val consumer = CustomSheetedDecalTextureGenerator(
                buffers.getBuffer(EnchantedRenderType.GLINT),
                matrices.viewProjection.last().pose(),
                matrices.viewProjection.last().normal(),
                0.0078125f
            )

            matrices.modelViewProjection.use {
                TransformStack.cast(matrices.modelViewProjection).translate(movementContext.localPos)
                Minecraft.getInstance().blockRenderer.renderBatched(
                    movementContext.state,
                    movementContext.localPos,
                    movementContext.world,
                    matrices.modelViewProjection,
                    consumer,
                    true,
                    RANDOM,
                    ModelData.EMPTY,
                    null
                )
            }

            matrices.viewProjection.use {
                superBuffer.transform(matrices.model)
                HarvesterRenderer.transform(movementContext.world, facing, superBuffer, speed, PIVOT)
                superBuffer
                    .light(
                        matrices.world,
                        ContraptionRenderDispatcher.getContraptionWorldLight(movementContext, renderWorld)
                    )
                    .renderInto(matrices.viewProjection, consumer)

                superBufferOriginal.transform(matrices.model)
                HarvesterRenderer.transform(movementContext.world, facing, superBufferOriginal, speed, PIVOT)
                superBufferOriginal
                    .light(
                        matrices.world,
                        ContraptionRenderDispatcher.getContraptionWorldLight(movementContext, renderWorld)
                    )
                    .renderInto(matrices.viewProjection, buffers.getBuffer(RenderType.cutout()))
            }
        }
    }
}
