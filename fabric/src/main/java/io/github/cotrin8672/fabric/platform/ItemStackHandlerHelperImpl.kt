package io.github.cotrin8672.fabric.platform

import io.github.cotrin8672.platform.ItemStackHandlerHelper
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler

class ItemStackHandlerHelperImpl : ItemStackHandlerHelper<ItemStackHandler> {
    override fun getSlots(itemStackHandler: ItemStackHandler): Int {
        return itemStackHandler.slotCount
    }
}
