package io.github.cotrin8672.util.interfaces

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.item.ItemEntity

interface ItemEntityDataHelper {
    fun persistentData(itemEntity: ItemEntity): CompoundTag
}
