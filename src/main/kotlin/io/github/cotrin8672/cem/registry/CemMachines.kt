package io.github.cotrin8672.cem.registry

import com.zurrtum.create.AllBlocks
import com.zurrtum.create.AllItems
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block

/** The Create machines that can be enchanted. */
object CemMachines {
    const val ENCHANTMENT_VALUE = 14

    val blocks: List<Block> by lazy {
        listOf(
            AllBlocks.MECHANICAL_DRILL,
            AllBlocks.MECHANICAL_SAW,
            AllBlocks.MECHANICAL_HARVESTER,
            AllBlocks.MECHANICAL_PLOUGH,
            AllBlocks.MECHANICAL_ROLLER,
            AllBlocks.CRUSHING_WHEEL,
            AllBlocks.ENCASED_FAN,
            AllBlocks.MILLSTONE,
            AllBlocks.MECHANICAL_MIXER,
            AllBlocks.MECHANICAL_PRESS,
            AllBlocks.SPOUT,
        )
    }

    val items: List<Item> by lazy {
        listOf(
            AllItems.MECHANICAL_DRILL,
            AllItems.MECHANICAL_SAW,
            AllItems.MECHANICAL_HARVESTER,
            AllItems.MECHANICAL_PLOUGH,
            AllItems.MECHANICAL_ROLLER,
            AllItems.CRUSHING_WHEEL,
            AllItems.ENCASED_FAN,
            AllItems.MILLSTONE,
            AllItems.MECHANICAL_MIXER,
            AllItems.MECHANICAL_PRESS,
            AllItems.SPOUT,
        )
    }
}
