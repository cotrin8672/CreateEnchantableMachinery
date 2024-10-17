package io.github.cotrin8672.forge.util

import io.github.cotrin8672.util.Side
import io.github.cotrin8672.util.interfaces.SideExecutorHelper
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.DistExecutor

class SideExecutorHelperImpl : SideExecutorHelper {
    override fun runWhenOn(side: Side, block: () -> Unit) {
        when (side) {
            Side.CLIENT -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT) { Runnable(block) }
            Side.SERVER -> DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER) { Runnable(block) }
        }
    }
}
