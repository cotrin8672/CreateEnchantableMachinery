package io.github.cotrin8672.createenchantablemachinery.platform

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface ItemStackHandlerHelper<T> {
    companion object : KoinComponent {
        private val instance by inject<ItemStackHandlerHelper<Any>>()

        operator fun invoke() = instance
    }

    fun getSlots(itemStackHandler: T): Int
}
