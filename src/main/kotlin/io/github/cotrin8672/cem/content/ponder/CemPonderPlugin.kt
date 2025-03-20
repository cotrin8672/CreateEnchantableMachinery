package io.github.cotrin8672.cem.content.ponder

import io.github.cotrin8672.cem.Cem
import io.github.cotrin8672.cem.registry.PonderSceneRegistration
import net.createmod.ponder.api.registration.PonderPlugin
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper
import net.minecraft.resources.ResourceLocation

object CemPonderPlugin : PonderPlugin {
    override fun getModId(): String {
        return Cem.MOD_ID
    }

    override fun registerScenes(helper: PonderSceneRegistrationHelper<ResourceLocation>) {
        PonderSceneRegistration.register(helper)
    }
}
