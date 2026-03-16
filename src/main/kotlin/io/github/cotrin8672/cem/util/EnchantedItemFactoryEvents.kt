package io.github.cotrin8672.cem.util

import net.minecraftforge.client.event.ClientPlayerNetworkEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.TagsUpdatedEvent
import net.minecraftforge.event.server.ServerStoppedEvent

object EnchantedItemFactoryEvents {
    fun register() {
        MinecraftForge.EVENT_BUS.addListener(::onServerStopped)
        MinecraftForge.EVENT_BUS.addListener(::onTagsUpdated)
    }

    fun registerClient() {
        MinecraftForge.EVENT_BUS.addListener(::onClientLogout)
    }

    private fun onServerStopped(event: ServerStoppedEvent) {
        EnchantedItemFactory.clearCache()
    }

    private fun onClientLogout(event: ClientPlayerNetworkEvent.LoggingOut) {
        EnchantedItemFactory.clearCache()
    }

    private fun onTagsUpdated(event: TagsUpdatedEvent) {
        EnchantedItemFactory.clearCache()
    }
}
