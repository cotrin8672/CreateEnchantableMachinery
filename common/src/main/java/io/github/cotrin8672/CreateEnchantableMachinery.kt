package io.github.cotrin8672

import com.simibubi.create.AllBlocks
import com.simibubi.create.foundation.data.CreateRegistrate
import io.github.cotrin8672.registrate.BlockEntityRegistration
import io.github.cotrin8672.registrate.BlockRegistration
import io.github.cotrin8672.registrate.RegistrateHandler
import io.github.cotrin8672.util.EnchantableBlockMapping
import io.github.cotrin8672.util.interfaces.AlternativePlacementHelper
import io.github.cotrin8672.util.interfaces.ItemEntityDataHelper
import net.minecraft.resources.ResourceLocation
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object CreateEnchantableMachinery : KoinComponent {
    const val MOD_ID: String = "createenchantablemachinery"
    val registrateHandler: RegistrateHandler by inject()
    val REGISTRATE: CreateRegistrate
        get() = registrateHandler.getRegistrate()
    val alternativePlacementHelper: AlternativePlacementHelper by inject()
    val itemEntityDataHelper: ItemEntityDataHelper by inject()

    @JvmStatic
    fun init() {
        BlockRegistration.register()
        BlockEntityRegistration.register()
    }

    fun registerBlockMapping() {
        EnchantableBlockMapping(AllBlocks.MECHANICAL_DRILL to BlockRegistration.ENCHANTABLE_MECHANICAL_DRILL)
        EnchantableBlockMapping(AllBlocks.MECHANICAL_HARVESTER to BlockRegistration.ENCHANTABLE_MECHANICAL_HARVESTER)
        EnchantableBlockMapping(AllBlocks.MECHANICAL_SAW to BlockRegistration.ENCHANTABLE_MECHANICAL_SAW)
        EnchantableBlockMapping(AllBlocks.MECHANICAL_PLOUGH to BlockRegistration.ENCHANTABLE_MECHANICAL_PLOUGH)
        EnchantableBlockMapping(AllBlocks.ENCASED_FAN to BlockRegistration.ENCHANTABLE_ENCASED_FAN)
        EnchantableBlockMapping(AllBlocks.MILLSTONE to BlockRegistration.ENCHANTABLE_MILLSTONE)
        EnchantableBlockMapping(AllBlocks.CRUSHING_WHEEL to BlockRegistration.ENCHANTABLE_CRUSHING_WHEEL)
        EnchantableBlockMapping(AllBlocks.MECHANICAL_PRESS to BlockRegistration.ENCHANTABLE_MECHANICAL_PRESS)
        EnchantableBlockMapping(AllBlocks.MECHANICAL_MIXER to BlockRegistration.ENCHANTABLE_MECHANICAL_MIXER)
        EnchantableBlockMapping(AllBlocks.MECHANICAL_ROLLER to BlockRegistration.ENCHANTABLE_MECHANICAL_ROLLER)
    }

    fun id(path: String): ResourceLocation {
        return ResourceLocation(MOD_ID, path)
    }
}
