package io.github.cotrin8672.createenchantablemachinery.platform

import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.api.behaviour.BlockSpoutingBehaviour
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour
import com.simibubi.create.foundation.fluid.SmartFluidTank
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FluidStack(val fluid: Fluid, var amount: Long, tag: CompoundTag? = null) {
    var tag: CompoundTag? = null
    val isEmpty: Boolean
        get() = fluid == Fluids.EMPTY || amount <= 0

    init {
        this.tag = tag?.copy()
    }

    companion object {
        val EMPTY = FluidStack(Fluids.EMPTY, 0)
    }

    fun copy(fluid: Fluid = this.fluid, amount: Long = this.amount, tag: CompoundTag? = this.tag): FluidStack {
        return FluidStack(fluid, amount, tag)
    }

    fun copyStackWithAmount(amount: Long): FluidStack {
        if (amount <= 0) return EMPTY
        if (this.isEmpty) return EMPTY
        this.amount = amount
        return this
    }
}

interface SmartFluidTankHelper {
    companion object : KoinComponent {
        private val instance by inject<SmartFluidTankHelper>()

        operator fun invoke() = instance
    }

    fun getFluid(tank: SmartFluidTank): FluidStack

    fun setFluid(tank: SmartFluidTank, fluid: FluidStack)

    fun getRenderedFluid(tank: SmartFluidTankBehaviour.TankSegment): FluidStack
}

interface FillingBySpoutHelper {
    companion object : KoinComponent {
        private val instance by inject<FillingBySpoutHelper>()

        operator fun invoke() = instance
    }

    fun getRequiredAmountForItem(level: Level, itemStack: ItemStack, fluidStack: FluidStack): Long

    fun fillItem(level: Level, requiredAmountForItem: Long, itemStack: ItemStack, fluidStack: FluidStack): ItemStack
}

interface BlockSpoutingBehaviourHelper {
    companion object : KoinComponent {
        private val instance by inject<BlockSpoutingBehaviourHelper>()

        operator fun invoke() = instance
    }

    fun fillBlock(
        behaviour: BlockSpoutingBehaviour,
        level: Level,
        pos: BlockPos,
        spout: SpoutBlockEntity,
        fluidStack: FluidStack,
        simulate: Boolean,
    ): Long
}

interface FluidFXHelper {
    companion object : KoinComponent {
        private val instance by inject<FluidFXHelper>()

        operator fun invoke() = instance
    }

    fun getFluidParticle(fluidStack: FluidStack): ParticleOptions
}

interface FluidRendererHelper {
    companion object : KoinComponent {
        private val instance by inject<FluidRendererHelper>()

        operator fun invoke() = instance
    }

    fun renderFluidBox(
        fluidStack: FluidStack, xMin: Float, yMin: Float, zMin: Float, xMax: Float, yMax: Float,
        zMax: Float, buffer: MultiBufferSource, ms: PoseStack, light: Int, renderBottom: Boolean,
    )
}

interface FluidVariantAttributesHelper {
    companion object : KoinComponent {
        private val instance by inject<FluidVariantAttributesHelper>()

        operator fun invoke() = instance
    }

    fun isLighterThanAir(fluidStack: FluidStack): Boolean
}
