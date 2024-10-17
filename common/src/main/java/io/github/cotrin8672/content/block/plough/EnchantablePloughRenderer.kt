package io.github.cotrin8672.content.block.plough

import com.jozufozu.flywheel.util.transform.TransformStack
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.render.ContraptionMatrices
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer
import io.github.cotrin8672.config.Config
import io.github.cotrin8672.content.EnchantedRenderType
import io.github.cotrin8672.util.extension.use
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.util.RandomSource

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
            buffer.getBuffer(EnchantedRenderType.GLINT),
            ms.last().pose(),
            ms.last().normal(),
            0.007125f
        )
        ms.use {
            if (!Config.renderGlint.get()) return@use
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
                buffers.getBuffer(EnchantedRenderType.GLINT),
                matrices.viewProjection.last().pose(),
                matrices.viewProjection.last().normal(),
                0.0078125f
            )

            matrices.modelViewProjection.use {
                TransformStack.cast(matrices.modelViewProjection).translate(movementContext.localPos)
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
