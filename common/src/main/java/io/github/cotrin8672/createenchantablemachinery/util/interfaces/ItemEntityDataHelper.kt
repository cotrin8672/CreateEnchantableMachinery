package io.github.cotrin8672.createenchantablemachinery.util.interfaces

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.item.ItemEntity
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface ItemEntityDataHelper {
    companion object : KoinComponent {
        private val instance: ItemEntityDataHelper by inject()

        operator fun invoke() = instance
    }

    fun entityPersistentData(itemEntity: ItemEntity): CompoundTag
}
