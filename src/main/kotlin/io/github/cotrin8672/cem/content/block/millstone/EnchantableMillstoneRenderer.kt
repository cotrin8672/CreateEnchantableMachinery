package io.github.cotrin8672.cem.content.block.millstone

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator
import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
import dev.engine_room.flywheel.api.visualization.VisualizationManager
import io.github.cotrin8672.cem.client.CustomRenderType
import io.github.cotrin8672.cem.config.CemConfig
import io.github.cotrin8672.cem.util.nonNullLevel
import io.github.cotrin8672.cem.util.use
import net.createmod.catnip.render.CachedBuffers
import net.createmod.catnip.render.SuperByteBuffer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.state.BlockState

class EnchantableMillstoneRenderer(
    private val context: BlockEntityRendererProvider.Context,
) : KineticBlockEntityRenderer<EnchantableMillstoneBlockEntity>(context) {
    override fun getRotatedModel(be: EnchantableMillstoneBlockEntity, state: BlockState): SuperByteBuffer? {
        return CachedBuffers.partial(AllPartialModels.MILLSTONE_COG, state)
    }

    override fun renderSafe(
        be: EnchantableMillstoneBlockEntity,
        partialTicks: Float,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int,
    ) {
        val state = be.blockState

        super.renderSafe(be, partialTicks, ms, buffer, light, overlay)
        ms.use {
            if (CemConfig.CONFIG.renderGlint.get()) {
                val consumer = SheetedDecalTextureGenerator(
                    buffer.getBuffer(CustomRenderType.GLINT),
                    ms.last().pose(),
                    ms.last().normal(),
                    0.007125f
                )

                context.blockRenderDispatcher.renderBatched(
                    be.blockState, be.blockPos, be.nonNullLevel, ms, consumer, true, RANDOM
                )
                if (!VisualizationManager.supportsVisualization(be.nonNullLevel))
                    renderRotatingBuffer(be, getRotatedModel(be, state), ms, consumer, light)
            }
        }
    }

    companion object {
        private val RANDOM = RandomSource.create()
    }
}
