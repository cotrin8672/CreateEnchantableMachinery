package io.github.cotrin8672.cem.content.block.crusher

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
import io.github.cotrin8672.cem.client.CustomRenderType
import io.github.cotrin8672.cem.config.CemConfig
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import thedarkcolour.kotlinforforge.neoforge.forge.use

class EnchantableCrushingWheelRenderer(
    context: BlockEntityRendererProvider.Context,
) : KineticBlockEntityRenderer<EnchantableCrushingWheelBlockEntity>(context) {
    override fun renderSafe(
        be: EnchantableCrushingWheelBlockEntity,
        partialTicks: Float,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int,
    ) {
        val state = getRenderedBlockState(be)

        ms.use {
            super.renderSafe(be, partialTicks, ms, buffer, light, overlay)
            if (CemConfig.CONFIG.renderGlint.get()) {
                val consumer = SheetedDecalTextureGenerator(
                    buffer.getBuffer(CustomRenderType.GLINT),
                    ms.last(),
                    0.007125f
                )
                renderRotatingBuffer(be, getRotatedModel(be, state), ms, consumer, light)
            }
        }
    }
}
