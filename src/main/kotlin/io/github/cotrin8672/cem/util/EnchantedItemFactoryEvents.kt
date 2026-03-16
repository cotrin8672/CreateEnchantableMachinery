package io.github.cotrin8672.cem.util

import io.github.cotrin8672.cem.Cem
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent
import net.neoforged.neoforge.event.TagsUpdatedEvent
import net.neoforged.neoforge.event.server.ServerStoppedEvent

@EventBusSubscriber(modid = Cem.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
object EnchantedItemFactoryEvents {
    @SubscribeEvent
    fun onServerStopped(event: ServerStoppedEvent) {
        EnchantedItemFactory.clearCache()
    }

    @SubscribeEvent
    fun onClientLogout(event: ClientPlayerNetworkEvent.LoggingOut) {
        EnchantedItemFactory.clearCache()
    }

    @SubscribeEvent
    fun onTagsUpdated(event: TagsUpdatedEvent) {
        EnchantedItemFactory.clearCache()
    }
}
