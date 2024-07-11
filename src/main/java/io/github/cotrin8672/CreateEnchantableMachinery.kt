package io.github.cotrin8672

import com.simibubi.create.AllBlocks
import io.github.cotrin8672.registrate.RegistrateKt
import io.github.cotrin8672.registry.BlockEntityRegistration
import io.github.cotrin8672.registry.BlockRegistration
import io.github.cotrin8672.util.EnchantableBlockMapping
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(CreateEnchantableMachinery.MOD_ID)
class CreateEnchantableMachinery {
    companion object {
        const val MOD_ID = "createenchantablemachinery"

        val REGISTRATE = RegistrateKt.create(MOD_ID)
    }

    init {
        MOD_BUS.addListener(this::registerEnchantableBlockMapping)
        REGISTRATE.registerEventListeners(MOD_BUS)
        BlockRegistration.register()
        BlockEntityRegistration.register()
    }

    private fun registerEnchantableBlockMapping(event: FMLCommonSetupEvent) {
        EnchantableBlockMapping(
            originBlock = AllBlocks.MECHANICAL_DRILL.get(),
            alternativeBlock = BlockRegistration.ENCHANTABLE_MECHANICAL_DRILL.get(),
            listOf(Enchantments.BLOCK_EFFICIENCY, Enchantments.BLOCK_FORTUNE, Enchantments.SILK_TOUCH),
        )
    }
}
