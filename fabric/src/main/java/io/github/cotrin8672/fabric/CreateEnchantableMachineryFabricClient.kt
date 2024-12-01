package io.github.cotrin8672.fabric

import io.github.cotrin8672.CreateEnchantableMachinery
import io.github.cotrin8672.config.Config
import io.github.cotrin8672.registrate.PartialModelRegistration
import net.fabricmc.api.ClientModInitializer
import net.minecraftforge.api.ModLoadingContext
import net.minecraftforge.fml.config.ModConfig

class CreateEnchantableMachineryFabricClient : ClientModInitializer {
    override fun onInitializeClient() {

        ModLoadingContext.registerConfig(
            CreateEnchantableMachinery.MOD_ID,
            ModConfig.Type.CLIENT,
            Config.clientSpec
        )
        PartialModelRegistration.register()
    }
}
