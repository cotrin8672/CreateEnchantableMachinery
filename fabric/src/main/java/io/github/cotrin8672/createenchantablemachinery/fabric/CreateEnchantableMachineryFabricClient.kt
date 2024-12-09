package io.github.cotrin8672.createenchantablemachinery.fabric

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry
import io.github.cotrin8672.createenchantablemachinery.CreateEnchantableMachinery
import io.github.cotrin8672.createenchantablemachinery.config.Config
import io.github.cotrin8672.createenchantablemachinery.registrate.PartialModelRegistration
import net.fabricmc.api.ClientModInitializer
import net.minecraftforge.fml.config.ModConfig

class CreateEnchantableMachineryFabricClient : ClientModInitializer {
    override fun onInitializeClient() {
        ForgeConfigRegistry.INSTANCE.register(
            CreateEnchantableMachinery.MOD_ID,
            ModConfig.Type.CLIENT,
            Config.clientSpec
        )
        PartialModelRegistration.register()
    }
}
