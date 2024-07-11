package io.github.cotrin8672.registry

import com.simibubi.create.AllMovementBehaviours.movementBehaviour
import com.simibubi.create.content.kinetics.BlockStressDefaults
import com.simibubi.create.foundation.data.AssetLookup
import com.simibubi.create.foundation.data.SharedProperties
import com.simibubi.create.foundation.data.TagGen.axeOrPickaxe
import com.tterrag.registrate.util.entry.BlockEntry
import io.github.cotrin8672.CreateEnchantableMachinery.Companion.REGISTRATE
import io.github.cotrin8672.block.EnchantableDrillBlock
import io.github.cotrin8672.block.EnchantableDrillMovementBehaviour
import io.github.cotrin8672.util.enchantableDirectionalBLock
import net.minecraft.world.level.material.MapColor

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
            .blockstate { c, p ->
                p.enchantableDirectionalBLock(c.get()) {
                    AssetLookup.partialBaseModel(c, p)
                }
            }
            .transform(BlockStressDefaults.setImpact(4.0))
            .onRegister(movementBehaviour(EnchantableDrillMovementBehaviour()))
            .register()

        @JvmStatic
        fun register() {
        }
    }
}
