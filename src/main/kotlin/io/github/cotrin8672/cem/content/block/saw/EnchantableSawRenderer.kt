package io.github.cotrin8672.cem.content.block.saw

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator
import com.mojang.math.Axis
import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.render.ContraptionMatrices
import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer.KINETIC_BLOCK
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer.renderRotatingBuffer
import com.simibubi.create.content.kinetics.saw.SawBlock
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld
import dev.engine_room.flywheel.api.visualization.VisualizationManager
import dev.engine_room.flywheel.lib.model.baked.PartialModel
import io.github.cotrin8672.cem.client.CustomRenderType
import io.github.cotrin8672.cem.config.CemConfig
import io.github.cotrin8672.cem.registry.PartialModelRegistration
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
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.Vec3
import thedarkcolour.kotlinforforge.neoforge.forge.use
import kotlin.math.abs

class EnchantableSawRenderer(
    private val context: BlockEntityRendererProvider.Context,
) : SafeBlockEntityRenderer<EnchantableSawBlockEntity>() {
    private fun getRotatedModel(be: EnchantableSawBlockEntity): SuperByteBuffer {
        val state = be.blockState
        if (state.getValue(BlockStateProperties.FACING).axis.isHorizontal)
            return CachedBuffers.partialFacing(
                AllPartialModels.SHAFT_HALF,
                state.rotate(be.level, be.blockPos, Rotation.CLOCKWISE_180)
            )
        return CachedBuffers.block(KINETIC_BLOCK, getRenderedBlockState(be))
    }

    private fun getRenderedBlockState(be: KineticBlockEntity?): BlockState {
        return KineticBlockEntityRenderer.shaft(KineticBlockEntityRenderer.getRotationAxisOf(be))
    }

    override fun renderSafe(
        be: EnchantableSawBlockEntity,
        partialTicks: Float,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int,
    ) {
        renderBlade(be, ms, buffer, light)
        renderItems(be, partialTicks, ms, buffer, light, overlay)
        FilteringRenderer.renderOnBlockEntity(be, partialTicks, ms, buffer, light, overlay)

        ms.use {
            if (CemConfig.CONFIG.renderGlint.get()) {
                val consumer = SheetedDecalTextureGenerator(
                    buffer.getBuffer(CustomRenderType.GLINT),
                    ms.last(),
                    0.007125f
                )

                context.blockRenderDispatcher.renderBatched(
                    be.blockState, be.blockPos, be.level, ms, consumer, true, RANDOM
                )
            }
        }

        if (!VisualizationManager.supportsVisualization(be.level)) {
            renderShaft(be, ms, buffer, light)
            if (CemConfig.CONFIG.renderGlint.get()) {
                val consumer = SheetedDecalTextureGenerator(
                    buffer.getBuffer(CustomRenderType.GLINT),
                    ms.last(),
                    0.007125f
                )
                renderShaft(be, ms, buffer, light)
            }
        }
    }

    private fun renderBlade(
        be: EnchantableSawBlockEntity,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
    ) {
        val blockState = be.blockState
        val partial: PartialModel
        val speed = be.speed
        var rotate = false

        if (SawBlock.isHorizontal(blockState)) {
            partial = if (speed > 0) {
                AllPartialModels.SAW_BLADE_HORIZONTAL_ACTIVE
            } else if (speed < 0) {
                AllPartialModels.SAW_BLADE_HORIZONTAL_REVERSED
            } else {
                AllPartialModels.SAW_BLADE_HORIZONTAL_INACTIVE
            }
        } else {
            partial = if (be.speed > 0) {
                AllPartialModels.SAW_BLADE_VERTICAL_ACTIVE
            } else if (speed < 0) {
                AllPartialModels.SAW_BLADE_VERTICAL_REVERSED
            } else {
                AllPartialModels.SAW_BLADE_VERTICAL_INACTIVE
            }

            if (blockState.getValue(SawBlock.AXIS_ALONG_FIRST_COORDINATE)) rotate = true
        }
        val enchantedBlade = PartialModelRegistration.ENCHANTABLE_SAW_BLADE

        val superBuffer = CachedBuffers.partialFacing(partial, blockState)
        val enchantedSuperBuffer = CachedBuffers.partialFacing(enchantedBlade, blockState)
        if (rotate) {
            superBuffer.rotateCentered(AngleHelper.rad(90.0), Direction.UP)
            enchantedSuperBuffer.rotateCentered(AngleHelper.rad(90.0), Direction.UP)
        }
        superBuffer
            .color<SuperByteBuffer>(0xFFFFFF)
            .light<SuperByteBuffer>(light)
            .renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()))

        val consumer = SheetedDecalTextureGenerator(
            buffer.getBuffer(CustomRenderType.GLINT),
            ms.last(),
            0.007125f
        )

        enchantedSuperBuffer
            .color<SuperByteBuffer>(0xFFFFFF)
            .light<SuperByteBuffer>(light)
            .renderInto(ms, consumer)
    }

    private fun renderShaft(
        be: EnchantableSawBlockEntity,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
    ) {
        renderRotatingBuffer(be, getRotatedModel(be), ms, buffer.getBuffer(RenderType.solid()), light)
    }

    private fun renderItems(
        be: EnchantableSawBlockEntity,
        partialTicks: Float,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int,
    ) {
        val processingMode = be.blockState.getValue(SawBlock.FACING) == Direction.UP
        if (processingMode && !be.inventory.isEmpty) {
            val alongZ = !be.blockState.getValue(SawBlock.AXIS_ALONG_FIRST_COORDINATE)

            ms.use {
                val moving = be.inventory.recipeDuration != 0f
                var offset = if (moving) be.inventory.remainingTime / be.inventory.recipeDuration else 0f
                val processingSpeed: Float = Mth.clamp(abs(be.speed) / 32, 1f, 128f)
                if (moving) {
                    offset = Mth.clamp(
                        offset + ((-partialTicks + .5f) * processingSpeed) / be.inventory.recipeDuration,
                        0.125f,
                        1f
                    )
                    if (!be.inventory.appliedRecipe) offset += 1f
                    offset /= 2f
                }

                if (be.speed == 0f) offset = .5f
                if ((be.speed < 0) xor alongZ) offset = 1 - offset

                for (i in 0 until be.inventory.slots) {
                    val stack = be.inventory.getStackInSlot(i)
                    if (stack.isEmpty) continue

                    val itemRenderer = Minecraft.getInstance().itemRenderer
                    val modelWithOverrides = itemRenderer.getModel(stack, be.level, null, 0)
                    val blockItem = modelWithOverrides.isGui3d

                    ms.translate(
                        if (alongZ) offset.toDouble() else .5,
                        (if (blockItem) .925f else 13f / 16f).toDouble(),
                        if (alongZ) .5 else offset.toDouble()
                    )

                    ms.scale(.5f, .5f, .5f)
                    if (alongZ) ms.mulPose(Axis.YP.rotationDegrees(90f))
                    ms.mulPose(Axis.XP.rotationDegrees(90f))
                    itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, light, overlay, ms, buffer, be.level, 0)
                    break
                }
            }
        }
    }

    companion object {
        private val RANDOM: RandomSource = RandomSource.create()

        fun renderInContraption(
            context: MovementContext,
            renderWorld: VirtualRenderWorld,
            matrices: ContraptionMatrices,
            buffer: MultiBufferSource,
        ) {
            val state = context.state
            val facing = state.getValue(SawBlock.FACING)

            val facingVec = context.rotation.apply(Vec3.atLowerCornerOf(context.state.getValue(SawBlock.FACING).normal))

            val closestToFacing = Direction.getNearest(facingVec.x, facingVec.y, facingVec.z)

            val horizontal = closestToFacing.axis.isHorizontal
            val backwards = VecHelper.isVecPointingTowards(context.relativeMotion, facing.opposite)
            val moving = context.animationSpeed != 0f
            val shouldAnimate =
                (context.contraption.stalled && horizontal) || (!context.contraption.stalled && !backwards && moving)
            val superBuffer = if (SawBlock.isHorizontal(state)) {
                if (shouldAnimate) CachedBuffers.partial(AllPartialModels.SAW_BLADE_HORIZONTAL_ACTIVE, state)
                else CachedBuffers.partial(AllPartialModels.SAW_BLADE_HORIZONTAL_INACTIVE, state)
            } else {
                if (shouldAnimate) CachedBuffers.partial(AllPartialModels.SAW_BLADE_VERTICAL_ACTIVE, state)
                else CachedBuffers.partial(AllPartialModels.SAW_BLADE_VERTICAL_INACTIVE, state)
            }
            val enchantedSuperBuffer = CachedBuffers.partial(PartialModelRegistration.ENCHANTABLE_SAW_BLADE, state)

            superBuffer.transform(matrices.model)
                .center()
                .rotateYDegrees(AngleHelper.horizontalAngle(facing))
                .rotateXDegrees(AngleHelper.verticalAngle(facing))

            enchantedSuperBuffer.transform(matrices.model)
                .center()
                .rotateYDegrees(AngleHelper.horizontalAngle(facing))
                .rotateXDegrees(AngleHelper.verticalAngle(facing))

            if (!SawBlock.isHorizontal(state)) {
                superBuffer.rotateZDegrees((if (state.getValue(SawBlock.AXIS_ALONG_FIRST_COORDINATE)) 90f else 0f))
                enchantedSuperBuffer.rotateZDegrees((if (state.getValue(SawBlock.AXIS_ALONG_FIRST_COORDINATE)) 90f else 0f))
            }

            superBuffer.uncenter()
                .light<SuperByteBuffer>(LevelRenderer.getLightColor(renderWorld, context.localPos))
                .useLevelLight<SuperByteBuffer>(context.world, matrices.world)
                .renderInto(matrices.viewProjection, buffer.getBuffer(RenderType.cutoutMipped()))

            val consumer = SheetedDecalTextureGenerator(
                buffer.getBuffer(CustomRenderType.GLINT),
                matrices.model.last(),
                0.007125f
            )

            enchantedSuperBuffer.uncenter()
                .light<SuperByteBuffer>(LevelRenderer.getLightColor(renderWorld, context.localPos))
                .useLevelLight<SuperByteBuffer>(context.world, matrices.world)
                .renderInto(matrices.viewProjection, consumer)
        }
    }
}
