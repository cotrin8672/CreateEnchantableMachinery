package io.github.cotrin8672.platform

import io.github.cotrin8672.util.Side

interface SideExecutorHelper {
    fun runWhenOn(side: Side, block: () -> Unit)
}
