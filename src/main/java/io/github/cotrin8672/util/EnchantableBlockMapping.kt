package io.github.cotrin8672.util

import com.tterrag.registrate.util.entry.BlockEntry
import net.minecraft.world.level.block.Block

class EnchantableBlockMapping(
    originBlock: Block,
    alternativeBlock: Block,
) {
    companion object {
        @JvmStatic
        val originBlockList = mutableListOf<Block>()
        private val alternativeBlockMapping: MutableMap<Block, Block> = mutableMapOf()

        @JvmStatic
        fun getAlternativeBlock(origin: Block): Block? {
            return alternativeBlockMapping[origin]
        }
    }

    constructor(pair: Pair<BlockEntry<out Block>, BlockEntry<out Block>>) : this(pair.first.get(), pair.second.get())

    init {
        originBlockList.add(originBlock)
        alternativeBlockMapping[originBlock] = alternativeBlock
    }
}
