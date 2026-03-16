package io.github.cotrin8672.cem.util

import com.simibubi.create.content.contraptions.behaviour.MovementContext
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.Unbreakable
import net.minecraft.world.item.enchantment.ItemEnchantments

object EnchantedItemFactory {
    private val pickaxeCache: MutableMap<ItemEnchantments, ItemStack> = mutableMapOf()
    private val toolCache: MutableMap<Pair<Item, ItemEnchantments>, ItemStack> = mutableMapOf()

    fun getPickaxeItemStack(enchantmentSet: ItemEnchantments): ItemStack {
        return pickaxeCache.getOrPut(enchantmentSet) {
            ItemStack(Items.NETHERITE_PICKAXE).apply {
                if (enchantmentSet.isEmpty) return@apply
                set(DataComponents.UNBREAKABLE, Unbreakable(false))
                set(DataComponents.ENCHANTMENTS, enchantmentSet)
            }
        }
    }

    fun getPickaxeItemStack(tag: CompoundTag?, context: MovementContext?): ItemStack {
        if (tag == null) return getPickaxeItemStack(ItemEnchantments.EMPTY)
        if (context == null) return getPickaxeItemStack(ItemEnchantments.EMPTY)
        if (context.temporaryData is ItemStack) return context.temporaryData as ItemStack

        val enchantments = getEnchantments(tag, context)

        val stack = getPickaxeItemStack(enchantments)
        context.temporaryData = stack
        return stack
    }

    fun getToolItemStack(tool: Item, enchantmentSet: ItemEnchantments): ItemStack {
        val key = Pair(tool, enchantmentSet)
        return toolCache.getOrPut(key) {
            ItemStack(tool).apply {
                if (enchantmentSet.isEmpty) return@apply
                set(DataComponents.UNBREAKABLE, Unbreakable(false))
                set(DataComponents.ENCHANTMENTS, enchantmentSet)
            }
        }
    }

    fun getEnchantments(tag: CompoundTag?, context: MovementContext?): ItemEnchantments {
        if (tag == null || context == null) return ItemEnchantments.EMPTY
        var enchantments: ItemEnchantments = ItemEnchantments.EMPTY
        val registryOps = context.world.registryAccess().createSerializationContext(NbtOps.INSTANCE)
        ItemEnchantments.CODEC
            .parse(registryOps, tag.get("Enchantments"))
            .resultOrPartial()
            .ifPresent { enchantments = it }
        return enchantments
    }

    fun clearCache() {
        pickaxeCache.clear()
        toolCache.clear()
    }
}
