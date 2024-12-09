package io.github.cotrin8672.fabric.platform

import io.github.cotrin8672.platform.ItemEntityDataHelper
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.item.ItemEntity

class ItemEntityDataHelperImpl : ItemEntityDataHelper {
    override fun entityPersistentData(itemEntity: ItemEntity): CompoundTag {
        return itemEntity.customData
    }
}
