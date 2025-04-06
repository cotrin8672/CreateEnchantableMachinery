package io.github.cotrin8672.cem.util

import com.simibubi.create.content.contraptions.behaviour.MovementContext
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.Unbreakable
import net.minecraft.world.item.enchantment.ItemEnchantments

object EnchantedItemFactory {
    private val pickaxeCache: MutableMap<ItemEnchantments, ItemStack> = mutableMapOf()
    private val hoeCache: MutableMap<ItemEnchantments, ItemStack> = mutableMapOf()

    fun getPickaxeItemStack(enchantmentSet: ItemEnchantments): ItemStack {
        return if (pickaxeCache.containsKey(enchantmentSet)) {
            pickaxeCache[enchantmentSet]!!
        } else {
            val stack = ItemStack(Items.NETHERITE_PICKAXE).apply {
                if (enchantmentSet.isEmpty) return@apply
                set(DataComponents.UNBREAKABLE, Unbreakable(false))
                set(DataComponents.ENCHANTMENTS, enchantmentSet)
            }
            pickaxeCache[enchantmentSet] = stack
            stack
        }
    }

    fun getPickaxeItemStack(tag: CompoundTag?, context: MovementContext?): ItemStack {
        if (tag == null) return getPickaxeItemStack(ItemEnchantments.EMPTY)
        if (context == null) return getPickaxeItemStack(ItemEnchantments.EMPTY)
        if (context.temporaryData is ItemStack) return context.temporaryData as ItemStack

        var enchantments: ItemEnchantments = ItemEnchantments.EMPTY
        val registryOps = context.world.registryAccess().createSerializationContext(NbtOps.INSTANCE)
        ItemEnchantments.CODEC
            .parse(registryOps, tag.get("Enchantments"))
            .resultOrPartial()
            .ifPresent { enchantments = it }

        val stack = getPickaxeItemStack(enchantments)
        context.temporaryData = stack
        return stack
    }
}
