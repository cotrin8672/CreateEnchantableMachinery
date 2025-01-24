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
import com.simibubi.create.foundation.fluid.FluidHelper
import com.simibubi.create.foundation.utility.Lang
import io.github.cotrin8672.createenchantablemachinery.content.block.EnchantableBlockEntity
import io.github.cotrin8672.createenchantablemachinery.content.block.EnchantableBlockEntityDelegate
import io.github.cotrin8672.createenchantablemachinery.mixin.SpoutBlockEntityMixin
import io.github.cotrin8672.createenchantablemachinery.util.extension.smartBlockEntityTick
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack
import joptsimple.internal.Strings
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import kotlin.math.max

class EnchantableSpoutBlockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState,
) : SpoutBlockEntity(type, pos, state), EnchantableBlockEntity by EnchantableBlockEntityDelegate() {
    private fun getCurrentFluidInTank(): FluidStack {
        return (this as SpoutBlockEntityMixin).tank.primaryHandler.fluid
    }

    override fun whenItemHeld(
        transported: TransportedItemStack,
        handler: TransportedItemStackHandlerBehaviour,
    ): BeltProcessingBehaviour.ProcessingResult {
        if (processingTicks != -1 && processingTicks != 5)
            return BeltProcessingBehaviour.ProcessingResult.HOLD
        if (!FillingBySpout.canItemBeFilled(level, transported.stack))
            return BeltProcessingBehaviour.ProcessingResult.PASS
        if ((this as SpoutBlockEntityMixin).tank.isEmpty)
            return BeltProcessingBehaviour.ProcessingResult.HOLD
        val fluid = getCurrentFluidInTank()
        val requiredAmountForItem = FillingBySpout.getRequiredAmountForItem(level, transported.stack, fluid.copy())
        if (requiredAmountForItem == -1L)
            return BeltProcessingBehaviour.ProcessingResult.PASS
        if (requiredAmountForItem > fluid.amount)
            return BeltProcessingBehaviour.ProcessingResult.HOLD

        if (processingTicks == -1) {
            val efficiency = getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY)
            processingTicks = max(FILLING_TIME - efficiency * 2, 1)
            notifyUpdate()
            return BeltProcessingBehaviour.ProcessingResult.HOLD
        }

        // Process finished
        val out = FillingBySpout.fillItem(level, requiredAmountForItem, transported.stack, fluid)
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

        tank.primaryHandler.fluid =
            if (fluid.isEmpty) FluidStack.EMPTY else fluid // fabric: if the FluidStack is empty it should actually be empty
        sendSplash = true
        notifyUpdate()
        return BeltProcessingBehaviour.ProcessingResult.HOLD
    }

    override fun tick() {
        smartBlockEntityTick()
        val currentFluidInTank = (this@EnchantableSpoutBlockEntity as SpoutBlockEntityMixin).tank.primaryHandler.fluid
        if (processingTicks == -1 && (isVirtual || !level!!.isClientSide()) && !currentFluidInTank.isEmpty) {
            BlockSpoutingBehaviour.forEach { behaviour: BlockSpoutingBehaviour ->
                if (customProcess != null) return@forEach
                if (behaviour.fillBlock(level, worldPosition.below(2), this, currentFluidInTank, true) > 0) {
                    val efficiency = getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY)
                    processingTicks = max(FILLING_TIME - efficiency * 2.5, 1.0).toInt()
                    customProcess = behaviour
                    notifyUpdate()
                }
            }
        }

        if (processingTicks >= 0) {
            processingTicks--
            if (processingTicks == 5 && customProcess != null) {
                val fillBlock =
                    customProcess.fillBlock(level, worldPosition.below(2), this, currentFluidInTank, false).toLong()
                customProcess = null
                if (fillBlock > 0) {
                    // fabric: if the FluidStack is empty it should actually be empty
                    var newStack = FluidHelper.copyStackWithAmount(
                        currentFluidInTank,
                        (currentFluidInTank.amount - fillBlock).toInt().toLong()
                    )
                    if (newStack.isEmpty) newStack = FluidStack.EMPTY
                    tank.primaryHandler.fluid = newStack
                    sendSplash = true
                    notifyUpdate()
                }
            }
        }

        if (processingTicks >= 8 && level!!.isClientSide)
            spawnProcessingParticles(tank.primaryTank.renderedFluid)
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
