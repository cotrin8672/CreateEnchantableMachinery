package io.github.cotrin8672.createenchantablemachinery.fabric.platform

import com.jozufozu.flywheel.util.transform.TransformStack
import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.AllRecipeTypes
import com.simibubi.create.api.behaviour.BlockSpoutingBehaviour
import com.simibubi.create.content.fluids.FluidFX
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity
import com.simibubi.create.content.fluids.transfer.FillingRecipe
import com.simibubi.create.content.fluids.transfer.GenericItemFilling
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour
import com.simibubi.create.foundation.fluid.FluidRenderer
import com.simibubi.create.foundation.fluid.SmartFluidTank
import com.simibubi.create.foundation.utility.Iterate
import io.github.cotrin8672.createenchantablemachinery.platform.*
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandlerContainer
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import java.util.function.Predicate
import kotlin.math.max

typealias FabricFluidStack = io.github.fabricators_of_create.porting_lib.fluids.FluidStack

class SmartFluidTankHelperImpl : SmartFluidTankHelper {
    override fun getFluid(tank: SmartFluidTank): FluidStack {
        return fromFabricFluidStack(tank.fluid)
    }

    override fun setFluid(tank: SmartFluidTank, fluid: FluidStack) {
        tank.fluid = toFabricFluidStack(fluid)
    }

    override fun getRenderedFluid(tank: SmartFluidTankBehaviour.TankSegment): FluidStack {
        return fromFabricFluidStack(tank.renderedFluid)
    }
}

class FillingBySpoutHelperImpl : FillingBySpoutHelper {
    companion object {
        private val WRAPPER = ItemStackHandlerContainer(1)
    }

    private fun matchItemAndFluid(
        level: Level,
        availableFluid: FabricFluidStack,
    ): Predicate<FillingRecipe> {
        return Predicate { it.matches(WRAPPER, level) && it.requiredFluid.test(availableFluid) }
    }

    override fun getRequiredAmountForItem(level: Level, itemStack: ItemStack, fluidStack: FluidStack): Long {
        val fabricFluidStack = toFabricFluidStack(fluidStack)
        WRAPPER.setItem(0, itemStack)

        val assemblyRecipe = SequencedAssemblyRecipe.getRecipe(
            level,
            WRAPPER,
            AllRecipeTypes.FILLING.getType(),
            FillingRecipe::class.java,
            matchItemAndFluid(level, fabricFluidStack)
        )
        if (assemblyRecipe.isPresent) {
            val requiredFluid = assemblyRecipe.get().requiredFluid
            if (requiredFluid.test(fabricFluidStack)) return requiredFluid.requiredAmount
        }

        for (recipe in level.recipeManager.getRecipesFor(AllRecipeTypes.FILLING.getType(), WRAPPER, level)) {
            val fillingRecipe = recipe as FillingRecipe
            val requiredFluid = fillingRecipe.requiredFluid
            if (requiredFluid.test(fabricFluidStack)) return requiredFluid.requiredAmount
        }
        return GenericItemFilling.getRequiredAmountForItem(level, itemStack, fabricFluidStack)
    }

    override fun fillItem(
        level: Level,
        requiredAmountForItem: Long,
        itemStack: ItemStack,
        fluidStack: FluidStack,
    ): ItemStack {
        val toFill = toFabricFluidStack(fluidStack.copy())
        toFill.setAmount(requiredAmountForItem)
        val fabricFluidStack = toFabricFluidStack(fluidStack)

        WRAPPER.setItem(0, itemStack)

        val fillingRecipe = SequencedAssemblyRecipe.getRecipe(
            level,
            WRAPPER,
            AllRecipeTypes.FILLING.getType(),
            FillingRecipe::class.java,
            matchItemAndFluid(level, fabricFluidStack)
        )
            .filter {
                it.requiredFluid.test(toFill)
            }
            .orElseGet {
                for (recipe in level.recipeManager.getRecipesFor(AllRecipeTypes.FILLING.getType(), WRAPPER, level)) {
                    val fr = recipe as FillingRecipe
                    val requiredFluid = fr.requiredFluid
                    if (requiredFluid.test(toFill)) return@orElseGet fr
                }
                null
            }

        if (fillingRecipe != null) {
            val results = fillingRecipe.rollResults()
            fabricFluidStack.shrink(requiredAmountForItem)
            fluidStack.apply {
                amount = fabricFluidStack.amount
                tag = fabricFluidStack.tag
            }
            itemStack.shrink(1)
            return if (results.isEmpty()) ItemStack.EMPTY else results[0]
        }

        return GenericItemFilling.fillItem(level, requiredAmountForItem, itemStack, fabricFluidStack)
    }
}

