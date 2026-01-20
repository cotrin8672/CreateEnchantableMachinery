package io.github.cotrin8672.cem

import net.fabricmc.api.ModInitializer

object CemFabric : ModInitializer {
    override fun onInitialize() {
        Cem.registrate()
    }
}
