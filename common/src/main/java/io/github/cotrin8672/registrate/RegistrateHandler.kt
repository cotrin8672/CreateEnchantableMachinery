package io.github.cotrin8672.registrate

import com.simibubi.create.foundation.data.CreateRegistrate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface RegistrateHandler {
    companion object : KoinComponent {
        private val instance by inject<RegistrateHandler>()

        operator fun invoke() = instance
    }

    fun getRegistrate(): CreateRegistrate

    fun register()
}
