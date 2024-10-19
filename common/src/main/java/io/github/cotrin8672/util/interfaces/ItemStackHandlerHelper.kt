package io.github.cotrin8672.util.interfaces

interface ItemStackHandlerHelper<T> {
    fun getSlots(itemStackHandler: T): Int
}
