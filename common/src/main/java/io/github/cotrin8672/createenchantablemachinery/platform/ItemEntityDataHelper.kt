package io.github.cotrin8672.createenchantablemachinery.platform

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.item.ItemEntity
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface ItemEntityDataHelper {
    companion object : KoinComponent {
        private val instance by inject<ItemEntityDataHelper>()

        operator fun invoke() = instance
    }

    fun entityPersistentData(itemEntity: ItemEntity): CompoundTag
}
