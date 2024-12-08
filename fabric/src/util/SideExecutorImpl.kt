package io.github.cotrin8672.createenchantablemachinery.forge.util

import io.github.cotrin8672.createenchantablemachinery.util.Side
import io.github.cotrin8672.createenchantablemachinery.util.interfaces.SideExecutor
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.DistExecutor

class SideExecutorImpl : SideExecutor {
    override fun runWhenOn(side: Side, block: () -> Unit) {
        when (side) {
            Side.CLIENT -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT) { Runnable(block) }
            Side.SERVER -> DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER) { Runnable(block) }
        }
    }
}
