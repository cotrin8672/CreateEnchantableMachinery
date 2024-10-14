package io.github.cotrin8672.registrate

import com.simibubi.create.AllMovementBehaviours.movementBehaviour
import com.simibubi.create.content.kinetics.BlockStressDefaults
import com.simibubi.create.foundation.data.BlockStateGen
import com.simibubi.create.foundation.data.SharedProperties
import com.simibubi.create.foundation.data.TagGen.axeOrPickaxe
import com.tterrag.registrate.util.entry.BlockEntry
import io.github.cotrin8672.CreateEnchantableMachinery.REGISTRATE
import io.github.cotrin8672.content.block.drill.EnchantableDrillBlock
import io.github.cotrin8672.content.block.drill.EnchantableDrillMovementBehaviour
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
        .transform(BlockStressDefaults.setImpact(4.0))
        .onRegister(movementBehaviour(EnchantableDrillMovementBehaviour()))
        .register()

    fun register() {}
}
