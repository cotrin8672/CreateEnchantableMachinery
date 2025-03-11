package io.github.cotrin8672.cem.content.block.spout

import com.simibubi.create.AllItems
import com.simibubi.create.api.behaviour.spouting.BlockSpoutingBehaviour
import com.simibubi.create.content.fluids.spout.FillingBySpout
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack
import com.simibubi.create.foundation.advancement.AdvancementBehaviour
import com.simibubi.create.foundation.advancement.AllAdvancements
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour
import com.simibubi.create.foundation.fluid.FluidHelper
import com.simibubi.create.foundation.utility.CreateLang
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntityDelegate
import io.github.cotrin8672.cem.mixin.SpoutBlockEntityMixin
import io.github.cotrin8672.cem.registry.BlockEntityRegistration
import io.github.cotrin8672.cem.util.holderLookup
import io.github.cotrin8672.cem.util.nonNullLevel
import io.github.cotrin8672.cem.util.smartBlockEntityTick
import joptsimple.internal.Strings
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.enchantment.Enchantment.getFullname
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import kotlin.math.ceil
import kotlin.math.max

class EnchantableSpoutBlockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState,
) : SpoutBlockEntity(type, pos, state), EnchantableBlockEntity by EnchantableBlockEntityDelegate() {
    companion object {
        fun registerCapabilities(event: RegisterCapabilitiesEvent) {
            event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                BlockEntityRegistration.ENCHANTABLE_SPOUT.get(),
                this::getCap
            )
        }

        fun getCap(be: EnchantableSpoutBlockEntity, context: Direction?): IFluidHandler? {
            return if (context != Direction.DOWN) be.fluidTank.capability else null
        }
    }

    val enchantedFillingTime: Int
        get() {
            val efficiency = holderLookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.EFFICIENCY)
            val efficiencyModifier = 2 * getEnchantmentLevel(efficiency)
            return max(1, (20 - efficiencyModifier))
        }

    val executionTick: Int
        get() {
            return max(0, ceil(enchantedFillingTime / 4f).toInt())
        }

    private val spawnParticleTick: Int
        get() {
            return max(0, enchantedFillingTime * 2 / 5)
        }

    val fluidTank: SmartFluidTankBehaviour
        get() = (this as SpoutBlockEntityMixin).tank

    private var createdSweetRoll: Boolean
        get() = (this as SpoutBlockEntityMixin).createdSweetRoll
        set(value) {
            (this as SpoutBlockEntityMixin).createdSweetRoll = value
        }
    private var createdHoneyApple: Boolean
        get() = (this as SpoutBlockEntityMixin).createdHoneyApple
        set(value) {
            (this as SpoutBlockEntityMixin).createdHoneyApple = value
        }
    private var createdChocolateBerries: Boolean
        get() = (this as SpoutBlockEntityMixin).createdChocolateBerries
        set(value) {
            (this as SpoutBlockEntityMixin).createdChocolateBerries = value
        }

    private fun getCurrentFluidInTank(): FluidStack {
        return fluidTank.primaryHandler.fluid
    }

    override fun addBehaviours(behaviours: MutableList<BlockEntityBehaviour?>) {
        super.addBehaviours(behaviours)
    }

    override fun onItemReceived(
        transported: TransportedItemStack,
        handler: TransportedItemStackHandlerBehaviour,
    ): ProcessingResult {
        if (handler.blockEntity.isVirtual) return ProcessingResult.PASS
        if (!FillingBySpout.canItemBeFilled(level, transported.stack)) return ProcessingResult.PASS
        if (fluidTank.isEmpty) return ProcessingResult.HOLD
        if (FillingBySpout.getRequiredAmountForItem(level, transported.stack, getCurrentFluidInTank()) == -1)
            return ProcessingResult.PASS
        return ProcessingResult.HOLD
    }

    override fun whenItemHeld(
        transported: TransportedItemStack,
        handler: TransportedItemStackHandlerBehaviour,
    ): ProcessingResult {
        if (processingTicks != -1 && processingTicks != executionTick)
            return ProcessingResult.HOLD
        if (!FillingBySpout.canItemBeFilled(nonNullLevel, transported.stack))
            return ProcessingResult.PASS
        if (fluidTank.isEmpty)
            return ProcessingResult.HOLD
        val fluid = getCurrentFluidInTank()
        val requiredAmountForItem =
            FillingBySpout.getRequiredAmountForItem(nonNullLevel, transported.stack, fluid.copy())
        if (requiredAmountForItem == -1)
            return ProcessingResult.PASS
        if (requiredAmountForItem > fluid.amount)
            return ProcessingResult.HOLD

        if (processingTicks == -1) {
            processingTicks = enchantedFillingTime
            notifyUpdate()
            return ProcessingResult.HOLD
        }

        // Process finished
        val out = FillingBySpout.fillItem(nonNullLevel, requiredAmountForItem, transported.stack, fluid)
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

        fluidTank.primaryHandler.fluid = fluid
        sendSplash = true
        notifyUpdate()
        return ProcessingResult.HOLD
    }

    override fun tick() {
        smartBlockEntityTick()

        val currentFluidInTank = getCurrentFluidInTank()
        if (processingTicks == -1 && (isVirtual || !nonNullLevel.isClientSide()) && !currentFluidInTank.isEmpty) {
            val filling = worldPosition.below(2)
            val behaviour = BlockSpoutingBehaviour.get(nonNullLevel, filling)
            if (behaviour != null && behaviour.fillBlock(level, filling, this, currentFluidInTank.copy(), true) > 0) {
                processingTicks = enchantedFillingTime
                customProcess = behaviour
                notifyUpdate()
            }
        }

        if (processingTicks >= 0) {
            processingTicks--
            if (processingTicks == executionTick && customProcess != null) {
                val fillBlock = customProcess.fillBlock(
                    level,
                    worldPosition.below(2),
                    this,
                    currentFluidInTank.copy(),
                    false
                )
                customProcess = null
                if (fillBlock > 0) {
                    fluidTank.primaryHandler.fluid = FluidHelper.copyStackWithAmount(
                        currentFluidInTank,
                        currentFluidInTank.amount - fillBlock
                    )
                    sendSplash = true
                    notifyUpdate()
                }
            }
        }

        if (processingTicks >= spawnParticleTick && nonNullLevel.isClientSide)
            spawnProcessingParticles(fluidTank.primaryTank.renderedFluid)
    }

    private fun trackFoods(): Boolean {
        return getBehaviour(AdvancementBehaviour.TYPE).isOwnerPresent
    }

    override fun addToGoggleTooltip(tooltip: MutableList<Component>, isPlayerSneaking: Boolean): Boolean {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking)

        if (getEnchantments().entrySet().isEmpty()) return false
        for (instance in getEnchantments().entrySet()) {
            CreateLang.text(Strings.repeat(' ', 0))
                .add(getFullname(instance.key, instance.intValue))
                .forGoggles(tooltip)
        }
        return true
    }

    override fun read(compound: CompoundTag, registries: HolderLookup.Provider, clientPacket: Boolean) {
        readEnchantments(compound, registries)
        super.read(compound, registries, clientPacket)
    }

    override fun write(compound: CompoundTag, registries: HolderLookup.Provider, clientPacket: Boolean) {
        writeEnchantments(compound, registries)
        super.write(compound, registries, clientPacket)
    }
}
