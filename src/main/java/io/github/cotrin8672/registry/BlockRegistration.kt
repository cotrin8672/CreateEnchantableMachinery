package io.github.cotrin8672.registry

import com.simibubi.create.AllMovementBehaviours.movementBehaviour
import com.simibubi.create.content.kinetics.BlockStressDefaults
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlock
import com.simibubi.create.content.kinetics.saw.SawGenerator
import com.simibubi.create.foundation.data.AssetLookup
import com.simibubi.create.foundation.data.BlockStateGen
import com.simibubi.create.foundation.data.SharedProperties
import com.simibubi.create.foundation.data.TagGen.axeOrPickaxe
import com.simibubi.create.foundation.data.TagGen.pickaxeOnly
import com.tterrag.registrate.util.entry.BlockEntry
import io.github.cotrin8672.CreateEnchantableMachinery.Companion.REGISTRATE
import io.github.cotrin8672.behaviour.movement.EnchantableDrillMovementBehaviour
import io.github.cotrin8672.behaviour.movement.EnchantableHarvesterMovementBehaviour
import io.github.cotrin8672.behaviour.movement.EnchantablePloughMovementBehaviour
import io.github.cotrin8672.behaviour.movement.EnchantableSawMovementBehaviour
import io.github.cotrin8672.block.*
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.material.PushReaction
import java.util.function.Supplier

@Suppress("unused")
class BlockRegistration {
    companion object {
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

        @JvmStatic
        val ENCHANTABLE_MECHANICAL_PLOUGH: BlockEntry<EnchantablePloughBlock> =
            REGISTRATE.block<EnchantablePloughBlock>("enchantable_mechanical_plough", ::EnchantablePloughBlock)
                .initialProperties(SharedProperties::stone)
                .properties { it.mapColor(MapColor.COLOR_GRAY).forceSolidOn() }
                .transform(axeOrPickaxe())
                .onRegister(movementBehaviour(EnchantablePloughMovementBehaviour()))
                .blockstate(BlockStateGen.horizontalBlockProvider(false))
                .register()

        @JvmStatic
        val ENCHANTABLE_ENCASED_FAN: BlockEntry<EnchantableEncasedFanBlock> =
            REGISTRATE.block<EnchantableEncasedFanBlock>("enchantable_encased_fan", ::EnchantableEncasedFanBlock)
                .initialProperties(SharedProperties::stone)
                .properties { it.mapColor(MapColor.PODZOL) }
                .blockstate(BlockStateGen.directionalBlockProvider(true))
                .addLayer { Supplier { RenderType.cutoutMipped() } }
                .transform(axeOrPickaxe())
                .transform(BlockStressDefaults.setImpact(2.0))
                .register()

        @JvmStatic
        val ENCHANTABLE_MILLSTONE: BlockEntry<EnchantableMillstoneBlock> =
            REGISTRATE.block<EnchantableMillstoneBlock>("enchantable_millstone", ::EnchantableMillstoneBlock)
                .initialProperties(SharedProperties::stone)
                .properties { it.mapColor(MapColor.METAL) }
                .transform(pickaxeOnly())
                .blockstate { c, p -> p.simpleBlock(c.entry, AssetLookup.partialBaseModel(c, p)) }
                .transform(BlockStressDefaults.setImpact(4.0))
                .register()

        @JvmStatic
        val ENCHANTABLE_CRUSHING_WHEEL_CONTROLLER: BlockEntry<EnchantableCrushingWheelControllerBlock> =
            REGISTRATE.block<EnchantableCrushingWheelControllerBlock>(
                "enchantable_crushing_wheel_controller",
                ::EnchantableCrushingWheelControllerBlock
            )
                .properties {
                    it.mapColor(MapColor.STONE)
                        .noOcclusion()
                        .noLootTable()
                        .air()
                        .noCollission()
                        .pushReaction(PushReaction.BLOCK)
                }
                .blockstate { c, p ->
                    p.getVariantBuilder(c.get()).forAllStatesExcept(
                        BlockStateGen.mapToAir(p),
                        CrushingWheelControllerBlock.FACING
                    )
                }
                .register()

        @JvmStatic
        val ENCHANTABLE_CRUSHING_WHEEL: BlockEntry<EnchantableCrushingWheelBlock> =
            REGISTRATE.block<EnchantableCrushingWheelBlock>(
                "enchantable_crushing_wheel",
                ::EnchantableCrushingWheelBlock
            )
                .properties { it.mapColor(MapColor.METAL) }
                .initialProperties(SharedProperties::stone)
                .properties(BlockBehaviour.Properties::noOcclusion)
                .transform(pickaxeOnly())
                .blockstate { c, p -> BlockStateGen.axisBlock(c, p) { AssetLookup.partialBaseModel(c, p) } }
                .addLayer { Supplier { RenderType.cutoutMipped() } }
                .transform(BlockStressDefaults.setImpact(8.0))
                .register()

        @JvmStatic
        val ENCHANTABLE_MECHANICAL_PRESS: BlockEntry<EnchantableMechanicalPressBlock> =
            REGISTRATE.block<EnchantableMechanicalPressBlock>(
                "enchantable_mechanical_press",
                ::EnchantableMechanicalPressBlock
            )
                .initialProperties(SharedProperties::stone)
                .properties { it.noOcclusion().mapColor(MapColor.PODZOL) }
                .transform(axeOrPickaxe())
                .blockstate(BlockStateGen.horizontalBlockProvider(true))
                .transform(BlockStressDefaults.setImpact(8.0))
                .register()

        @JvmStatic
        fun register() {
        }
    }
}
