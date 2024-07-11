package io.github.cotrin8672.util

import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.level.block.Block

class EnchantableBlockMapping(
    originBlock: Block,
    alternativeBlock: Block,
    applicableEnchantments: List<Enchantment>,
) {
    companion object {
        @JvmStatic
        val originBlockList = mutableListOf<Block>()
        private val alternativeBlockMapping: MutableMap<Block, Block> = mutableMapOf()
        private val applicableEnchantmentsMapping: MutableMap<Block, List<Enchantment>> = mutableMapOf()

        @JvmStatic
        fun getAlternativeBlock(origin: Block): Block? {
            return alternativeBlockMapping[origin]
        }

        @JvmStatic
        fun getApplicableEnchantments(origin: Block): List<Enchantment> {
            return applicableEnchantmentsMapping[origin] ?: listOf()
        }
    }

    init {
        originBlockList.add(originBlock)
        alternativeBlockMapping[originBlock] = alternativeBlock
        applicableEnchantmentsMapping[originBlock] = applicableEnchantments
    }
}
