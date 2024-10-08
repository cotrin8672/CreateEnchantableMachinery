package io.github.cotrin8672.renderer

import com.jozufozu.flywheel.backend.Backend
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator
import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
import com.simibubi.create.foundation.render.CachedBufferer
import io.github.cotrin8672.blockentity.EnchantableMechanicalPressBlockEntity
import io.github.cotrin8672.config.Config
import io.github.cotrin8672.util.use
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.client.model.data.ModelData

class EnchantableMechanicalPressRenderer(
    private val context: Context,
) : KineticBlockEntityRenderer<EnchantableMechanicalPressBlockEntity>(context) {
    override fun shouldRenderOffScreen(be: EnchantableMechanicalPressBlockEntity): Boolean {
        return true
    }

    override fun renderSafe(
        be: EnchantableMechanicalPressBlockEntity,
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
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay)
        val blockState = be.blockState
        val headModel = CachedBufferer.partialFacing(
            AllPartialModels.MECHANICAL_PRESS_HEAD,
            blockState,
            blockState.getValue(HORIZONTAL_FACING)
        )
        val pressingBehaviour = be.getPressingBehaviour()
        val renderedHeadOffset =
            pressingBehaviour.getRenderedHeadOffset(partialTicks) * pressingBehaviour.mode.headOffset
        if (!Backend.canUseInstancing(be.level)) {
            headModel
                .translate(0.0, -renderedHeadOffset.toDouble(), 0.0)
                .light(light)
                .renderInto(ms, buffer.getBuffer(RenderType.solid()))
        }

        ms.use {
            if (Config.renderGlint.get()) {
                context.blockRenderDispatcher.renderBatched(
                    be.blockState, be.blockPos, be.level!!, ms, consumer, true, Random, ModelData.EMPTY, null
                )
                headModel
                    .translate(0.0, -renderedHeadOffset.toDouble(), 0.0)
                    .light(light)
                    .renderInto(ms, consumer)
            }
        }
    }

    override fun getRenderedBlockState(be: EnchantableMechanicalPressBlockEntity?): BlockState {
        return shaft(getRotationAxisOf(be))
    }

    companion object {
        private val Random = RandomSource.create()
    }
}
