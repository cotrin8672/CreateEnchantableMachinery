package io.github.cotrin8672.event

import io.github.cotrin8672.entity.BlockBreaker
import io.github.cotrin8672.entity.ContraptionBlockBreaker
import net.minecraft.server.level.ServerLevel
import net.minecraftforge.event.level.LevelEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
class UnloadEvent {
    @SubscribeEvent
    fun onUnloadEvent(event: LevelEvent.Unload) {
        val level = event.level
        if (level is ServerLevel) {
            BlockBreaker.unload(level)
            ContraptionBlockBreaker.unload(level)
        }
    }
}
