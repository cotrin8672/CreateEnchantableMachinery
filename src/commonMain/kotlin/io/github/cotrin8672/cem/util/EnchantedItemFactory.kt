package io.github.cotrin8672.cem.util

import com.simibubi.create.content.contraptions.behaviour.MovementContext
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

object EnchantedItemFactory {
    private val pickaxeCache: MutableMap<ListTag, ItemStack> = mutableMapOf()

    fun getPickaxeItemStack(enchantmentTag: ListTag?): ItemStack {
        enchantmentTag ?: return ItemStack.EMPTY

        return if (pickaxeCache.containsKey(enchantmentTag)) {
            pickaxeCache[enchantmentTag]!!
        } else {
            val stack = ItemStack(Items.NETHERITE_PICKAXE)
            val nbt = CompoundTag().apply {
                putByte("Unbreakable", 1)
                put("Enchantments", enchantmentTag)
            }
            stack.tag = nbt
            stack
        }
    }

    fun getPickaxeItemStack(movementContext: MovementContext): ItemStack {
        movementContext ?: return ItemStack.EMPTY
        val blockEntityData = movementContext.blockEntityData ?: return ItemStack.EMPTY
        val enchantmentTag =
            blockEntityData.getList(ItemStack.TAG_ENCH, Tag.TAG_COMPOUND.toInt()) ?: return ItemStack.EMPTY
        return getPickaxeItemStack(enchantmentTag)
    }
}
