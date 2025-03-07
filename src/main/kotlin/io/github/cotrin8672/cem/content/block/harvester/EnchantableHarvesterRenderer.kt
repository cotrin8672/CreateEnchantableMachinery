package io.github.cotrin8672.cem.content.block.harvester

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator
import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterBlock
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterRenderer
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.render.ContraptionMatrices
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld
import dev.engine_room.flywheel.lib.transform.TransformStack
import io.github.cotrin8672.cem.client.CustomRenderType
import io.github.cotrin8672.cem.config.CemConfig
import io.github.cotrin8672.cem.registry.PartialModelRegistration
import io.github.cotrin8672.cem.util.nonNullLevel
import net.createmod.catnip.math.VecHelper
import net.createmod.catnip.render.CachedBuffers
import net.createmod.catnip.render.SuperByteBuffer
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.util.RandomSource
import net.minecraft.world.phys.Vec3
import thedarkcolour.kotlinforforge.neoforge.forge.use

class EnchantableHarvesterRenderer(
    private val context: BlockEntityRendererProvider.Context,
) : SafeBlockEntityRenderer<EnchantableHarvesterBlockEntity>() {
    override fun renderSafe(
        be: EnchantableHarvesterBlockEntity,
        partialTicks: Float,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int,
    ) {
        val blockState = be.blockState
        val superBuffer = CachedBuffers.partial(PartialModelRegistration.ENCHANTABLE_HARVESTER_BLADE, blockState)

        val superBufferOriginal = CachedBuffers.partial(AllPartialModels.HARVESTER_BLADE, blockState)
        HarvesterRenderer.transform(
            be.nonNullLevel,
            blockState.getValue(HarvesterBlock.FACING),
            superBufferOriginal,
            be.animatedSpeed,
            PIVOT
        )
        superBufferOriginal.light<SuperByteBuffer>(light)
            .renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()))

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
                HarvesterRenderer.transform(
                    be.nonNullLevel,
                    blockState.getValue(HarvesterBlock.FACING),
                    superBuffer,
                    be.animatedSpeed,
                    PIVOT
                )
                superBuffer.light<SuperByteBuffer>(light).renderInto(ms, consumer)
            }
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
            val superBuffer = CachedBuffers.partial(PartialModelRegistration.ENCHANTABLE_HARVESTER_BLADE, blockState)
            val superBufferOriginal = CachedBuffers.partial(AllPartialModels.HARVESTER_BLADE, blockState)
            var speed = if (!VecHelper.isVecPointingTowards(movementContext.relativeMotion, facing.opposite))
                movementContext.animationSpeed else 0f
            if (movementContext.contraption.stalled) speed = 0f

            val consumer = SheetedDecalTextureGenerator(
                buffers.getBuffer(CustomRenderType.GLINT),
                matrices.model.last(),
                0.007125f
            )

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

            matrices.viewProjection.use {
                superBuffer.transform(matrices.model)
                HarvesterRenderer.transform(movementContext.world, facing, superBuffer, speed, PIVOT)
                superBuffer
                    .light<SuperByteBuffer>(LevelRenderer.getLightColor(renderWorld, movementContext.localPos))
                    .renderInto(matrices.viewProjection, consumer)

//                superBufferOriginal.transform(matrices.model)
//                HarvesterRenderer.transform(movementContext.world, facing, superBufferOriginal, speed, PIVOT)
//                superBufferOriginal
//                    .light<SuperByteBuffer>(LevelRenderer.getLightColor(renderWorld, movementContext.localPos))
//                    .renderInto(matrices.viewProjection, buffers.getBuffer(RenderType.cutout()))
            }
        }
    }
}
