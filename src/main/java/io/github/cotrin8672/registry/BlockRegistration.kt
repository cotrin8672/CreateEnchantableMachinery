package io.github.cotrin8672.registry

import com.simibubi.create.AllMovementBehaviours.movementBehaviour
import com.simibubi.create.content.kinetics.BlockStressDefaults
import com.simibubi.create.content.kinetics.saw.SawGenerator
import com.simibubi.create.foundation.data.BlockStateGen
import com.simibubi.create.foundation.data.SharedProperties
import com.simibubi.create.foundation.data.TagGen.axeOrPickaxe
import com.tterrag.registrate.util.entry.BlockEntry
import io.github.cotrin8672.CreateEnchantableMachinery.Companion.REGISTRATE
import io.github.cotrin8672.behaviour.EnchantableDrillMovementBehaviour
import io.github.cotrin8672.behaviour.EnchantableHarvesterMovementBehaviour
import io.github.cotrin8672.behaviour.EnchantablePloughMovementBehaviour
import io.github.cotrin8672.behaviour.EnchantableSawMovementBehaviour
import io.github.cotrin8672.block.*
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.level.material.MaterialColor
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
            .properties { it.color(MaterialColor.PODZOL) }
            .transform(axeOrPickaxe())
            .blockstate(BlockStateGen.directionalBlockProvider(true))
            .transform(BlockStressDefaults.setImpact(4.0))
            .onRegister(movementBehaviour(EnchantableDrillMovementBehaviour()))
            .register()

        @JvmStatic
        val ENCHANTABLE_MECHANICAL_HARVESTER: BlockEntry<EnchantableHarvesterBlock> =
            REGISTRATE.block<EnchantableHarvesterBlock>("enchantable_mechanical_harvester", ::EnchantableHarvesterBlock)
                .initialProperties(SharedProperties::stone)
                .properties { it.color(MaterialColor.METAL) }
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
                .properties { it.color(MaterialColor.PODZOL) }
                .transform(axeOrPickaxe())
                .blockstate(SawGenerator()::generate)
                .transform(BlockStressDefaults.setImpact(4.0))
                .onRegister(movementBehaviour(EnchantableSawMovementBehaviour()))
                .register()

        @JvmStatic
        val ENCHANTABLE_MECHANICAL_PLOUGH: BlockEntry<EnchantablePloughBlock> =
            REGISTRATE.block<EnchantablePloughBlock>("enchantable_mechanical_plough", ::EnchantablePloughBlock)
                .initialProperties(SharedProperties::stone)
                .properties { it.color(MaterialColor.COLOR_GRAY) }
                .transform(axeOrPickaxe())
                .onRegister(movementBehaviour(EnchantablePloughMovementBehaviour()))
                .blockstate(BlockStateGen.horizontalBlockProvider(false))
                .register()

        @JvmStatic
        val ENCHANTABLE_ENCASED_FAN: BlockEntry<EnchantableEncasedFanBlock> =
            REGISTRATE.block<EnchantableEncasedFanBlock>("enchantable_encased_fan", ::EnchantableEncasedFanBlock)
                .initialProperties(SharedProperties::stone)
                .properties { it.color(MaterialColor.PODZOL) }
                .blockstate(BlockStateGen.directionalBlockProvider(true))
                .addLayer { Supplier { RenderType.cutoutMipped() } }
                .transform(axeOrPickaxe())
                .transform(BlockStressDefaults.setImpact(2.0))
                .register()

        @JvmStatic
        fun register() {
        }
    }
}
