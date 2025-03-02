package io.github.cotrin8672.cem.util

import it.unimi.dsi.fastutil.objects.Object2IntMap
import net.minecraft.core.Holder
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantment

object EnchantedItemFactory {
    private val pickaxeCache: MutableMap<Set<Object2IntMap.Entry<Holder<Enchantment>>>, ItemStack> = mutableMapOf()
    private val hoeCache: MutableMap<Set<Object2IntMap.Entry<Holder<Enchantment>>>, ItemStack> = mutableMapOf()

    fun getPickaxeItemStack(enchantmentSet: Set<Object2IntMap.Entry<Holder<Enchantment>>>): ItemStack {
        return if (pickaxeCache.containsKey(enchantmentSet)) {
            pickaxeCache[enchantmentSet]!!
        } else {
            val stack = ItemStack(Items.NETHERITE_PICKAXE).apply {
                for (instance in enchantmentSet) {
                    enchant(instance.key, instance.intValue)
                }
            }
            pickaxeCache[enchantmentSet] = stack
            stack
        }
    }

    fun getHoeItemStack(enchantmentSet: Set<Object2IntMap.Entry<Holder<Enchantment>>>): ItemStack {
        return if (hoeCache.containsKey(enchantmentSet)) {
            hoeCache[enchantmentSet]!!
        } else {
            val stack = ItemStack(Items.NETHERITE_HOE).apply {
                for (instance in enchantmentSet) {
                    enchant(instance.key, instance.intValue)
                }
            }
            hoeCache[enchantmentSet] = stack
            stack
        }
    }
}
