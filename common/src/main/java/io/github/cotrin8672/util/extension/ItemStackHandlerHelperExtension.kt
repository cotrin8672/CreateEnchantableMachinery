package io.github.cotrin8672.util.extension

import io.github.cotrin8672.CreateEnchantableMachinery
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler

val ItemStackHandler.inventorySlots: Int
    get() = CreateEnchantableMachinery.itemStackHandlerHelper.getSlots(this)
