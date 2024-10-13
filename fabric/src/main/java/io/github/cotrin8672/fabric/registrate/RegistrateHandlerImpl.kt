package io.github.cotrin8672.fabric.registrate

import com.simibubi.create.foundation.data.CreateRegistrate
import io.github.cotrin8672.CreateEnchantableMachinery
import io.github.cotrin8672.registrate.RegistrateHandler

class RegistrateHandlerImpl : RegistrateHandler {
    private var registrate: CreateRegistrate? = null

    override fun getRegistrate(): CreateRegistrate {
        return if (registrate == null) {
            registrate = CreateRegistrate.create(CreateEnchantableMachinery.MOD_ID)
            registrate!!
        } else registrate!!
    }

    override fun register() {
        checkNotNull(registrate).register()
    }
}
