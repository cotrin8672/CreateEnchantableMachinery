package io.github.cotrin8672.createenchantablemachinery.util.extension

import io.github.cotrin8672.createenchantablemachinery.util.interfaces.ItemEntityDataHelper
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.item.ItemEntity

val ItemEntity.entityPersistentData: CompoundTag
    get() = ItemEntityDataHelper().entityPersistentData(this)
