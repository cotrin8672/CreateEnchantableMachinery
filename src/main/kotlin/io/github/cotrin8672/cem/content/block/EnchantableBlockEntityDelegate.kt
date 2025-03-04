package io.github.cotrin8672.cem.content.block

import net.minecraft.world.item.enchantment.ItemEnchantments

class EnchantableBlockEntityDelegate : EnchantableBlockEntity {
    private var enchantments = ItemEnchantments.EMPTY

    override fun getEnchantments(): ItemEnchantments {
        return enchantments
    }

    override fun setEnchantment(enchantments: ItemEnchantments) {
        this.enchantments = enchantments
    }
}
