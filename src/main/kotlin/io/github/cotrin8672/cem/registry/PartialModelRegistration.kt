package io.github.cotrin8672.cem.registry

import dev.engine_room.flywheel.lib.model.baked.PartialModel
import io.github.cotrin8672.cem.CreateEnchantableMachinery

object PartialModelRegistration {
    @JvmStatic
    val ENCHANTABLE_SAW_BLADE = block("block/enchantable_mechanical_saw/blade_horizontal")

    @JvmStatic
    val ENCHANTABLE_HARVESTER_BLADE = block("block/enchantable_mechanical_harvester/blade")

    @JvmStatic
    val ENCHANTABLE_MECHANICAL_MIXER_HEAD = block("block/enchantable_mechanical_mixer/head")

    fun block(path: String): PartialModel {
        return PartialModel.of(CreateEnchantableMachinery.asResource(path))
    }

    fun register() {}
}
