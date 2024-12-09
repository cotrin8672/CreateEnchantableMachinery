package io.github.cotrin8672.util

import io.github.cotrin8672.platform.SideExecutorHelper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object SideExecutor : KoinComponent {
    private val sideExecutorHelper: SideExecutorHelper by inject()

    fun runWhenOn(side: Side, block: () -> Unit) {
        sideExecutorHelper.runWhenOn(side, block)
    }
}
