package io.github.cotrin8672.cem.registry

import net.fabricmc.fabric.api.loot.v3.LootTableEvents
import net.minecraft.core.component.DataComponents
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction
import net.minecraft.world.level.storage.loot.parameters.LootContextParams

/** Create's block loot tables drop a plain item; copy the machine's enchantments onto it. */
object CemLootTables {
    private val tables by lazy { CemMachines.blocks.mapNotNull { it.lootTable.orElse(null) }.toSet() }

    fun register() {
        LootTableEvents.MODIFY.register { key, builder, source, _ ->
            if (source.isBuiltin && key in tables) {
                builder.apply(
                    CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
                        .include(DataComponents.ENCHANTMENTS)
                        .build()
                )
            }
        }
    }
}
