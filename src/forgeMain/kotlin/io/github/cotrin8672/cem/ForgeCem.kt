package io.github.cotrin8672.cem

import com.simibubi.create.foundation.data.CreateRegistrate
import io.github.cotrin8672.cem.platform.forgePlatformModule
import net.minecraftforge.fml.common.Mod
import org.koin.core.context.startKoin

@Mod(Cem.MOD_ID)
object ForgeCem {
    val REGISTRATE: CreateRegistrate = CreateRegistrate.create(Cem.MOD_ID)

    fun registrate(): CreateRegistrate = REGISTRATE

    init {
        startKoin {
            modules(forgePlatformModule)
        }

        //RegistrateProvider().get().registerEventListeners(MOD_BUS)
    }
}
