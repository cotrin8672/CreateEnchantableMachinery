package io.github.cotrin8672.fabric.util

import io.github.cotrin8672.util.Side
import io.github.cotrin8672.util.interfaces.SideExecutorHelper
import io.github.fabricators_of_create.porting_lib.util.EnvExecutor
import net.fabricmc.api.EnvType

class SideExecutorHelperImpl : SideExecutorHelper {
    override fun runWhenOn(side: Side, block: () -> Unit) {
        when (side) {
            Side.CLIENT -> EnvExecutor.runWhenOn(EnvType.CLIENT) { Runnable(block) }
            Side.SERVER -> EnvExecutor.runWhenOn(EnvType.SERVER) { Runnable(block) }
        }
    }
}
