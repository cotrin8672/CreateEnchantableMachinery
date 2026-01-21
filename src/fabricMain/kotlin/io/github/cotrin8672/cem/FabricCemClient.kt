package io.github.cotrin8672.cem

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry
import io.github.cotrin8672.cem.config.CemConfig
import net.fabricmc.api.ClientModInitializer
import net.minecraftforge.fml.config.ModConfig

object FabricCemClient : ClientModInitializer {
    override fun onInitializeClient() {
        ForgeConfigRegistry.INSTANCE.register(
            Cem.MOD_ID,
            ModConfig.Type.CLIENT,
            CemConfig.CONFIG_SPEC,
        )
    }
}