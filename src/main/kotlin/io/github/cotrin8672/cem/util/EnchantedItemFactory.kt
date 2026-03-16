package io.github.cotrin8672.cem.util

import com.simibubi.create.content.contraptions.behaviour.MovementContext
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

object EnchantedItemFactory {
    private val pickaxeCache: MutableMap<ListTag, ItemStack> = mutableMapOf()
    private val toolCache: MutableMap<Pair<Item, ListTag>, ItemStack> = mutableMapOf()

    fun getPickaxeItemStack(enchantmentTag: ListTag?): ItemStack {
        enchantmentTag ?: return ItemStack(Items.NETHERITE_PICKAXE)
        return pickaxeCache.getOrPut(enchantmentTag) {
            ItemStack(Items.NETHERITE_PICKAXE).apply {
                tag = CompoundTag().apply {
                    putByte("Unbreakable", 1)
                    put(ItemStack.TAG_ENCH, enchantmentTag.copy())
                }
            }
        }
    }

    fun getPickaxeItemStack(tag: CompoundTag?, context: MovementContext?): ItemStack {
        if (context?.temporaryData is ItemStack) return context.temporaryData as ItemStack
        val enchantmentTag = getEnchantments(tag, context)
        val stack = getPickaxeItemStack(enchantmentTag)
        if (context != null) context.temporaryData = stack
        return stack
    }

    fun getPickaxeItemStack(movementContext: MovementContext?): ItemStack {
        return getPickaxeItemStack(movementContext?.blockEntityData, movementContext)
    }

    fun getToolItemStack(tool: Item, enchantmentTag: ListTag?): ItemStack {
        enchantmentTag ?: return ItemStack(tool)
        val key = Pair(tool, enchantmentTag)
        return toolCache.getOrPut(key) {
            ItemStack(tool).apply {
                tag = CompoundTag().apply {
                    putByte("Unbreakable", 1)
                    put(ItemStack.TAG_ENCH, enchantmentTag.copy())
                }
            }
        }
    }

    fun getEnchantments(tag: CompoundTag?, context: MovementContext?): ListTag {
        if (tag == null || context == null) return ListTag()
        return tag.getList(ItemStack.TAG_ENCH, Tag.TAG_COMPOUND.toInt())
    }

    fun clearCache() {
        pickaxeCache.clear()
        toolCache.clear()
    }
}
