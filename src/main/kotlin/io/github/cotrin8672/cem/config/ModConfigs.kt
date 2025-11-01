package io.github.cotrin8672.cem.config

import com.simibubi.create.api.stress.BlockStressValues
import net.createmod.catnip.config.ConfigBase
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.config.ModConfigEvent
import java.util.*
import java.util.function.Supplier


class ModConfigs {
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    companion object {
        private val CONFIGS = EnumMap<ModConfig.Type, ConfigBase>(ModConfig.Type::class.java)
        private lateinit var common: CemCommonConfig

        fun common() = common

        private fun <T : ConfigBase> register(factory: Supplier<T>, side: ModConfig.Type): T {
            val specPair = ForgeConfigSpec.Builder().configure { builder: ForgeConfigSpec.Builder ->
                val config = factory.get()
                config.registerAll(builder)
                config
            }

            val config = specPair.left
            config!!.specification = specPair.right
            CONFIGS[side] = config
            return config
        }

        fun register(context: ModLoadingContext) {
            common = register(::CemCommonConfig, ModConfig.Type.COMMON)

            CONFIGS.forEach {
                context.registerConfig(it.key, it.value.specification)
            }

            BlockStressValues.IMPACTS.registerProvider(common().kinetics.stressValues::getImpact)
        }
    }

    @SubscribeEvent
    fun onLoad(event: ModConfigEvent.Loading) {
        for (config in CONFIGS.values) if (config.specification === event.config.getSpec()) config.onLoad()
    }

    @SubscribeEvent
    fun onReload(event: ModConfigEvent.Reloading) {
        for (config in CONFIGS.values) if (config.specification === event.config.getSpec()) config.onReload()
    }
}
