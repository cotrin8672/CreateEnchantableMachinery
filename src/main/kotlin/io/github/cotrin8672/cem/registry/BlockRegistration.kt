package io.github.cotrin8672.cem.registry

import com.simibubi.create.api.behaviour.movement.MovementBehaviour.movementBehaviour
import com.simibubi.create.content.kinetics.saw.SawGenerator
import com.simibubi.create.foundation.data.BlockStateGen
import com.simibubi.create.foundation.data.SharedProperties
import com.simibubi.create.foundation.data.TagGen.axeOrPickaxe
import com.tterrag.registrate.util.entry.BlockEntry
import io.github.cotrin8672.cem.CreateEnchantableMachinery.Companion.REGISTRATE
import io.github.cotrin8672.cem.content.block.drill.EnchantableDrillBlock
import io.github.cotrin8672.cem.content.block.drill.EnchantableDrillMovementBehaviour
import io.github.cotrin8672.cem.content.block.saw.EnchantableSawBlock
import io.github.cotrin8672.cem.content.block.saw.EnchantableSawMovementBehaviour
import io.github.cotrin8672.cem.registrate.CemStress
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.level.material.MapColor
import java.util.function.Supplier

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
        .transform(CemStress.setImpact(4.0))
        .onRegister(movementBehaviour(EnchantableDrillMovementBehaviour()))
        .register()

    @JvmStatic
    val ENCHANTABLE_MECHANICAL_SAW: BlockEntry<EnchantableSawBlock> = REGISTRATE.block<EnchantableSawBlock>(
        "enchantable_mechanical_saw",
        ::EnchantableSawBlock
    )
        .initialProperties(SharedProperties::stone)
        .addLayer { Supplier { RenderType.cutoutMipped() } }
        .properties { it.mapColor(MapColor.PODZOL) }
        .transform(axeOrPickaxe())
        .blockstate(SawGenerator()::generate)
        .transform(CemStress.setImpact(4.0))
        .onRegister(movementBehaviour(EnchantableSawMovementBehaviour()))
        .register()

    fun register() {}
}
