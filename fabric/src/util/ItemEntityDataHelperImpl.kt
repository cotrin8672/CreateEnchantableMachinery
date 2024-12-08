package io.github.cotrin8672.createenchantablemachinery.forge.util

import io.github.cotrin8672.createenchantablemachinery.util.interfaces.ItemEntityDataHelper
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.item.ItemEntity

class ItemEntityDataHelperImpl : ItemEntityDataHelper {
    override fun entityPersistentData(itemEntity: ItemEntity): CompoundTag {
        return itemEntity.persistentData
    }
}
