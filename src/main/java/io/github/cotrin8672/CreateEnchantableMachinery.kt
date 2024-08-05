package io.github.cotrin8672

import com.simibubi.create.AllBlocks
import io.github.cotrin8672.registrate.KotlinRegistrate
import io.github.cotrin8672.registry.BlockEntityRegistration
import io.github.cotrin8672.registry.BlockRegistration
import io.github.cotrin8672.util.EnchantableBlockMapping
import net.minecraftforge.fml.common.Mod
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
        REGISTRATE.registerEventListeners(MOD_BUS)
        BlockRegistration.register()
        BlockEntityRegistration.register()
    }

    private fun registerEnchantableBlockMapping(event: FMLCommonSetupEvent) {
        EnchantableBlockMapping(AllBlocks.MECHANICAL_DRILL to BlockRegistration.ENCHANTABLE_MECHANICAL_DRILL)
        EnchantableBlockMapping(AllBlocks.MECHANICAL_HARVESTER to BlockRegistration.ENCHANTABLE_MECHANICAL_HARVESTER)
    }
}
