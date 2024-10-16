package io.github.cotrin8672.registrate

import com.simibubi.create.AllMovementBehaviours.movementBehaviour
import com.simibubi.create.content.kinetics.BlockStressDefaults
import com.simibubi.create.content.kinetics.saw.SawGenerator
import com.simibubi.create.foundation.data.BlockStateGen
import com.simibubi.create.foundation.data.SharedProperties
import com.simibubi.create.foundation.data.TagGen.axeOrPickaxe
import com.tterrag.registrate.util.entry.BlockEntry
import io.github.cotrin8672.CreateEnchantableMachinery.REGISTRATE
import io.github.cotrin8672.content.block.drill.EnchantableDrillBlock
import io.github.cotrin8672.content.block.drill.EnchantableDrillMovementBehaviour
import io.github.cotrin8672.content.block.harvester.EnchantableHarvesterBlock
import io.github.cotrin8672.content.block.harvester.EnchantableHarvesterMovementBehaviour
import io.github.cotrin8672.content.block.saw.EnchantableSawBlock
import io.github.cotrin8672.content.block.saw.EnchantableSawMovementBehaviour
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
        .transform(BlockStressDefaults.setImpact(4.0))
        .onRegister(movementBehaviour(EnchantableDrillMovementBehaviour()))
        .register()

    @JvmStatic
    val ENCHANTABLE_MECHANICAL_HARVESTER: BlockEntry<EnchantableHarvesterBlock> =
        REGISTRATE.block<EnchantableHarvesterBlock>("enchantable_mechanical_harvester", ::EnchantableHarvesterBlock)
            .initialProperties(SharedProperties::stone)
            .properties { it.mapColor(MapColor.METAL).forceSolidOn() }
            .transform(axeOrPickaxe())
            .onRegister(movementBehaviour(EnchantableHarvesterMovementBehaviour()))
            .blockstate(BlockStateGen.horizontalBlockProvider(true))
            .addLayer { Supplier { RenderType.cutoutMipped() } }
            .register()

    @JvmStatic
    val ENCHANTABLE_MECHANICAL_SAW: BlockEntry<EnchantableSawBlock> =
        REGISTRATE.block<EnchantableSawBlock>("enchantable_mechanical_saw", ::EnchantableSawBlock)
            .initialProperties(SharedProperties::stone)
            .addLayer { Supplier { RenderType.cutoutMipped() } }
            .properties { it.mapColor(MapColor.PODZOL) }
            .transform(axeOrPickaxe())
            .blockstate(SawGenerator()::generate)
            .transform(BlockStressDefaults.setImpact(4.0))
            .onRegister(movementBehaviour(EnchantableSawMovementBehaviour()))
            .register()

    fun register() {}
}
