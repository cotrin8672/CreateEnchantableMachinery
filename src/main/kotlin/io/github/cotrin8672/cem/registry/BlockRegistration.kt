package io.github.cotrin8672.cem.registry

import com.simibubi.create.api.behaviour.movement.MovementBehaviour.movementBehaviour
import com.simibubi.create.content.kinetics.saw.SawGenerator
import com.simibubi.create.foundation.data.AssetLookup
import com.simibubi.create.foundation.data.BlockStateGen
import com.simibubi.create.foundation.data.SharedProperties
import com.simibubi.create.foundation.data.TagGen.axeOrPickaxe
import com.simibubi.create.foundation.data.TagGen.pickaxeOnly
import com.tterrag.registrate.util.entry.BlockEntry
import io.github.cotrin8672.cem.CreateEnchantableMachinery.Companion.REGISTRATE
import io.github.cotrin8672.cem.content.block.drill.EnchantableDrillBlock
import io.github.cotrin8672.cem.content.block.drill.EnchantableDrillMovementBehaviour
import io.github.cotrin8672.cem.content.block.fan.EnchantableEncasedFanBlock
import io.github.cotrin8672.cem.content.block.harvester.EnchantableHarvesterBlock
import io.github.cotrin8672.cem.content.block.harvester.EnchantableHarvesterMovementBehaviour
import io.github.cotrin8672.cem.content.block.millstone.EnchantableMillstoneBlock
import io.github.cotrin8672.cem.content.block.plough.EnchantablePloughBlock
import io.github.cotrin8672.cem.content.block.plough.EnchantablePloughMovementBehaviour
import io.github.cotrin8672.cem.content.block.saw.EnchantableSawBlock
import io.github.cotrin8672.cem.content.block.saw.EnchantableSawMovementBehaviour
import io.github.cotrin8672.cem.registrate.CemStress
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.level.material.MapColor
import java.util.function.Supplier

object BlockRegistration {
    @JvmStatic
    val ENCHANTABLE_MECHANICAL_DRILL: BlockEntry<EnchantableDrillBlock> = REGISTRATE
        .block<EnchantableDrillBlock>("enchantable_mechanical_drill", ::EnchantableDrillBlock)
        .initialProperties(SharedProperties::stone)
        .properties { it.mapColor(MapColor.PODZOL) }
        .transform(axeOrPickaxe())
        .blockstate(BlockStateGen.directionalBlockProvider(true))
        .transform(CemStress.setImpact(4.0))
        .onRegister(movementBehaviour(EnchantableDrillMovementBehaviour()))
        .register()

    @JvmStatic
    val ENCHANTABLE_MECHANICAL_SAW: BlockEntry<EnchantableSawBlock> = REGISTRATE
        .block<EnchantableSawBlock>("enchantable_mechanical_saw", ::EnchantableSawBlock)
        .initialProperties(SharedProperties::stone)
        .addLayer { Supplier(RenderType::cutoutMipped) }
        .properties { it.mapColor(MapColor.PODZOL) }
        .transform(axeOrPickaxe())
        .blockstate(SawGenerator()::generate)
        .transform(CemStress.setImpact(4.0))
        .onRegister(movementBehaviour(EnchantableSawMovementBehaviour()))
        .register()

    @JvmStatic
    val ENCHANTABLE_MECHANICAL_HARVESTER: BlockEntry<EnchantableHarvesterBlock> = REGISTRATE
        .block<EnchantableHarvesterBlock>("enchantable_mechanical_harvester", ::EnchantableHarvesterBlock)
        .initialProperties(SharedProperties::stone)
        .properties { it.mapColor(MapColor.METAL).forceSolidOn() }
        .transform(axeOrPickaxe())
        .onRegister(movementBehaviour(EnchantableHarvesterMovementBehaviour()))
        .blockstate(BlockStateGen.horizontalBlockProvider(true))
        .addLayer { Supplier(RenderType::cutoutMipped) }
        .register()

    @JvmStatic
    val ENCHANTABLE_ENCASED_FAN: BlockEntry<EnchantableEncasedFanBlock> = REGISTRATE
        .block<EnchantableEncasedFanBlock>("enchantable_encased_fan", ::EnchantableEncasedFanBlock)
        .initialProperties(SharedProperties::stone)
        .properties { it.mapColor(MapColor.PODZOL) }
        .blockstate(BlockStateGen.directionalBlockProvider(true))
        .addLayer { Supplier(RenderType::cutoutMipped) }
        .transform(axeOrPickaxe())
        .transform(CemStress.setImpact(2.0))
        .register()

    @JvmStatic
    val ENCHANTABLE_MILLSTONE: BlockEntry<EnchantableMillstoneBlock> = REGISTRATE
        .block<EnchantableMillstoneBlock>("enchantable_millstone", ::EnchantableMillstoneBlock)
        .initialProperties(SharedProperties::stone)
        .properties { it.mapColor(MapColor.METAL) }
        .transform(pickaxeOnly())
        .blockstate { c, p -> p.simpleBlock(c.entry, AssetLookup.partialBaseModel(c, p)) }
        .transform(CemStress.setImpact(4.0))
        .register()

    @JvmStatic
    val ENCHANTABLE_MECHANICAL_PLOUGH: BlockEntry<EnchantablePloughBlock> = REGISTRATE
        .block<EnchantablePloughBlock>("enchantable_mechanical_plough", ::EnchantablePloughBlock)
        .initialProperties(SharedProperties::stone)
        .properties { it.mapColor(MapColor.COLOR_GRAY).forceSolidOn() }
        .transform(axeOrPickaxe())
        .onRegister(movementBehaviour(EnchantablePloughMovementBehaviour()))
        .blockstate(BlockStateGen.horizontalBlockProvider(false))
        .register()

    fun register() {}
}
