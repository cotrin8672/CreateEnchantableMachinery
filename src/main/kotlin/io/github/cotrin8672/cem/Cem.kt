package io.github.cotrin8672.cem

import com.simibubi.create.AllBlocks
import io.github.cotrin8672.cem.config.CemConfig
import io.github.cotrin8672.cem.config.ModConfigs
import io.github.cotrin8672.cem.content.ponder.CemPonderPlugin
import io.github.cotrin8672.cem.registrate.KotlinRegistrate
import io.github.cotrin8672.cem.registry.BlockEntityRegistration
import io.github.cotrin8672.cem.registry.BlockRegistration
import io.github.cotrin8672.cem.registry.PartialModelRegistration
import io.github.cotrin8672.cem.util.EnchantableBlockMapping
import net.createmod.ponder.foundation.PonderIndex
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod.EventBusSubscriber(modid = Cem.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@Mod(Cem.MOD_ID)
class Cem {
    companion object {
        const val MOD_ID = "createenchantablemachinery"
        val REGISTRATE = KotlinRegistrate.create(MOD_ID)

        fun asResource(path: String): ResourceLocation {
            return ResourceLocation.fromNamespaceAndPath(MOD_ID, path)
        }
    }

    init {
        MOD_BUS.addListener(this::registerEnchantableBlockMapping)
        MOD_BUS.register(this::class.java)
        REGISTRATE.registerEventListeners(MOD_BUS)
        BlockRegistration.register()
        BlockEntityRegistration.register()
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CemConfig.CONFIG_SPEC)
        ModConfigs.register(ModLoadingContext.get())
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT) {
            Runnable {
                PartialModelRegistration.register()
                PonderIndex.addPlugin(CemPonderPlugin)
            }
        }
    }

    private fun registerEnchantableBlockMapping(event: FMLCommonSetupEvent) {
        EnchantableBlockMapping.register(AllBlocks.MECHANICAL_DRILL to BlockRegistration.ENCHANTABLE_MECHANICAL_DRILL)
        EnchantableBlockMapping.register(AllBlocks.MECHANICAL_SAW to BlockRegistration.ENCHANTABLE_MECHANICAL_SAW)
        EnchantableBlockMapping.register(AllBlocks.MECHANICAL_HARVESTER to BlockRegistration.ENCHANTABLE_MECHANICAL_HARVESTER)
        EnchantableBlockMapping.register(AllBlocks.ENCASED_FAN to BlockRegistration.ENCHANTABLE_ENCASED_FAN)
        EnchantableBlockMapping.register(AllBlocks.MILLSTONE to BlockRegistration.ENCHANTABLE_MILLSTONE)
        EnchantableBlockMapping.register(AllBlocks.CRUSHING_WHEEL to BlockRegistration.ENCHANTABLE_CRUSHING_WHEEL)
        EnchantableBlockMapping.register(AllBlocks.MECHANICAL_PLOUGH to BlockRegistration.ENCHANTABLE_MECHANICAL_PLOUGH)
        EnchantableBlockMapping.register(AllBlocks.MECHANICAL_MIXER to BlockRegistration.ENCHANTABLE_MECHANICAL_MIXER)
        EnchantableBlockMapping.register(AllBlocks.MECHANICAL_PRESS to BlockRegistration.ENCHANTABLE_MECHANICAL_PRESS)
        EnchantableBlockMapping.register(AllBlocks.MECHANICAL_ROLLER to BlockRegistration.ENCHANTABLE_MECHANICAL_ROLLER)
        EnchantableBlockMapping.register(AllBlocks.SPOUT to BlockRegistration.ENCHANTABLE_SPOUT)
    }
}
