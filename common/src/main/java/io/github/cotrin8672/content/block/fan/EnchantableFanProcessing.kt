package io.github.cotrin8672.content.block.fan

import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour.TransportedResult
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingTypeRegistry
import com.simibubi.create.infrastructure.config.AllConfigs
import io.github.cotrin8672.util.extension.persistentData
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.level.Level
import kotlin.math.pow

class EnchantableFanProcessing(private val efficiencyLevel: Int) {
    fun canProcess(entity: ItemEntity, type: FanProcessingType): Boolean {
        if (entity.persistentData.contains("CreateData")) {
            val compound = entity.persistentData.getCompound("CreateData")
            if (compound.contains("Processing")) {
                val processing = compound.getCompound("Processing")

                if (AllFanProcessingTypes.parseLegacy(processing.getString("Type")) !== type)
                    return type.canProcess(entity.item, entity.level())
                else if (processing.getInt("Time") >= 0)
                    return true
                else if (processing.getInt("Time") == -1)
                    return false
            }
        }
        return type.canProcess(entity.item, entity.level())
    }

    fun applyProcessing(entity: ItemEntity, type: FanProcessingType): Boolean {
        if (decrementProcessingTime(entity, type) != 0) return false
        val stacks = type.process(entity.item, entity.level()) ?: return false
        if (stacks.isEmpty()) {
            entity.discard()
            return false
        }
        entity.item = stacks.removeAt(0)
        for (additional in stacks) {
            val entityIn = ItemEntity(entity.level(), entity.x, entity.y, entity.z, additional)
            entityIn.deltaMovement = entity.deltaMovement
            entity.level().addFreshEntity(entityIn)
        }
        return true
    }

    fun applyProcessing(
        transported: TransportedItemStack,
        world: Level?,
        type: FanProcessingType,
    ): TransportedResult {
        val ignore = TransportedResult.doNothing()
        if (transported.processedBy !== type) {
            transported.processedBy = type
            val timeModifierForStackSize = ((transported.stack.count - 1) / 16) + 1
            val efficiencyLevelModifier = if (efficiencyLevel == 0) 1.0 else efficiencyLevel.toDouble().pow(1.5)
            val processingTime =
                (AllConfigs.server().kinetics.fanProcessingTime.get() * timeModifierForStackSize) + 1
            transported.processingTime = (processingTime / efficiencyLevelModifier).toInt()
            if (!type.canProcess(transported.stack, world)) transported.processingTime = -1
            return ignore
        }
        if (transported.processingTime == -1) return ignore
        if (transported.processingTime-- > 0) return ignore

        val stacks = type.process(transported.stack, world) ?: return ignore

        val transportedStacks: MutableList<TransportedItemStack> = ArrayList()
        for (additional in stacks) {
            val newTransported = transported.similar
            newTransported.stack = additional.copy()
            transportedStacks.add(newTransported)
        }
        return TransportedResult.convertTo(transportedStacks)
    }

    private fun decrementProcessingTime(entity: ItemEntity, type: FanProcessingType): Int {
        val nbt = entity.persistentData

        if (!nbt.contains("CreateData")) nbt.put("CreateData", CompoundTag())
        val createData = nbt.getCompound("CreateData")

        if (!createData.contains("Processing")) createData.put("Processing", CompoundTag())
        val processing = createData.getCompound("Processing")

        if (!processing.contains("Type") || AllFanProcessingTypes.parseLegacy(processing.getString("Type")) !== type) {
            processing.putString("Type", FanProcessingTypeRegistry.getIdOrThrow(type).toString())
            val timeModifierForStackSize = ((entity.item.count - 1) / 16) + 1
            val efficiencyLevelModifier = if (efficiencyLevel == 0) 1.0 else efficiencyLevel.toDouble().pow(1.5)
            val processingTime =
                (AllConfigs.server().kinetics.fanProcessingTime.get() * timeModifierForStackSize) + 1
            processing.putInt("Time", (processingTime / efficiencyLevelModifier).toInt())
        }

        val value = processing.getInt("Time") - 1
        processing.putInt("Time", value)
        return value
    }
}
