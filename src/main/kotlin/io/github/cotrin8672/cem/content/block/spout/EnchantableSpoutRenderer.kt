package io.github.cotrin8672.cem.content.block.spout

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator
import com.simibubi.create.AllPartialModels
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer
import com.simibubi.create.foundation.fluid.FluidRenderer
import io.github.cotrin8672.cem.client.CustomRenderType
import io.github.cotrin8672.cem.config.CemConfig
import io.github.cotrin8672.cem.util.nonNullLevel
import io.github.cotrin8672.cem.util.use
import net.createmod.catnip.render.CachedBuffers
import net.createmod.catnip.render.SuperByteBuffer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.phys.AABB
import net.neoforged.neoforge.client.model.data.ModelData
import kotlin.math.max
import kotlin.math.pow

class EnchantableSpoutRenderer(
    private val context: BlockEntityRendererProvider.Context,
) : SafeBlockEntityRenderer<EnchantableSpoutBlockEntity>() {
    override fun renderSafe(
        be: EnchantableSpoutBlockEntity,
        partialTicks: Float,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int,
    ) {
        if (CemConfig.CONFIG.renderGlint.get()) {
            val consumer = SheetedDecalTextureGenerator(
                buffer.getBuffer(CustomRenderType.GLINT),
                ms.last(),
                0.007125f
            )
            context.blockRenderDispatcher.renderBatched(
                be.blockState, be.blockPos, be.nonNullLevel, ms, consumer, true, RANDOM, ModelData.EMPTY, null
            )
        }

        val tank = be.fluidTank

        val primaryTank = tank.primaryTank
        val fluidStack = tank.primaryTank.renderedFluid
        var level = primaryTank.fluidLevel.getValue(partialTicks)

        val processingTicks = be.processingTicks
        val processingPT = processingTicks.toFloat() - partialTicks
        var processingProgress = 1 - (processingPT - be.executionTick) / (be.enchantedFillingTime - be.executionTick)
        processingProgress = Mth.clamp(processingProgress, 0f, 1f)
        var radius = 0f

        if (processingTicks != -1) {
            radius = (((2 * processingProgress) - 1).toDouble().pow(2.0) - 1).toFloat()
            val bb = AABB(0.5, 0.5, 0.5, 0.5, -1.2, 0.5).inflate((radius / 32f).toDouble())
            ms.use {
                FluidRenderer.renderFluidBox(
                    fluidStack.fluid,
                    fluidStack.amount.toLong(),
                    bb.minX.toFloat(),
                    bb.minY.toFloat(),
                    bb.minZ.toFloat(),
                    bb.maxX.toFloat(),
                    bb.maxY.toFloat(),
                    bb.maxZ.toFloat(),
                    buffer,
                    ms,
                    light,
                    false,
                    true,
                    fluidStack.componentsPatch
                )
            }
        }

        val squeeze = when {
            processingPT < 0 -> 0f
            processingPT < 2 -> Mth.lerp(processingPT / 2f, 0f, -1f)
            processingPT < be.enchantedFillingTime - be.executionTick -> -1f
            else -> radius
        }

        ms.use {
            for (bit in BITS) {
                CachedBuffers.partial(bit, be.blockState)
                    .light<SuperByteBuffer>(light)
                    .renderInto(ms, buffer.getBuffer(RenderType.solid()))
                if (CemConfig.CONFIG.renderGlint.get()) {
                    val consumer = SheetedDecalTextureGenerator(
                        buffer.getBuffer(CustomRenderType.GLINT),
                        ms.last(),
                        0.007125f
                    )
                    CachedBuffers.partial(bit, be.blockState)
                        .light<SuperByteBuffer>(light)
                        .renderInto(ms, consumer)
                }
                ms.translate(0f, -3 * squeeze / 32f, 0f)
            }
        }

        if (!fluidStack.isEmpty && level != 0f) {
            val top = fluidStack.fluid.fluidType.isLighterThanAir

            level = max(level, 0.175f)
            val min = 2.5f / 16f
            val max = min + (11 / 16f)
            val yOffset = (11 / 16f) * level

            ms.use {
                if (!top)
                    ms.translate(0f, yOffset, 0f)
                else
                    ms.translate(0f, max - min, 0f)
                FluidRenderer.renderFluidBox(
                    fluidStack.fluid, fluidStack.amount.toLong(),
                    min, min - yOffset, min,
                    max, min, max,
                    buffer, ms, light, false, true, fluidStack.componentsPatch
                )
            }
        }
    }

    companion object {
        private val RANDOM = RandomSource.create()
        private val BITS =
            arrayOf(AllPartialModels.SPOUT_TOP, AllPartialModels.SPOUT_MIDDLE, AllPartialModels.SPOUT_BOTTOM)
    }
}
