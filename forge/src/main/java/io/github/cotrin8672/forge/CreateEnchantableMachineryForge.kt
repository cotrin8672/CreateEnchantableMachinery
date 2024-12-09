package io.github.cotrin8672.forge

import io.github.cotrin8672.CreateEnchantableMachinery.MOD_ID
import io.github.cotrin8672.CreateEnchantableMachinery.init
import io.github.cotrin8672.CreateEnchantableMachinery.registerBlockMapping
import io.github.cotrin8672.config.Config
import io.github.cotrin8672.registrate.PartialModelRegistration
import io.github.cotrin8672.registrate.RegistrateHandler
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import org.koin.core.context.startKoin
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(MOD_ID)
class CreateEnchantableMachineryForge {
    init {
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

        MOD_BUS.addListener(this::registerBlockMapping)
        RegistrateHandler().register()
        init()
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.clientSpec, "$MOD_ID-client.toml")
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT) { Runnable { PartialModelRegistration.register() } }
    }

    private fun registerBlockMapping(event: FMLCommonSetupEvent) {
        registerBlockMapping()
    }
}
