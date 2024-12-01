package io.github.cotrin8672.fabric

import io.github.cotrin8672.CreateEnchantableMachinery.init
import io.github.cotrin8672.CreateEnchantableMachinery.registerBlockMapping
import io.github.cotrin8672.registrate.RegistrateHandler
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import org.koin.core.context.startKoin

class CreateEnchantableMachineryFabric : ModInitializer {
    override fun onInitialize() {
        startKoin {
            modules(
                registrateModule,
                fakePlayerModule,
                alternativePlacementHelperModule,
                itemEntityDataHelperModule,
                sideExecutorModule,
                itemStackHandlerHelperModule,
            )
        }

        init()
        RegistrateHandler().register()

        ServerLifecycleEvents.SERVER_STARTED.register {
            registerBlockMapping()
        }
    }
}
