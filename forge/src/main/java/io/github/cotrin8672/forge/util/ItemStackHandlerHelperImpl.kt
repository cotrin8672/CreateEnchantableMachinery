package io.github.cotrin8672.forge.util

import io.github.cotrin8672.util.interfaces.ItemStackHandlerHelper
import net.minecraftforge.items.ItemStackHandler

class ItemStackHandlerHelperImpl : ItemStackHandlerHelper<ItemStackHandler> {
    override fun getSlots(itemStackHandler: ItemStackHandler): Int {
        return itemStackHandler.slots
    }
}
