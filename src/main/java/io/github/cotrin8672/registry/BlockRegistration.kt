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
import io.github.cotrin8672.behaviour.EnchantableSawMovementBehaviour
import io.github.cotrin8672.block.EnchantableDrillBlock
import io.github.cotrin8672.block.EnchantableSawBlock
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.level.material.MapColor
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
        val ENCHANTABLE_MECHANICAL_SAW: BlockEntry<EnchantableSawBlock> = REGISTRATE.block<EnchantableSawBlock>(
            "enchantable_mechanical_saw",
            ::EnchantableSawBlock
        )
            .initialProperties(SharedProperties::stone)
            .addLayer {
                Supplier { RenderType.cutoutMipped() }
            }
            .properties { it.mapColor(MapColor.PODZOL) }
            .transform(axeOrPickaxe())
            .blockstate(SawGenerator()::generate)
            .transform(BlockStressDefaults.setImpact(4.0))
            .onRegister(movementBehaviour(EnchantableSawMovementBehaviour()))
            .register()

        @JvmStatic
        fun register() {
        }
    }
}
