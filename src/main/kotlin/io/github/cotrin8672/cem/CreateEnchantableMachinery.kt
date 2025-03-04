package io.github.cotrin8672.cem

import com.simibubi.create.AllBlocks
import io.github.cotrin8672.cem.config.CemConfig
import io.github.cotrin8672.cem.config.ModConfigs
import io.github.cotrin8672.cem.registrate.KotlinRegistrate
import io.github.cotrin8672.cem.registry.BlockEntityRegistration
import io.github.cotrin8672.cem.registry.BlockRegistration
import io.github.cotrin8672.cem.util.EnchantableBlockMapping
import net.minecraft.resources.ResourceLocation
import net.neoforged.fml.ModContainer
import net.neoforged.fml.ModLoadingContext
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(CreateEnchantableMachinery.MOD_ID)
class CreateEnchantableMachinery(container: ModContainer) {
    companion object {
        const val MOD_ID = "createenchantablemachinery"
        val REGISTRATE = KotlinRegistrate.create(MOD_ID)

        fun asResource(path: String): ResourceLocation {
            return ResourceLocation.fromNamespaceAndPath(MOD_ID, path)
        }
    }

    init {
        MOD_BUS.addListener(this::registerEnchantableBlockMapping)
        REGISTRATE.registerEventListeners(MOD_BUS)
        BlockRegistration.register()
        BlockEntityRegistration.register()
        container.registerConfig(ModConfig.Type.CLIENT, CemConfig.CONFIG_SPEC)
        ModConfigs.register(ModLoadingContext.get(), container)
    }

    private fun registerEnchantableBlockMapping(event: FMLCommonSetupEvent) {
        EnchantableBlockMapping.register(AllBlocks.MECHANICAL_DRILL to BlockRegistration.ENCHANTABLE_MECHANICAL_DRILL)
    }
}
