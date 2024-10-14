package io.github.cotrin8672.fabric

import io.github.cotrin8672.CreateEnchantableMachinery
import net.fabricmc.api.ModInitializer
import org.koin.core.context.startKoin

class CreateEnchantableMachineryFabric : ModInitializer {
    override fun onInitialize() {
        startKoin {
            modules(registrateModule)
        }

        CreateEnchantableMachinery.init()
    }
}
