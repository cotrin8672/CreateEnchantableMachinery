package io.github.cotrin8672.cem.content.block

import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.ItemEnchantments

interface EnchantableBlockEntity {
    fun getEnchantments(): ItemEnchantments

    fun setEnchantment(enchantments: ItemEnchantments)

    fun getSourceItem(): Item?

    fun setSourceItem(item: Item?)

    fun getEnchantmentLevel(enchantment: Holder<Enchantment>): Int {
        return getEnchantments().getLevel(enchantment)
    }

    fun readEnchantments(tag: CompoundTag, provider: HolderLookup.Provider) {
        if (tag.contains("Enchantments")) {
            val registryOps = provider.createSerializationContext(NbtOps.INSTANCE)
            ItemEnchantments.CODEC
                .parse(registryOps, tag.get("Enchantments"))
                .resultOrPartial()
                .ifPresent {
                    setEnchantment(it)
                }
        }
        if (tag.contains("SourceItem")) {
            val itemId = tag.getString("SourceItem")
            val location = ResourceLocation.tryParse(itemId)
            if (location != null && BuiltInRegistries.ITEM.containsKey(location)) {
                setSourceItem(BuiltInRegistries.ITEM.get(location))
            } else {
                setSourceItem(null)
            }
        } else {
            setSourceItem(null)
        }
    }

    fun writeEnchantments(tag: CompoundTag, provider: HolderLookup.Provider) {
        if (!getEnchantments().isEmpty) {
            val registryOps = provider.createSerializationContext(NbtOps.INSTANCE)
            ItemEnchantments.CODEC
                .encodeStart(registryOps, getEnchantments())
                .resultOrPartial()
                .ifPresent { tag.put("Enchantments", it) }
        }
        val sourceItem = getSourceItem()
        if (sourceItem != null) {
            tag.putString("SourceItem", BuiltInRegistries.ITEM.getKey(sourceItem).toString())
        }
    }
}
