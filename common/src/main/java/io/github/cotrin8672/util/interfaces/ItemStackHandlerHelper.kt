package io.github.cotrin8672.util.interfaces

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface ItemStackHandlerHelper<T> {
    companion object : KoinComponent {
        private val instance: ItemStackHandlerHelper<Any> by inject()

        operator fun invoke() = instance
    }

    fun getSlots(itemStackHandler: T): Int
}
