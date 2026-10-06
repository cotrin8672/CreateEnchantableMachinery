package io.github.cotrin8672.cem.util

import com.zurrtum.create.content.kinetics.fan.IAirCurrentSource
import net.minecraft.world.level.block.entity.BlockEntity

/**
 * The fan whose air current is currently being ticked. A ThreadLocal because in single-player the client and server
 * threads both tick air currents.
 */
object CemFanContext {
    private val current = ThreadLocal<IAirCurrentSource?>()

    @JvmStatic
    fun run(source: IAirCurrentSource?, action: Runnable) {
        val previous = current.get()
        current.set(source)
        try {
            action.run()
        } finally {
            current.set(previous)
        }
    }

    @JvmStatic
    fun processingTime(time: Int): Int {
        val fan = current.get() as? BlockEntity ?: return time
        return CemSpeeds.fanProcessingTime(time, CemEnchantments.efficiency(fan))
    }
}
