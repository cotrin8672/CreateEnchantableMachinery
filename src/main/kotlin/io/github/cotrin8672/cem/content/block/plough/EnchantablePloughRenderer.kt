package io.github.cotrin8672.cem.content.block.plough

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.render.ContraptionMatrices
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer
import dev.engine_room.flywheel.lib.transform.TransformStack
import io.github.cotrin8672.cem.client.CustomRenderType
import io.github.cotrin8672.cem.config.CemConfig
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.util.RandomSource
import thedarkcolour.kotlinforforge.neoforge.forge.use

class EnchantablePloughRenderer(
    private val context: BlockEntityRendererProvider.Context,
) : SafeBlockEntityRenderer<EnchantablePloughBlockEntity>() {
    override fun renderSafe(
        be: EnchantablePloughBlockEntity,
        partialTicks: Float,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int,
    ) {
        val consumer = SheetedDecalTextureGenerator(
            buffer.getBuffer(CustomRenderType.GLINT),
            ms.last(),
            0.007125f
        )
        ms.use {
            if (!CemConfig.CONFIG.renderGlint.get()) return@use
            context.blockRenderDispatcher.renderBatched(
                be.blockState, be.blockPos, be.level!!, ms, consumer, true, RANDOM
            )
        }
    }

    companion object {
        private val RANDOM = RandomSource.create()

        fun renderInContraption(
            movementContext: MovementContext,
            matrices: ContraptionMatrices,
            buffers: MultiBufferSource,
        ) {
            val consumer = SheetedDecalTextureGenerator(
                buffers.getBuffer(CustomRenderType.GLINT),
                matrices.viewProjection.last(),
                0.0078125f
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
        }
    }
}
