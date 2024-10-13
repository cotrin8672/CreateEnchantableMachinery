package io.github.cotrin8672

import com.simibubi.create.foundation.data.CreateRegistrate
import io.github.cotrin8672.registrate.RegistrateHandler
import net.minecraft.resources.ResourceLocation
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object CreateEnchantableMachinery : KoinComponent {
    const val MOD_ID: String = "createenchantablemachinery"
    val registrateHandler: RegistrateHandler by inject()
    val REGISTRATE: CreateRegistrate
        get() = registrateHandler.getRegistrate()

    @JvmStatic
    fun init() {
    }

    fun id(path: String): ResourceLocation {
        return ResourceLocation(MOD_ID, path)
    }
}
