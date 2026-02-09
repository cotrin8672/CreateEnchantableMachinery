package io.github.cotrin8672.cem.platform

import com.simibubi.create.foundation.data.CreateRegistrate
import io.github.cotrin8672.cem.ForgeCem

class RegistrateProviderImpl : RegistrateProvider {
    override fun get(): CreateRegistrate = ForgeCem.registrate()
}
