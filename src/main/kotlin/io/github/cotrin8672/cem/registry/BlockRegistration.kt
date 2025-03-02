package io.github.cotrin8672.cem.registry

import com.simibubi.create.api.behaviour.movement.MovementBehaviour.movementBehaviour
import com.simibubi.create.content.kinetics.drill.DrillMovementBehaviour
import com.simibubi.create.foundation.data.BlockStateGen
import com.simibubi.create.foundation.data.SharedProperties
import com.simibubi.create.foundation.data.TagGen.axeOrPickaxe
import com.simibubi.create.infrastructure.config.CStress
import com.tterrag.registrate.util.entry.BlockEntry
import io.github.cotrin8672.cem.CreateEnchantableMachinery.REGISTRATE
import io.github.cotrin8672.cem.content.block.drill.EnchantableDrillBlock
import net.minecraft.world.level.material.MapColor

object BlockRegistration {
    @JvmStatic
    val ENCHANTABLE_MECHANICAL_DRILL: BlockEntry<EnchantableDrillBlock> = REGISTRATE.block<EnchantableDrillBlock>(
        "enchantable_mechanical_drill",
        ::EnchantableDrillBlock
    )
        .initialProperties(SharedProperties::stone)
        .properties { it.mapColor(MapColor.PODZOL) }
        .transform(axeOrPickaxe())
        .blockstate(BlockStateGen.directionalBlockProvider(true))
        .transform(CStress.setImpact(4.0))
        .onRegister(movementBehaviour(DrillMovementBehaviour()))
        .register()

    fun register() {}
}
