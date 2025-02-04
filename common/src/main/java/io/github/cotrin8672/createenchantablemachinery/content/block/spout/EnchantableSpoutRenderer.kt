package io.github.cotrin8672.createenchantablemachinery.content.block.spout

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator
import com.simibubi.create.AllPartialModels
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer
import com.simibubi.create.foundation.render.CachedBufferer
import io.github.cotrin8672.createenchantablemachinery.config.Config
import io.github.cotrin8672.createenchantablemachinery.content.EnchantedRenderType
import io.github.cotrin8672.createenchantablemachinery.mixin.SpoutBlockEntityMixin
import io.github.cotrin8672.createenchantablemachinery.platform.FluidRendererHelper
import io.github.cotrin8672.createenchantablemachinery.platform.FluidVariantAttributesHelper
import io.github.cotrin8672.createenchantablemachinery.platform.SmartFluidTankHelper
import io.github.cotrin8672.createenchantablemachinery.util.extension.use
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.phys.AABB
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
        val consumer = SheetedDecalTextureGenerator(
            buffer.getBuffer(EnchantedRenderType.GLINT),
            ms.last().pose(),
            ms.last().normal(),
            0.007125f
        )
        if (Config.renderGlint.get()) {
            context.blockRenderDispatcher.renderBatched(
                be.blockState, be.blockPos, be.level!!, ms, consumer, true, RANDOM
            )
        }

        val tank = (be as SpoutBlockEntityMixin).tank ?: return

        val primaryTank = tank.primaryTank
        val fluidStack = SmartFluidTankHelper().getRenderedFluid(primaryTank)
        var level = primaryTank.fluidLevel.getValue(partialTicks)

        val processingTicks = be.processingTicks
        val processingPT = processingTicks.toFloat() - partialTicks
        var processingProgress = 1 - (processingPT - be.executionTick) / (be.enchantedFillingTime - be.executionTick)
        processingProgress = Mth.clamp(processingProgress, 0f, 1f)
        var radius = 0f

        if (processingTicks != -1) {
            radius = (((2 * processingProgress) - 1).toDouble().pow(2.0) - 1).toFloat()
            val bb = AABB(0.5, .5, 0.5, 0.5, -1.2, 0.5).inflate((radius / 32f).toDouble())
            ms.use {
                FluidRendererHelper().renderFluidBox(
                    fluidStack, bb.minX.toFloat(), bb.minY.toFloat(), bb.minZ.toFloat(),
                    bb.maxX.toFloat(), bb.maxY.toFloat(), bb.maxZ.toFloat(), buffer, ms, light, true
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
                if (Config.renderGlint.get()) {
                    CachedBufferer.partial(bit, be.blockState)
                        .light(light)
                        .renderInto(ms, consumer)
                }
                CachedBufferer.partial(bit, be.blockState)
                    .light(light)
                    .renderInto(ms, buffer.getBuffer(RenderType.solid()))
                translate(0f, -3 * squeeze / 32f, 0f)
            }
        }

        if (!fluidStack.isEmpty && level != 0f) {
            val top = FluidVariantAttributesHelper().isLighterThanAir(fluidStack)

            level = max(level.toDouble(), 0.175).toFloat()
            val min = 2.5f / 16f
            val max = min + (11 / 16f)
            val yOffset = (11 / 16f) * level

            ms.use {
                if (!top)
                    ms.translate(0f, yOffset, 0f)
                else
                    ms.translate(0f, max - min, 0f)
                FluidRendererHelper().renderFluidBox(
                    fluidStack,
                    min, min - yOffset, min,
                    max, min, max,
                    buffer, ms, light, false
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
