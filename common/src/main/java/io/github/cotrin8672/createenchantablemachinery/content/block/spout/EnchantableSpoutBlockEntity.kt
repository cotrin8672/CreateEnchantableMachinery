package io.github.cotrin8672.createenchantablemachinery.content.block.spout

import com.simibubi.create.AllItems
import com.simibubi.create.api.behaviour.BlockSpoutingBehaviour
import com.simibubi.create.content.fluids.spout.FillingBySpout
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack
import com.simibubi.create.foundation.advancement.AdvancementBehaviour
import com.simibubi.create.foundation.advancement.AllAdvancements
import com.simibubi.create.foundation.utility.Lang
import com.simibubi.create.foundation.utility.VecHelper
import io.github.cotrin8672.createenchantablemachinery.content.block.EnchantableBlockEntity
import io.github.cotrin8672.createenchantablemachinery.content.block.EnchantableBlockEntityDelegate
import io.github.cotrin8672.createenchantablemachinery.mixin.SpoutBlockEntityMixin
import io.github.cotrin8672.createenchantablemachinery.platform.*
import io.github.cotrin8672.createenchantablemachinery.util.extension.nonNullLevel
import io.github.cotrin8672.createenchantablemachinery.util.extension.smartBlockEntityTick
import joptsimple.internal.Strings
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import kotlin.math.ceil
import kotlin.math.max

class EnchantableSpoutBlockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState,
) : SpoutBlockEntity(type, pos, state), EnchantableBlockEntity by EnchantableBlockEntityDelegate() {
    val enchantedFillingTime: Int
        get() {
            val efficiency = this.getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY)
            return max(1, (20 - efficiency * 2))
        }

    val executionTick: Int
        get() {
            return max(0, ceil(enchantedFillingTime / 4f).toInt())
        }

    private val spawnParticleTick: Int
        get() {
            return max(0, enchantedFillingTime * 2 / 5)
        }

    private fun getCurrentFluidInTank(): FluidStack {
        return SmartFluidTankHelper().getFluid((this as SpoutBlockEntityMixin).tank.primaryHandler)
    }

    override fun whenItemHeld(
        transported: TransportedItemStack,
        handler: TransportedItemStackHandlerBehaviour,
    ): BeltProcessingBehaviour.ProcessingResult {
        if (processingTicks != -1 && processingTicks != executionTick)
            return BeltProcessingBehaviour.ProcessingResult.HOLD
        if (!FillingBySpout.canItemBeFilled(level, transported.stack))
            return BeltProcessingBehaviour.ProcessingResult.PASS
        if ((this as SpoutBlockEntityMixin).tank.isEmpty)
            return BeltProcessingBehaviour.ProcessingResult.HOLD
        val fluid = getCurrentFluidInTank()
        val requiredAmountForItem =
            FillingBySpoutHelper().getRequiredAmountForItem(nonNullLevel, transported.stack, fluid.copy())
        if (requiredAmountForItem == -1L)
            return BeltProcessingBehaviour.ProcessingResult.PASS
        if (requiredAmountForItem > fluid.amount)
            return BeltProcessingBehaviour.ProcessingResult.HOLD

        if (processingTicks == -1) {
            processingTicks = enchantedFillingTime
            notifyUpdate()
            return BeltProcessingBehaviour.ProcessingResult.HOLD
        }

        // Process finished
        val out = FillingBySpoutHelper().fillItem(nonNullLevel, requiredAmountForItem, transported.stack, fluid)
        if (!out.isEmpty) {
            val outList: MutableList<TransportedItemStack> = ArrayList()
            var held: TransportedItemStack? = null
            val result = transported.copy()
            result.stack = out
            if (!transported.stack.isEmpty) held = transported.copy()
            outList.add(result)
            handler.handleProcessingOnItem(
                transported,
                TransportedItemStackHandlerBehaviour.TransportedResult.convertToAndLeaveHeld(outList, held)
            )
        }

        award(AllAdvancements.SPOUT)
        if (trackFoods()) {
            createdChocolateBerries = createdChocolateBerries or AllItems.CHOCOLATE_BERRIES.isIn(out)
            createdHoneyApple = createdHoneyApple or AllItems.HONEYED_APPLE.isIn(out)
            createdSweetRoll = createdSweetRoll or AllItems.SWEET_ROLL.isIn(out)
            if (createdChocolateBerries && createdHoneyApple && createdSweetRoll) award(AllAdvancements.FOODS)
        }

        SmartFluidTankHelper().setFluid(tank.primaryHandler, if (fluid.isEmpty) FluidStack.EMPTY else fluid)

        sendSplash = true
        notifyUpdate()
        return BeltProcessingBehaviour.ProcessingResult.HOLD
    }

    override fun tick() {
        smartBlockEntityTick()
        val currentFluidInTank = getCurrentFluidInTank()
        if (processingTicks == -1 && (isVirtual || !level!!.isClientSide()) && !currentFluidInTank.isEmpty) {
            BlockSpoutingBehaviour.forEach { behaviour: BlockSpoutingBehaviour ->
                if (customProcess != null) return@forEach
                val amount = BlockSpoutingBehaviourHelper().fillBlock(
                    behaviour,
                    nonNullLevel,
                    worldPosition.below(2),
                    this@EnchantableSpoutBlockEntity,
                    currentFluidInTank,
                    true
                )
                if (amount > 0) {
                    processingTicks = enchantedFillingTime
                    customProcess = behaviour
                    notifyUpdate()
                }
            }
        }

        if (processingTicks >= 0) {
            processingTicks--
            if (processingTicks == executionTick && customProcess != null) {
                val fillBlock = BlockSpoutingBehaviourHelper().fillBlock(
                    customProcess,
                    nonNullLevel,
                    worldPosition.below(2),
                    this,
                    currentFluidInTank,
                    false
                )
                customProcess = null
                if (fillBlock > 0) {
                    // fabric: if the FluidStack is empty it should actually be empty
                    var newStack = currentFluidInTank.copyStackWithAmount(currentFluidInTank.amount - fillBlock)

                    if (newStack.isEmpty) newStack = FluidStack.EMPTY
                    val tank = (this as SpoutBlockEntityMixin).tank
                    SmartFluidTankHelper().setFluid(tank.primaryHandler, FluidStack.EMPTY)
                    sendSplash = true
                    notifyUpdate()
                }
            }
        }

        if (processingTicks >= spawnParticleTick && level!!.isClientSide)
            spawnProcessingParticles(SmartFluidTankHelper().getRenderedFluid((this as SpoutBlockEntityMixin).tank.primaryTank))
    }

    private fun spawnProcessingParticles(fluidStack: FluidStack) {
        if (isVirtual) return
        var vec = VecHelper.getCenterOf(worldPosition)
        vec = vec.subtract(0.0, (8 / 16f).toDouble(), 0.0)
        val particle = FluidFXHelper().getFluidParticle(fluidStack)
        nonNullLevel.addAlwaysVisibleParticle(particle, vec.x, vec.y, vec.z, 0.0, -.1, 0.0)
    }

    private fun trackFoods(): Boolean {
        return getBehaviour(AdvancementBehaviour.TYPE).isOwnerPresent
    }

    override fun addToGoggleTooltip(tooltip: MutableList<Component>, isPlayerSneaking: Boolean): Boolean {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking)
        for (instance in getEnchantments()) {
            val level = instance.level
            Lang.text(Strings.repeat(' ', 0))
                .add(instance.enchantment.getFullname(level).copy())
                .forGoggles(tooltip)
        }
        return true
    }

    override fun read(compound: CompoundTag, clientPacket: Boolean) {
        readEnchantments(compound)
        super.read(compound, clientPacket)
    }

    override fun write(compound: CompoundTag, clientPacket: Boolean) {
        compound.remove(ItemStack.TAG_ENCH)
        writeEnchantments(compound)
        super.write(compound, clientPacket)
    }
}
