package io.github.cotrin8672.blockentity

import net.minecraft.nbt.ListTag
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.EnchantmentInstance

class EnchantableBlockEntityDelegate : EnchantableBlockEntity {
    var enchantmentsTag: ListTag? = null
    val enchantmentInstances: List<EnchantmentInstance>
        get() = enchantmentsTag?.let { tag ->
            EnchantmentHelper.deserializeEnchantments(tag).map {
                EnchantmentInstance(it.key, it.value)
            }
        } ?: listOf()

    override fun getEnchantments(): List<EnchantmentInstance> {
        return enchantmentInstances
    }

    override fun setEnchantment(listTag: ListTag) {
        enchantmentsTag = listTag
        println(listTag.toString())
    }
}
