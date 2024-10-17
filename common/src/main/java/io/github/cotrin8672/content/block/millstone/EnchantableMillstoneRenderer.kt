package io.github.cotrin8672.content.block.millstone

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator
import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
import com.simibubi.create.foundation.render.CachedBufferer
import com.simibubi.create.foundation.render.SuperByteBuffer
import io.github.cotrin8672.config.Config
import io.github.cotrin8672.content.EnchantedRenderType
import io.github.cotrin8672.util.extension.use
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.state.BlockState

class EnchantableMillstoneRenderer(
    private val context: BlockEntityRendererProvider.Context,
) : KineticBlockEntityRenderer<EnchantableMillstoneBlockEntity>(context) {
    override fun getRotatedModel(be: EnchantableMillstoneBlockEntity, state: BlockState?): SuperByteBuffer? {
        return CachedBufferer.partial(AllPartialModels.MILLSTONE_COG, state)
    }

    override fun renderSafe(
        be: EnchantableMillstoneBlockEntity,
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
    }
}
