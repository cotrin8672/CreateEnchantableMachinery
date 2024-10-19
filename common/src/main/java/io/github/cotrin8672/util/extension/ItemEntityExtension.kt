package io.github.cotrin8672.util.extension

import io.github.cotrin8672.CreateEnchantableMachinery
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.item.ItemEntity

val ItemEntity.entityPersistentData: CompoundTag
    get() = CreateEnchantableMachinery.itemEntityDataHelper.entityPersistentData(this)
