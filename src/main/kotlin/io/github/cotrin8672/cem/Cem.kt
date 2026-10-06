package io.github.cotrin8672.cem

import io.github.cotrin8672.cem.registry.CemBlockEntityTypes
import io.github.cotrin8672.cem.registry.CemItemComponents
import io.github.cotrin8672.cem.registry.CemLootTables
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.resources.Identifier
import org.slf4j.LoggerFactory
import org.spongepowered.asm.mixin.MixinEnvironment

object Cem : ModInitializer {
    const val MOD_ID = "createenchantablemachinery"
    val LOGGER = LoggerFactory.getLogger(MOD_ID)

    fun asResource(path: String): Identifier = Identifier.fromNamespaceAndPath(MOD_ID, path)

    override fun onInitialize() {
        CemBlockEntityTypes.register()
        CemItemComponents.register()
        CemLootTables.register()

        // Dev only: apply every mixin at startup so a broken injection target fails fast instead of on first use.
        if (FabricLoader.getInstance().isDevelopmentEnvironment && System.getProperty("cem.auditMixins") != null) {
            MixinEnvironment.getCurrentEnvironment().audit()
        }
    }
}
