package io.github.cotrin8672

import com.simibubi.create.AllBlocks
import io.github.cotrin8672.config.Config
import io.github.cotrin8672.registrate.KotlinRegistrate
import io.github.cotrin8672.registry.BlockEntityRegistration
import io.github.cotrin8672.registry.BlockRegistration
import io.github.cotrin8672.registry.PartialModelRegistration
import io.github.cotrin8672.util.EnchantableBlockMapping
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(CreateEnchantableMachinery.MOD_ID)
class CreateEnchantableMachinery {
    companion object {
        const val MOD_ID = "createenchantablemachinery"

        val REGISTRATE = KotlinRegistrate.create(MOD_ID)
    }

    init {
        MOD_BUS.addListener(this::registerEnchantableBlockMapping)
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT) {
            Runnable {
                PartialModelRegistration.init()
            }
        }
        //MOD_BUS.register(UnloadEvent())
        REGISTRATE.registerEventListeners(MOD_BUS)
        BlockRegistration.register()
        BlockEntityRegistration.register()
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.clientSpec, "$MOD_ID-client.toml")
    }

    private fun registerEnchantableBlockMapping(event: FMLCommonSetupEvent) {
        EnchantableBlockMapping(AllBlocks.MECHANICAL_DRILL to BlockRegistration.ENCHANTABLE_MECHANICAL_DRILL)
        EnchantableBlockMapping(AllBlocks.MECHANICAL_HARVESTER to BlockRegistration.ENCHANTABLE_MECHANICAL_HARVESTER)
        EnchantableBlockMapping(AllBlocks.MECHANICAL_SAW to BlockRegistration.ENCHANTABLE_MECHANICAL_SAW)
        EnchantableBlockMapping(AllBlocks.MECHANICAL_PLOUGH to BlockRegistration.ENCHANTABLE_MECHANICAL_PLOUGH)
        EnchantableBlockMapping(AllBlocks.ENCASED_FAN to BlockRegistration.ENCHANTABLE_ENCASED_FAN)
    }

    private fun clientInit(event: FMLClientSetupEvent) {
        PartialModelRegistration.init()
    }
}
