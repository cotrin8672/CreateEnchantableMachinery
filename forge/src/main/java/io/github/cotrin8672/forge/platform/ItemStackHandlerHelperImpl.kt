package io.github.cotrin8672.forge.platform

import io.github.cotrin8672.platform.ItemStackHandlerHelper
import net.minecraftforge.items.ItemStackHandler

class ItemStackHandlerHelperImpl : ItemStackHandlerHelper<ItemStackHandler> {
    override fun getSlots(itemStackHandler: ItemStackHandler): Int {
        return itemStackHandler.slots
    }
}
