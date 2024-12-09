package io.github.cotrin8672.createenchantablemachinery.content.block

import net.minecraft.nbt.ListTag
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.EnchantmentInstance

interface EnchantableBlockEntity {
    fun getEnchantments(): List<EnchantmentInstance>

    fun setEnchantment(listTag: ListTag)

    fun getEnchantmentLevel(enchantment: Enchantment): Int {
        return getEnchantments().find { it.enchantment == enchantment }?.level ?: 0
    }
}
