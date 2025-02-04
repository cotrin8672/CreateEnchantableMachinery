package io.github.cotrin8672.createenchantablemachinery.forge.platform

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
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.phys.Vec3
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions
import net.minecraftforge.items.ItemStackHandler
import net.minecraftforge.items.wrapper.RecipeWrapper
import java.util.function.Predicate
import kotlin.math.max

typealias ForgeFluidStack = net.minecraftforge.fluids.FluidStack

class SmartFluidTankHelperImpl : SmartFluidTankHelper {
    override fun getFluid(tank: SmartFluidTank): FluidStack {
        return fromForgeFluidStack(tank.fluid)
    }

    override fun setFluid(tank: SmartFluidTank, fluid: FluidStack) {
        tank.fluid = toForgeFluidStack(fluid)
    }

    override fun getRenderedFluid(tank: SmartFluidTankBehaviour.TankSegment): FluidStack {
        return fromForgeFluidStack(tank.renderedFluid)
    }
}

class FillingBySpoutHelperImpl : FillingBySpoutHelper {
    companion object {
        private val WRAPPER = RecipeWrapper(ItemStackHandler(1));
    }

    private fun matchItemAndFluid(
        level: Level,
        availableFluid: ForgeFluidStack,
    ): Predicate<FillingRecipe> {
        return Predicate { it.matches(WRAPPER, level) && it.requiredFluid.test(availableFluid) }
    }

    override fun getRequiredAmountForItem(level: Level, itemStack: ItemStack, fluidStack: FluidStack): Long {
        val forgeFluidStack = toForgeFluidStack(fluidStack)
        WRAPPER.setItem(0, itemStack)

        val assemblyRecipe = SequencedAssemblyRecipe.getRecipe(
            level,
            WRAPPER,
            AllRecipeTypes.FILLING.getType(),
            FillingRecipe::class.java,
            matchItemAndFluid(level, forgeFluidStack)
        )
        if (assemblyRecipe.isPresent) {
            val requiredFluid = assemblyRecipe.get().requiredFluid
            if (requiredFluid.test(forgeFluidStack)) return requiredFluid.requiredAmount.toLong()
        }

        for (recipe in level.recipeManager.getRecipesFor(AllRecipeTypes.FILLING.getType(), WRAPPER, level)) {
            val fillingRecipe = recipe as FillingRecipe
            val requiredFluid = fillingRecipe.requiredFluid
            if (requiredFluid.test(forgeFluidStack)) return requiredFluid.requiredAmount.toLong()
        }
        return GenericItemFilling.getRequiredAmountForItem(level, itemStack, forgeFluidStack).toLong()
    }

    override fun fillItem(
        level: Level,
        requiredAmountForItem: Long,
        itemStack: ItemStack,
        fluidStack: FluidStack,
    ): ItemStack {
        val toFill = toForgeFluidStack(fluidStack.copy())
        toFill.setAmount(requiredAmountForItem.toInt())
        val forgeFluidStack = toForgeFluidStack(fluidStack)

        WRAPPER.setItem(0, itemStack)

        val fillingRecipe = SequencedAssemblyRecipe.getRecipe(
            level,
            WRAPPER,
            AllRecipeTypes.FILLING.getType(),
            FillingRecipe::class.java,
            matchItemAndFluid(level, forgeFluidStack)
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
            forgeFluidStack.shrink(requiredAmountForItem.toInt())
            fluidStack.apply {
                amount = forgeFluidStack.amount.toLong()
                tag = forgeFluidStack.tag
            }
            itemStack.shrink(1)
            return if (results.isEmpty()) ItemStack.EMPTY else results[0]
        }

        return GenericItemFilling.fillItem(level, requiredAmountForItem.toInt(), itemStack, forgeFluidStack)
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
        return behaviour.fillBlock(level, pos, spout, toForgeFluidStack(fluidStack), simulate).toLong()
    }
}

class FluidFXHelperImpl : FluidFXHelper {
    override fun getFluidParticle(fluidStack: FluidStack): ParticleOptions {
        return FluidFX.getFluidParticle(toForgeFluidStack(fluidStack))
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
        val fluid: Fluid = fluidStack.fluid
        val forgeFluidStack = toForgeFluidStack(fluidStack)
        val clientFluid = IClientFluidTypeExtensions.of(fluid)
        val fluidAttributes = fluid.fluidType
        val fluidTexture = Minecraft.getInstance()
            .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
            .apply(clientFluid.getStillTexture(forgeFluidStack))

        val color = clientFluid.getTintColor(forgeFluidStack)
        val blockLightIn = (light shr 4) and 0xF
        val luminosity = max(blockLightIn.toDouble(), fluidAttributes.getLightLevel(forgeFluidStack).toDouble()).toInt()
        val newLight = (light and 0xF00000) or (luminosity shl 4)
        val builder = FluidRenderer.getFluidBuilder(buffer)

        val center = Vec3(
            (xMin + (xMax - xMin) / 2).toDouble(),
            (yMin + (yMax - yMin) / 2).toDouble(),
            (zMin + (zMax - zMin) / 2).toDouble()
        )
        ms.pushPose()
        if (fluidAttributes.isLighterThanAir) TransformStack.cast(ms)
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
        return toForgeFluidStack(fluidStack).fluid.fluidType.isLighterThanAir
    }
}

private fun fromForgeFluidStack(stack: ForgeFluidStack): FluidStack {
    return FluidStack(stack.fluid, stack.amount.toLong(), stack.tag)
}

private fun toForgeFluidStack(stack: FluidStack): ForgeFluidStack {
    return ForgeFluidStack(stack.fluid, stack.amount.toInt(), stack.tag)
}
