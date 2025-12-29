package io.github.cotrin8672.createenchantablemachinery.content.block.roller

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
import io.github.cotrin8672.createenchantablemachinery.config.Config
import io.github.cotrin8672.createenchantablemachinery.content.EnchantedRenderType
import io.github.cotrin8672.createenchantablemachinery.util.SuperBufferUtil
import io.github.cotrin8672.createenchantablemachinery.util.extension.use
import net.createmod.catnip.math.AngleHelper
import net.createmod.catnip.math.VecHelper
import net.createmod.catnip.render.CachedBuffers
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.Vec3

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
        val wheel = CachedBuffers.partial(AllPartialModels.ROLLER_WHEEL, blockState)

        ms.use {
            translate(0.0, -0.25, 0.0)

            wheel.translate(Vec3.atLowerCornerOf(facing.normal).scale((17 / 16f).toDouble()))
            HarvesterRenderer.transform(be.level, facing, wheel, be.animatedSpeed, Vec3.ZERO)
            wheel.translate(0.0, -0.5, 0.5)
            wheel.rotateY(90.0F)
            SuperBufferUtil.applyPackedLight(wheel, light)
            wheel.renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()))
        }

        val frame = CachedBuffers.partial(AllPartialModels.ROLLER_FRAME, blockState)
        frame.rotateCentered(
            AngleHelper.rad(AngleHelper.horizontalAngle(facing) + 180.0),
            Direction.UP
        )
        SuperBufferUtil.applyPackedLight(frame, light)
        frame.renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()))

        ms.use {
            if (Config.renderGlint.get()) {
                val consumer = SheetedDecalTextureGenerator(
                    buffer.getBuffer(EnchantedRenderType.GLINT),
                    ms.last().pose(),
                    ms.last().normal(),
                    0.007125f
                )

                context.blockRenderDispatcher.renderBatched(
                    be.blockState,
                    be.blockPos,
                    be.level!!,
                    ms,
                    consumer,
                    true,
                    RANDOM
                )

                ms.use {
                    translate(0.0, -0.25, 0.0)

                    wheel.translate(Vec3.atLowerCornerOf(facing.normal).scale((17 / 16f).toDouble()))
                    HarvesterRenderer.transform(be.level, facing, wheel, be.animatedSpeed, Vec3.ZERO)
                    wheel.translate(0.0, -0.5, 0.5)
                    wheel.rotateY(90.0F)
                    SuperBufferUtil.applyPackedLight(wheel, light)
                    wheel.renderInto(ms, consumer)
                }

                val glintFrame = CachedBuffers.partial(AllPartialModels.ROLLER_FRAME, blockState)
                glintFrame.rotateCentered(
                    AngleHelper.rad(AngleHelper.horizontalAngle(facing) + 180.0),
                    Direction.UP
                )
                SuperBufferUtil.applyPackedLight(glintFrame, light)
                glintFrame.renderInto(ms, consumer)
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
            val wheel = CachedBuffers.partial(AllPartialModels.ROLLER_WHEEL, blockState)

            var speed =
                if (!VecHelper.isVecPointingTowards(context.relativeMotion, facing.opposite))
                    context.animationSpeed
                else 0f

            if (context.contraption.stalled)
                speed = 0f

            val viewProjection = matrices.viewProjection

            val consumer = SheetedDecalTextureGenerator(
                buffers.getBuffer(EnchantedRenderType.GLINT),
                viewProjection.last().pose(),
                viewProjection.last().normal(),
                0.007125f
            )

            matrices.modelViewProjection.use {
                TransformStack.of(matrices.modelViewProjection)
                    .translate(context.localPos)

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

            viewProjection.use {
                wheel.transform(matrices.model)
                wheel.translate(Vec3.atLowerCornerOf(facing.normal).scale(17.0 / 16.0))
                HarvesterRenderer.transform(context.world, facing, wheel, speed, Vec3.ZERO)
                translate(0.0, -0.25, 0.0)
                wheel.translate(0.0, -0.5, 0.5)
                wheel.rotateY(90.0F)
                SuperBufferUtil.applyWorldLight(wheel, context.world, matrices.world)
                wheel.renderInto(this, consumer)
            }

            viewProjection.use {
                wheel.transform(matrices.model)
                wheel.translate(Vec3.atLowerCornerOf(facing.normal).scale(17.0 / 16.0))
                HarvesterRenderer.transform(context.world, facing, wheel, speed, Vec3.ZERO)
                translate(0.0, -0.25, 0.0)
                wheel.translate(0.0, -0.5, 0.5)
                wheel.rotateY(90.0F)
                SuperBufferUtil.applyWorldLight(wheel, context.world, matrices.world)
                wheel.renderInto(this, buffers.getBuffer(RenderType.cutoutMipped()))
            }

            viewProjection.use {
                val frame = CachedBuffers.partial(AllPartialModels.ROLLER_FRAME, blockState)
                frame.transform(matrices.model)
                frame.rotateCentered(
                    AngleHelper.rad(AngleHelper.horizontalAngle(facing) + 180.0),
                    Direction.UP
                )
                SuperBufferUtil.applyWorldLight(frame, context.world, matrices.world)
                frame.renderInto(viewProjection, consumer)

                val frameCutout = CachedBuffers.partial(AllPartialModels.ROLLER_FRAME, blockState)
                frameCutout.transform(matrices.model)
                frameCutout.rotateCentered(
                    AngleHelper.rad(AngleHelper.horizontalAngle(facing) + 180.0),
                    Direction.UP
                )
                SuperBufferUtil.applyWorldLight(frame, context.world, matrices.world)
                frameCutout.renderInto(viewProjection, buffers.getBuffer(RenderType.cutoutMipped()))
            }
        }
    }
}
