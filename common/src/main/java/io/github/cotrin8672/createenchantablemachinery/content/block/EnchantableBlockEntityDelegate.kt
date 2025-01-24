package io.github.cotrin8672.createenchantablemachinery.content.block

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.EnchantmentInstance

class EnchantableBlockEntityDelegate : EnchantableBlockEntity {
    private var enchantmentsTag: ListTag? = null
    private val enchantmentInstances: List<EnchantmentInstance>
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
    }

    override fun readEnchantments(compound: CompoundTag) {
        enchantmentsTag = compound.getList(ItemStack.TAG_ENCH, Tag.TAG_COMPOUND.toInt())
    }

    override fun writeEnchantments(compound: CompoundTag) {
        enchantmentsTag?.let { compound.put(ItemStack.TAG_ENCH, it) }
    }
}
