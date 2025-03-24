package io.github.cotrin8672.cem.content.ponder

import io.github.cotrin8672.cem.Cem
import io.github.cotrin8672.cem.registry.PonderSceneRegistration
import io.github.cotrin8672.cem.registry.PonderTagsRegistration
import net.createmod.ponder.api.registration.PonderPlugin
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper
import net.createmod.ponder.api.registration.SharedTextRegistrationHelper
import net.minecraft.resources.ResourceLocation

object CemPonderPlugin : PonderPlugin {
    override fun getModId(): String {
        return Cem.MOD_ID
    }

    override fun registerScenes(helper: PonderSceneRegistrationHelper<ResourceLocation>) {
        PonderSceneRegistration.register(helper)
    }

    override fun registerTags(helper: PonderTagRegistrationHelper<ResourceLocation>) {
        PonderTagsRegistration.register(helper)
    }

    override fun registerSharedText(helper: SharedTextRegistrationHelper) {
        helper.registerSharedText(
            "enchantment_info",
            "When wearing Engineers' Goggles, the player can get drill's enchantments"
        )
    }
}
