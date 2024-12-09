package io.github.cotrin8672.util.extension

import io.github.cotrin8672.platform.ItemEntityDataHelper
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.item.ItemEntity

val ItemEntity.entityPersistentData: CompoundTag
    get() = ItemEntityDataHelper().entityPersistentData(this)
