package io.github.cotrin8672.util

import com.tterrag.registrate.util.entry.BlockEntry
import io.github.cotrin8672.block.EnchantableBlock
import net.minecraft.world.level.block.Block

class EnchantableBlockMapping<Origin, Alt>(
    originBlock: Block,
    alternativeBlock: Block,
) where Origin : Block, Alt : Block, Alt : EnchantableBlock {
    companion object {
        @JvmStatic
        val originBlockList = mutableListOf<Block>()
        private val alternativeBlockMapping: MutableMap<Block, Block> = mutableMapOf()

        @JvmStatic
        fun getAlternativeBlock(origin: Block): Block? {
            return alternativeBlockMapping[origin]
        }
    }

    constructor(mapping: Pair<BlockEntry<Origin>, BlockEntry<Alt>>) : this(mapping.first.get(), mapping.second.get())

    init {
        originBlockList.add(originBlock)
        alternativeBlockMapping[originBlock] = alternativeBlock
    }
}
