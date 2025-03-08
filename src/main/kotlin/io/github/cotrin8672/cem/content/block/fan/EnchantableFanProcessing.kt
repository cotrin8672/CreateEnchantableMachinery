package io.github.cotrin8672.cem.content.block.fan

import com.simibubi.create.api.registry.CreateBuiltInRegistries
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour.TransportedResult
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType
import com.simibubi.create.infrastructure.config.AllConfigs
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.level.Level

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
            val efficiencyLevelModifier = 1f - (efficiencyLevel * 0.1)
            val processingTime =
                (AllConfigs.server().kinetics.fanProcessingTime.get() * timeModifierForStackSize) + 1
            transported.processingTime = (processingTime * efficiencyLevelModifier).toInt()
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
            val key = CreateBuiltInRegistries.FAN_PROCESSING_TYPE.getKey(type)
            requireNotNull(key) { "Could not get id for FanProcessingType $type!" }

            processing.putString("Type", key.toString())
            val timeModifierForStackSize = ((entity.item.count - 1) / 16) + 1
            val processingTime =
                (AllConfigs.server().kinetics.fanProcessingTime.get() * timeModifierForStackSize) + 1
            val efficiencyModifier = 1f - (efficiencyLevel * 0.1)
            processing.putInt("Time", (processingTime * efficiencyModifier).toInt())
        }

        val value = processing.getInt("Time") - 1
        processing.putInt("Time", value)
        return value
    }
}
