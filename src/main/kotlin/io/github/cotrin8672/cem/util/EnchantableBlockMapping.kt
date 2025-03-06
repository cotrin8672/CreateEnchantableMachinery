package io.github.cotrin8672.cem.util

import com.tterrag.registrate.util.entry.BlockEntry
import net.minecraft.world.level.block.Block

object EnchantableBlockMapping {
    private val mappings: MutableMap<Block, Block> = mutableMapOf()

    fun register(map: Pair<BlockEntry<*>, BlockEntry<*>>) {
        mappings[map.first.value()] = map.second.value()
    }

    @JvmStatic
    fun getAlternativeBlock(originalBlock: Block): Block? {
        return mappings[originalBlock]
    }

    @JvmStatic
    fun getOriginalBlocks(): Set<Block> {
        return mappings.keys.toSet()
    }
}
