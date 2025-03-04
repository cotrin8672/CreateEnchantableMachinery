package io.github.cotrin8672.cem.content.block

import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.ItemEnchantments

interface EnchantableBlockEntity {
    fun getEnchantments(): ItemEnchantments

    fun setEnchantment(enchantments: ItemEnchantments)

    fun getEnchantmentLevel(enchantment: Holder<Enchantment>): Int {
        return getEnchantments().getLevel(enchantment)
    }

    fun readEnchantments(tag: CompoundTag, provider: HolderLookup.Provider): ItemEnchantments {
        if (tag.contains("Enchantments")) {
            val registryOps = provider.createSerializationContext(NbtOps.INSTANCE)
            var itemEnchantments: ItemEnchantments = ItemEnchantments.EMPTY
            ItemEnchantments.CODEC
                .parse(registryOps, tag.get("Enchantments"))
                .resultOrPartial()
                .ifPresent {
                    setEnchantment(it)
                    itemEnchantments = it
                }
            return itemEnchantments
        }
        return ItemEnchantments.EMPTY
    }

    fun writeEnchantments(tag: CompoundTag, provider: HolderLookup.Provider) {
        if (!getEnchantments().isEmpty) {
            val registryOps = provider.createSerializationContext(NbtOps.INSTANCE)
            ItemEnchantments.CODEC
                .encodeStart(registryOps, getEnchantments())
                .resultOrPartial()
                .ifPresent { tag.put("Enchantments", it) }
        }
    }
}
