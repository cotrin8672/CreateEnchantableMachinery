package io.github.cotrin8672.createenchantablemachinery.registrate

import dev.engine_room.flywheel.lib.model.baked.PartialModel
import io.github.cotrin8672.createenchantablemachinery.CreateEnchantableMachinery

object PartialModelRegistration {
    @JvmStatic
    val ENCHANTABLE_HARVESTER_BLADE = block("block/enchantable_mechanical_harvester/blade")

    @JvmStatic
    val ENCHANTABLE_MECHANICAL_MIXER_HEAD = block("block/enchantable_mechanical_mixer/head")

    fun block(path: String): PartialModel {
        return PartialModel.of(CreateEnchantableMachinery.id(path))
    }

    fun register() {}
}