class BlockSpoutingBehaviourHelperImpl : BlockSpoutingBehaviourHelper {
    override fun fillBlock(
        behaviour: BlockSpoutingBehaviour,
        level: Level,
        pos: BlockPos,
        spout: SpoutBlockEntity,
        fluidStack: FluidStack,
        simulate: Boolean,
    ): Long {
        return behaviour.fillBlock(level, pos, spout, toFabricFluidStack(fluidStack), simulate)
    }
}

class FluidFXHelperImpl : FluidFXHelper {
    override fun getFluidParticle(fluidStack: FluidStack): ParticleOptions {
        return FluidFX.getFluidParticle(toFabricFluidStack(fluidStack))
    }
}

class FluidRendererHelperImpl : FluidRendererHelper {
    override fun renderFluidBox(
        fluidStack: FluidStack,
        xMin: Float,
        yMin: Float,
        zMin: Float,
        xMax: Float,
        yMax: Float,
        zMax: Float,
        buffer: MultiBufferSource,
        ms: PoseStack,
        light: Int,
        renderBottom: Boolean,
    ) {
        val fluidVariant = getFluidVariant(fluidStack)
        val sprites = FluidVariantRendering.getSprites(fluidVariant)
        val fluidTexture = sprites?.get(0) ?: return

        val color = FluidVariantRendering.getColor(fluidVariant)
        val blockLightIn = (light shr 4) and 0xF
        val luminosity =
            max(blockLightIn.toDouble(), FluidVariantAttributes.getLuminance(fluidVariant).toDouble()).toInt()
        val builder = FluidRenderer.getFluidBuilder(buffer)
        val newLight = (light and 0xF00000) or (luminosity shl 4)

        val center = Vec3(
            (xMin + (xMax - xMin) / 2).toDouble(),
            (yMin + (yMax - yMin) / 2).toDouble(),
            (zMin + (zMax - zMin) / 2).toDouble()
        )
        ms.pushPose()
        if (FluidVariantAttributes.isLighterThanAir(fluidVariant)) TransformStack.cast(ms)
            .translate(center)
            .rotateX(180.0)
            .translateBack(center)

        for (side in Iterate.directions) {
            if (side == Direction.DOWN && !renderBottom) continue

            val positive = side.axisDirection == Direction.AxisDirection.POSITIVE
            if (side.axis
                    .isHorizontal
            ) {
                if (side.axis === Direction.Axis.X) {
                    FluidRenderer.renderStillTiledFace(
                        side, zMin, yMin, zMax, yMax, if (positive) xMax else xMin, builder, ms, newLight,
                        color, fluidTexture
                    )
                } else {
                    FluidRenderer.renderStillTiledFace(
                        side, xMin, yMin, xMax, yMax, if (positive) zMax else zMin, builder, ms, newLight,
                        color, fluidTexture
                    )
                }
            } else {
                FluidRenderer.renderStillTiledFace(
                    side, xMin, zMin, xMax, zMax, if (positive) yMax else yMin, builder, ms, newLight, color,
                    fluidTexture
                )
            }
        }
        ms.popPose()
    }
}

class FluidVariantAttributesHelperImpl : FluidVariantAttributesHelper {
    override fun isLighterThanAir(fluidStack: FluidStack): Boolean {
        return FluidVariantAttributes.getHandler(fluidStack.fluid)?.isLighterThanAir(getFluidVariant(fluidStack))
            ?: false
    }
}

private fun fromFabricFluidStack(stack: FabricFluidStack): FluidStack {
    return FluidStack(stack.fluid, stack.amount, stack.tag)
}

private fun toFabricFluidStack(stack: FluidStack): FabricFluidStack {
    return FabricFluidStack(stack.fluid, stack.amount, stack.tag)
}

private fun getFluidVariant(stack: FluidStack): FluidVariant {
    return FluidVariant.of(stack.fluid)
}
