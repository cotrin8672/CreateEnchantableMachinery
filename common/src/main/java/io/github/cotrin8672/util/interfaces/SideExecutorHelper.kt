package io.github.cotrin8672.util.interfaces

import io.github.cotrin8672.util.Side

interface SideExecutorHelper {
    fun runWhenOn(side: Side, block: () -> Unit)
}
