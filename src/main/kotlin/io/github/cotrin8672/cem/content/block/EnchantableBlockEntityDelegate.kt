package io.github.cotrin8672.cem.content.block

import net.minecraft.world.item.Item
import net.minecraft.world.item.enchantment.ItemEnchantments

open class EnchantableBlockEntityDelegate : EnchantableBlockEntity {
    private var enchantments: ItemEnchantments = ItemEnchantments.EMPTY
    private var sourceItem: Item? = null

    override fun getEnchantments(): ItemEnchantments {
        return enchantments
    }

    override fun setEnchantment(enchantments: ItemEnchantments) {
        this.enchantments = enchantments
    }

    override fun getSourceItem(): Item? {
        return sourceItem
    }

    override fun setSourceItem(item: Item?) {
        sourceItem = item
    }
}
