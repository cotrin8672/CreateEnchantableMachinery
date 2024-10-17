package io.github.cotrin8672.registrate

import com.simibubi.create.foundation.data.CreateRegistrate

interface RegistrateHandler {
    fun getRegistrate(): CreateRegistrate

    fun register()
}
