package io.github.cotrin8672.renderer

import com.jozufozu.flywheel.backend.Backend
import com.jozufozu.flywheel.core.PartialModel
import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.render.ContraptionMatrices
import com.simibubi.create.content.contraptions.render.ContraptionRenderDispatcher
import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
import com.simibubi.create.content.kinetics.saw.SawBlock
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer
import com.simibubi.create.foundation.render.CachedBufferer
import com.simibubi.create.foundation.render.SuperByteBuffer
import com.simibubi.create.foundation.utility.AngleHelper
import com.simibubi.create.foundation.utility.VecHelper
import io.github.cotrin8672.blockentity.EnchantableSawBlockEntity
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Direction
import net.minecraft.util.Mth
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.Vec3
import kotlin.math.abs

open class EnchantableSawRenderer(context: BlockEntityRendererProvider.Context) :
    SafeBlockEntityRenderer<EnchantableSawBlockEntity>() {
    override fun renderSafe(
        be: EnchantableSawBlockEntity, partialTicks: Float, ms: PoseStack, buffer: MultiBufferSource, light: Int,
        overlay: Int,
    ) {
        renderBlade(be, ms, buffer, light)
        renderItems(be, partialTicks, ms, buffer, light, overlay)
        FilteringRenderer.renderOnBlockEntity(be, partialTicks, ms, buffer, light, overlay)

        if (Backend.canUseInstancing(be.level)) return

        renderShaft(be, ms, buffer, light, overlay)

    }

    private fun renderBlade(be: EnchantableSawBlockEntity, ms: PoseStack?, buffer: MultiBufferSource, light: Int) {
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

        val superBuffer = CachedBufferer.partialFacing(partial, blockState)
        if (rotate) {
            superBuffer.rotateCentered(Direction.UP, AngleHelper.rad(90.0))
        }
        superBuffer.color(0xFFFFFF)
            .light(light)
            .renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()))
    }

    protected fun renderShaft(
        be: EnchantableSawBlockEntity,
        ms: PoseStack?,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int,
    ) {
        KineticBlockEntityRenderer.renderRotatingBuffer(
            be, getRotatedModel(be), ms,
            buffer.getBuffer(RenderType.solid()), light
        )
    }

    protected fun renderItems(
        be: EnchantableSawBlockEntity, partialTicks: Float, ms: PoseStack, buffer: MultiBufferSource?,
        light: Int, overlay: Int,
    ) {
        val processingMode = be.blockState
            .getValue(SawBlock.FACING) == Direction.UP
        if (processingMode && !be.inventory.isEmpty) {
            val alongZ = !be.blockState
                .getValue(SawBlock.AXIS_ALONG_FIRST_COORDINATE)
            ms.pushPose()

            val moving = be.inventory.recipeDuration != 0f
            var offset = if (moving) be.inventory.remainingTime / be.inventory.recipeDuration else 0f
            val processingSpeed: Float = Mth.clamp(abs(be.speed) / 32, 1f, 128f)
            if (moving) {
                offset = Mth
                    .clamp(offset + ((-partialTicks + .5f) * processingSpeed) / be.inventory.recipeDuration, 0.125f, 1f)
                if (!be.inventory.appliedRecipe) offset += 1f
                offset /= 2f
            }

            if (be.speed == 0f) offset = .5f
            if ((be.speed < 0) xor alongZ) offset = 1 - offset

            for (i in 0 until be.inventory.slots) {
                val stack = be.inventory.getStackInSlot(i)
                if (stack.isEmpty) continue

                val itemRenderer = Minecraft.getInstance()
                    .itemRenderer
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

            ms.popPose()
        }
    }

    protected fun getRotatedModel(be: KineticBlockEntity): SuperByteBuffer {
        val state = be.blockState
        if (state.getValue(BlockStateProperties.FACING)
                .axis
                .isHorizontal
        ) return CachedBufferer.partialFacing(
            AllPartialModels.SHAFT_HALF,
            state.rotate(be.level, be.blockPos, Rotation.CLOCKWISE_180)
        )
        return CachedBufferer.block(
            KineticBlockEntityRenderer.KINETIC_BLOCK,
            getRenderedBlockState(be)
        )
    }

    protected fun getRenderedBlockState(be: KineticBlockEntity?): BlockState {
        return KineticBlockEntityRenderer.shaft(KineticBlockEntityRenderer.getRotationAxisOf(be))
    }

    companion object {
        fun renderInContraption(
            context: MovementContext, renderWorld: VirtualRenderWorld,
            matrices: ContraptionMatrices, buffer: MultiBufferSource,
        ) {
            val state = context.state
            val facing = state.getValue(SawBlock.FACING)

            var facingVec = Vec3.atLowerCornerOf(
                context.state.getValue(SawBlock.FACING)
                    .normal
            )
            facingVec = context.rotation.apply(facingVec)

            val closestToFacing = Direction.getNearest(facingVec.x, facingVec.y, facingVec.z)

            val horizontal = closestToFacing.axis
                .isHorizontal
            val backwards = VecHelper.isVecPointingTowards(context.relativeMotion, facing.opposite)
            val moving = context.animationSpeed != 0f
            val shouldAnimate =
                (context.contraption.stalled && horizontal) || (!context.contraption.stalled && !backwards && moving)
            val superBuffer = if (SawBlock.isHorizontal(state)) {
                if (shouldAnimate) CachedBufferer.partial(AllPartialModels.SAW_BLADE_HORIZONTAL_ACTIVE, state)
                else CachedBufferer.partial(AllPartialModels.SAW_BLADE_HORIZONTAL_INACTIVE, state)
            } else {
                if (shouldAnimate) CachedBufferer.partial(AllPartialModels.SAW_BLADE_VERTICAL_ACTIVE, state)
                else CachedBufferer.partial(AllPartialModels.SAW_BLADE_VERTICAL_INACTIVE, state)
            }

            superBuffer.transform(matrices.model)
                .centre()
                .rotateY(AngleHelper.horizontalAngle(facing).toDouble())
                .rotateX(AngleHelper.verticalAngle(facing).toDouble())

            if (!SawBlock.isHorizontal(state)) {
                superBuffer.rotateZ((if (state.getValue(SawBlock.AXIS_ALONG_FIRST_COORDINATE)) 90 else 0).toDouble())
            }

            superBuffer.unCentre()
                .light(matrices.world, ContraptionRenderDispatcher.getContraptionWorldLight(context, renderWorld))
                .renderInto(matrices.viewProjection, buffer.getBuffer(RenderType.cutoutMipped()))
        }
    }
}
