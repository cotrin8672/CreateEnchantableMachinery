package io.github.cotrin8672.util.interfaces

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.item.ItemEntity

interface ItemEntityDataHelper {
    fun entityPersistentData(itemEntity: ItemEntity): CompoundTag
}
