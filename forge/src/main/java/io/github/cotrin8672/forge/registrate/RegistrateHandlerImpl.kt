package io.github.cotrin8672.forge.registrate

import com.simibubi.create.foundation.data.CreateRegistrate
import io.github.cotrin8672.registrate.RegistrateHandler
import thedarkcolour.kotlinforforge.forge.MOD_BUS

class RegistrateHandlerImpl : RegistrateHandler {
    private var registrate: CreateRegistrate? = null

    override fun getRegistrate(): CreateRegistrate {
        return if (registrate == null) {
            registrate = KotlinRegistrate.create("")
            registrate!!
        } else registrate!!
    }

    override fun register() {
        checkNotNull(registrate).registerEventListeners(MOD_BUS)
    }
}
