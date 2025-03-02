package io.github.cotrin8672.cem

import com.simibubi.create.AllBlocks
import io.github.cotrin8672.cem.registrate.KotlinRegistrate
import io.github.cotrin8672.cem.registry.BlockEntityRegistration
import io.github.cotrin8672.cem.registry.BlockRegistration
import io.github.cotrin8672.cem.util.EnchantableBlockMapping
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(CreateEnchantableMachinery.MOD_ID)
object CreateEnchantableMachinery {
    const val MOD_ID = "createenchantablemachinery"
    val REGISTRATE = KotlinRegistrate.create(MOD_ID)

    init {
        MOD_BUS.addListener(this::registerEnchantableBlockMapping)
        REGISTRATE.registerEventListeners(MOD_BUS)
        BlockRegistration.register()
        BlockEntityRegistration.register()
    }

    private fun registerEnchantableBlockMapping(event: FMLCommonSetupEvent) {
        EnchantableBlockMapping.register(AllBlocks.MECHANICAL_DRILL to BlockRegistration.ENCHANTABLE_MECHANICAL_DRILL)
    }
}
