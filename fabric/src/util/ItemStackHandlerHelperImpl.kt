package io.github.cotrin8672.createenchantablemachinery.forge.util

import io.github.cotrin8672.createenchantablemachinery.util.interfaces.ItemStackHandlerHelper
import net.minecraftforge.items.ItemStackHandler

class ItemStackHandlerHelperImpl : ItemStackHandlerHelper<ItemStackHandler> {
    override fun getSlots(itemStackHandler: ItemStackHandler): Int {
        return itemStackHandler.slots
    }
}
