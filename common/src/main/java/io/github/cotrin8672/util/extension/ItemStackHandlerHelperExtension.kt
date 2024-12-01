package io.github.cotrin8672.util.extension

import io.github.cotrin8672.util.interfaces.ItemStackHandlerHelper
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler

val ItemStackHandler.inventorySlots: Int
    get() = ItemStackHandlerHelper().getSlots(this)
