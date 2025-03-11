package io.github.cotrin8672.cem.content.block.roller

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator
import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterRenderer
import com.simibubi.create.content.contraptions.actors.roller.RollerBlock
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.render.ContraptionMatrices
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld
import dev.engine_room.flywheel.lib.transform.TransformStack
import io.github.cotrin8672.cem.client.CustomRenderType
import io.github.cotrin8672.cem.config.CemConfig
import io.github.cotrin8672.cem.util.nonNullLevel
import net.createmod.catnip.math.AngleHelper
import net.createmod.catnip.math.VecHelper
import net.createmod.catnip.render.CachedBuffers
import net.createmod.catnip.render.SuperByteBuffer
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.client.model.data.ModelData
import thedarkcolour.kotlinforforge.neoforge.forge.use

class EnchantableRollerRenderer(
    private val context: BlockEntityRendererProvider.Context,
) : SmartBlockEntityRenderer<EnchantableRollerBlockEntity>(context) {
    override fun renderSafe(
        be: EnchantableRollerBlockEntity,
        partialTicks: Float,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int,
    ) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay)

        val blockState = be.blockState
        val facing = blockState.getValue(RollerBlock.FACING)
        val superBuffer = CachedBuffers.partial(AllPartialModels.ROLLER_WHEEL, blockState)

        ms.use {
            ms.translate(0.0, -0.25, 0.0)
            superBuffer.translate(Vec3.atLowerCornerOf(facing.normal).scale((17 / 16f).toDouble()))
            HarvesterRenderer.transform(be.level, facing, superBuffer, be.animatedSpeed, Vec3.ZERO)
            superBuffer.translate(0.0, -0.5, 0.5)
                .rotateYDegrees(90f)
                .light<SuperByteBuffer>(light)
                .renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()))
        }

        CachedBuffers.partial(AllPartialModels.ROLLER_FRAME, blockState)
            .rotateCentered(AngleHelper.rad((AngleHelper.horizontalAngle(facing) + 180.0)), Direction.UP)
            .light<SuperByteBuffer>(light)
            .renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()))

        ms.use {
            if (CemConfig.CONFIG.renderGlint.get()) {
                val consumer = SheetedDecalTextureGenerator(
                    buffer.getBuffer(CustomRenderType.GLINT),
                    ms.last(),
                    0.007125f
                )
                context.blockRenderDispatcher.renderBatched(
                    be.blockState, be.blockPos, be.nonNullLevel, ms, consumer, true, RANDOM, ModelData.EMPTY, null
                )
                ms.use {
                    ms.translate(0.0, -0.25, 0.0)
                    superBuffer.translate(Vec3.atLowerCornerOf(facing.normal).scale((17 / 16f).toDouble()))
                    HarvesterRenderer.transform(be.level, facing, superBuffer, be.animatedSpeed, Vec3.ZERO)
                    superBuffer.translate(0.0, -0.5, 0.5)
                        .rotateYDegrees(90f)
                        .light<SuperByteBuffer>(light)
                        .renderInto(ms, consumer)
                }
                CachedBuffers.partial(AllPartialModels.ROLLER_FRAME, blockState)
                    .rotateCentered(AngleHelper.rad((AngleHelper.horizontalAngle(facing) + 180.0)), Direction.UP)
                    .light<SuperByteBuffer>(light)
                    .renderInto(ms, consumer)
            }
        }
    }

    companion object {
        private val RANDOM = RandomSource.create()

        fun renderInContraption(
            context: MovementContext,
            renderWorld: VirtualRenderWorld,
            matrices: ContraptionMatrices,
            buffers: MultiBufferSource,
        ) {
            val blockState = context.state
            val facing = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
            val superBuffer = CachedBuffers.partial(AllPartialModels.ROLLER_WHEEL, blockState)
            var speed = if (!VecHelper.isVecPointingTowards(context.relativeMotion, facing.opposite))
                context.animationSpeed else 0f
            if (context.contraption.stalled) speed = 0f

            val viewProjection = matrices.viewProjection
            val contraptionWorldLight = LevelRenderer.getLightColor(renderWorld, context.localPos)
            val consumer = SheetedDecalTextureGenerator(
                buffers.getBuffer(CustomRenderType.GLINT),
                viewProjection.last(),
                0.007125f
            )

            matrices.modelViewProjection.use {
                if (CemConfig.CONFIG.renderGlint.get()) {
                    TransformStack.of(matrices.modelViewProjection).translate(context.localPos)
                    Minecraft.getInstance().blockRenderer.renderBatched(
                        context.state,
                        context.localPos,
                        context.world,
                        matrices.modelViewProjection,
                        consumer,
                        true,
                        RANDOM,
                        ModelData.EMPTY,
                        null
                    )
                }
            }

            viewProjection.use {
                if (CemConfig.CONFIG.renderGlint.get()) {
                    superBuffer
                        .transform(matrices.model)
                        .translate(Vec3.atLowerCornerOf(facing.normal).scale((17.0 / 16)))
                    HarvesterRenderer.transform(context.world, facing, superBuffer, speed, Vec3.ZERO)
                    viewProjection.translate(0.0, -0.25, 0.0)

                    superBuffer.translate(0.0, -0.5, 0.5)
                        .rotateYDegrees(90f)
                        .light<SuperByteBuffer>(contraptionWorldLight)
                        .renderInto(viewProjection, consumer)
                }
            }

            viewProjection.use {
                superBuffer
                    .transform(matrices.model)
                    .translate(Vec3.atLowerCornerOf(facing.normal).scale((17.0 / 16)))
                HarvesterRenderer.transform(context.world, facing, superBuffer, speed, Vec3.ZERO)
                viewProjection.translate(0.0, -0.25, 0.0)

                superBuffer.translate(0.0, -0.5, 0.5)
                    .rotateYDegrees(90f)
                    .light<SuperByteBuffer>(contraptionWorldLight)
                    .renderInto(viewProjection, buffers.getBuffer(RenderType.cutoutMipped()))
            }

            viewProjection.use {
                if (CemConfig.CONFIG.renderGlint.get())
                    CachedBuffers.partial(AllPartialModels.ROLLER_FRAME, blockState)
                        .transform(matrices.model)
                        .rotateCentered(
                            AngleHelper.rad((AngleHelper.horizontalAngle(facing) + 180.0)),
                            Direction.UP
                        )
                        .light<SuperByteBuffer>(contraptionWorldLight)
                        .renderInto(viewProjection, consumer)

                CachedBuffers.partial(AllPartialModels.ROLLER_FRAME, blockState)
                    .transform(matrices.model)
                    .rotateCentered(AngleHelper.rad((AngleHelper.horizontalAngle(facing) + 180.0)), Direction.UP)
                    .light<SuperByteBuffer>(contraptionWorldLight)
                    .renderInto(viewProjection, buffers.getBuffer(RenderType.cutoutMipped()))
            }
        }
    }
}
