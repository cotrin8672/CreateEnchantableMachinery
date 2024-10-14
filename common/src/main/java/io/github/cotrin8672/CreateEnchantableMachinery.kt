package io.github.cotrin8672

import com.simibubi.create.AllBlocks
import com.simibubi.create.foundation.data.CreateRegistrate
import io.github.cotrin8672.registrate.BlockEntityRegistration
import io.github.cotrin8672.registrate.BlockRegistration
import io.github.cotrin8672.registrate.RegistrateHandler
import io.github.cotrin8672.util.AlternativePlacementHelper
import io.github.cotrin8672.util.EnchantableBlockMapping
import net.minecraft.resources.ResourceLocation
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object CreateEnchantableMachinery : KoinComponent {
    const val MOD_ID: String = "createenchantablemachinery"
    val registrateHandler: RegistrateHandler by inject()
    val REGISTRATE: CreateRegistrate
        get() = registrateHandler.getRegistrate()
    val alternativePlacementHelper: AlternativePlacementHelper by inject()

    @JvmStatic
    fun init() {
        BlockRegistration.register()
        BlockEntityRegistration.register()
    }

    fun registerBlockMapping() {
        EnchantableBlockMapping(AllBlocks.MECHANICAL_DRILL to BlockRegistration.ENCHANTABLE_MECHANICAL_DRILL)
    }

    fun id(path: String): ResourceLocation {
        return ResourceLocation(MOD_ID, path)
    }
}
