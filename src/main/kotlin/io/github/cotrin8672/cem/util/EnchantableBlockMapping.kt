package io.github.cotrin8672.cem.util

import com.tterrag.registrate.util.entry.BlockEntry
import net.minecraft.world.level.block.Block

object EnchantableBlockMapping {
    private val mappings: MutableMap<Block, Block> = mutableMapOf()

    fun register(map: Pair<BlockEntry<*>, BlockEntry<*>>) {
        mappings[map.first.value()] = map.second.value()
    }

    fun register(originalBlock: Block, enchantableBlock: BlockEntry<*>) {
        mappings[originalBlock] = enchantableBlock.value()
    }

    @JvmStatic
    fun clear() {
        mappings.clear()
    }

    @JvmStatic
    fun getAlternativeBlock(originalBlock: Block): Block? {
        return mappings[originalBlock]
    }

    @JvmStatic
    fun getOriginalBlocks(): Set<Block> {
        return mappings.keys.toSet()
    }

    @JvmStatic
    fun getEnchantableBlocks(): Set<Block> {
        return mappings.values.toSet()
    }
}
