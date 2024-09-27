package io.github.cotrin8672.registry

import com.jozufozu.flywheel.core.PartialModel
import io.github.cotrin8672.CreateEnchantableMachinery
import net.minecraft.resources.ResourceLocation

class PartialModelRegistration {
    companion object {
        @JvmStatic
        val ENCHANTABLE_HARVESTER_BLADE = block("block/enchantable_mechanical_harvester/blade")

        @JvmStatic
        val ENCHANTABLE_MECHANICAL_MIXER_HEAD = block("block/enchantable_mechanical_mixer/head")

        @JvmStatic
        fun block(path: String): PartialModel {
            return PartialModel(ResourceLocation(CreateEnchantableMachinery.MOD_ID, path))
        }

        @JvmStatic
        fun init() {
            ENCHANTABLE_HARVESTER_BLADE
        }
    }
}
