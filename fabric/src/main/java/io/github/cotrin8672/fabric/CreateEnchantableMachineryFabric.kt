package io.github.cotrin8672.fabric

import io.github.cotrin8672.CreateEnchantableMachinery.init
import io.github.cotrin8672.CreateEnchantableMachinery.registerBlockMapping
import io.github.cotrin8672.CreateEnchantableMachinery.registrateHandler
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
                sideExecutorHelperModule,
                itemStackHandlerHelperModule,
            )
        }

        init()
        registrateHandler.register()

        ServerLifecycleEvents.SERVER_STARTED.register {
            registerBlockMapping()
        }
    }
}
