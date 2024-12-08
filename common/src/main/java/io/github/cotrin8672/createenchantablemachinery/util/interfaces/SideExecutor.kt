package io.github.cotrin8672.createenchantablemachinery.util.interfaces

import io.github.cotrin8672.createenchantablemachinery.util.Side
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface SideExecutor {
    companion object : KoinComponent {
        private val instance: SideExecutor by inject()

        operator fun invoke() = instance
    }

    fun runWhenOn(side: Side, block: () -> Unit)
}
