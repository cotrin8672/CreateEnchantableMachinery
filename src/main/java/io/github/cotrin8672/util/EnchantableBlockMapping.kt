package io.github.cotrin8672.util

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

    init {
        originBlockList.add(originBlock)
        alternativeBlockMapping[originBlock] = alternativeBlock
    }
}
