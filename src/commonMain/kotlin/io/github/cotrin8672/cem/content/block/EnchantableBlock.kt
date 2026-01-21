package io.github.cotrin8672.cem.content.block

import net.minecraft.world.item.enchantment.Enchantment

interface EnchantableBlock {
    fun canApply(enchantment: Enchantment): Boolean
}
