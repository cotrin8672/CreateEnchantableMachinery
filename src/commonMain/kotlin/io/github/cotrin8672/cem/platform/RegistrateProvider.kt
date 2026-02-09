package io.github.cotrin8672.cem.platform

import com.simibubi.create.foundation.data.CreateRegistrate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface RegistrateProvider {
    companion object : KoinComponent {
        private val instance by inject<RegistrateProvider>()

        operator fun invoke() = instance
    }

    fun get(): CreateRegistrate
}
