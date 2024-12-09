package io.github.cotrin8672.createenchantablemachinery.fabric.platform

import io.github.cotrin8672.createenchantablemachinery.platform.SideExecutor
import io.github.cotrin8672.createenchantablemachinery.util.Side
import io.github.fabricators_of_create.porting_lib.util.EnvExecutor
import net.fabricmc.api.EnvType

class SideExecutorImpl : SideExecutor {
    override fun runWhenOn(side: Side, block: () -> Unit) {
        when (side) {
            Side.CLIENT -> EnvExecutor.runWhenOn(EnvType.CLIENT) { Runnable(block) }
            Side.SERVER -> EnvExecutor.runWhenOn(EnvType.SERVER) { Runnable(block) }
        }
    }
}
