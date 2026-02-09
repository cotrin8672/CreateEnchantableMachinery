package io.github.cotrin8672.cem

import com.simibubi.create.foundation.data.CreateRegistrate
import io.github.cotrin8672.cem.platform.RegistrateProvider
import io.github.cotrin8672.cem.platform.fabricPlatformModule
import net.fabricmc.api.ModInitializer
import org.koin.core.context.startKoin

object FabricCem : ModInitializer {
    val REGISTRATE: CreateRegistrate = CreateRegistrate.create(Cem.MOD_ID)

    override fun onInitialize() {
        startKoin {
            modules(fabricPlatformModule)
        }

        RegistrateProvider().get().register()
    }

    fun registrate(): CreateRegistrate = REGISTRATE

    init {

    }
}
