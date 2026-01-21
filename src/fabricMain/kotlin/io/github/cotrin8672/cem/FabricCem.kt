package io.github.cotrin8672.cem

import com.simibubi.create.foundation.data.CreateRegistrate
import net.fabricmc.api.ModInitializer

object FabricCem : ModInitializer {
    val REGISTRATE: CreateRegistrate = CreateRegistrate.create(Cem.MOD_ID)

    override fun onInitialize() {
        Cem.registrate()
    }

    fun registrate(): CreateRegistrate = REGISTRATE
}
