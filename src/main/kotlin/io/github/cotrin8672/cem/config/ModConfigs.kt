package io.github.cotrin8672.cem.config

import com.simibubi.create.api.stress.BlockStressValues
import net.createmod.catnip.config.ConfigBase
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.ModLoadingContext
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.config.ModConfig
import net.neoforged.fml.event.config.ModConfigEvent
import net.neoforged.neoforge.common.ModConfigSpec
import java.util.*
import java.util.function.Supplier

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
class ModConfigs {
    companion object {
        private val CONFIGS = EnumMap<ModConfig.Type, ConfigBase>(ModConfig.Type::class.java)
        private lateinit var common: CemCommonConfig

        fun common() = common

        private fun <T : ConfigBase> register(factory: Supplier<T>, side: ModConfig.Type): T {
            val specPair = ModConfigSpec.Builder().configure { builder: ModConfigSpec.Builder ->
                val config = factory.get()
                config.registerAll(builder)
                config
            }

            val config = specPair.left
            config!!.specification = specPair.right
            CONFIGS[side] = config
            return config
        }

        fun register(context: ModLoadingContext, container: ModContainer) {
            common = register(::CemCommonConfig, ModConfig.Type.COMMON)

            CONFIGS.forEach {
                container.registerConfig(it.key, it.value.specification)
            }

            BlockStressValues.IMPACTS.registerProvider(common().kinetics.stressValues::getImpact)
        }

        @SubscribeEvent
        fun onLoad(event: ModConfigEvent.Loading) {
            for (config in CONFIGS.values) if (config.specification === event.config.spec) config.onLoad()
        }

        @SubscribeEvent
        fun onReload(event: ModConfigEvent.Reloading) {
            for (config in CONFIGS.values) if (config.specification === event.config.spec) config.onReload()
        }
    }
}
