package io.github.cotrin8672.util

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.EnchantmentInstance

object EnchantedItemFactory {
    private val pickaxeCache: MutableMap<List<EnchantmentInstance>, ItemStack> = mutableMapOf()
    private val hoeCache: MutableMap<List<EnchantmentInstance>, ItemStack> = mutableMapOf()

    fun getPickaxeItemStack(vararg instances: EnchantmentInstance): ItemStack {
        val key = instances.toList().distinctBy {
            it.enchantment
        }
        return if (pickaxeCache.containsKey(key)) {
            pickaxeCache[key]!!
        } else {
            val stack = ItemStack(Items.NETHERITE_PICKAXE).apply {
                for (instance in instances) {
                    enchant(instance.enchantment, instance.level)
                }
            }
            pickaxeCache[key] = stack
            stack
        }
    }
    
    fun getHoeItemStack(vararg instances: EnchantmentInstance): ItemStack {
        val key = instances.toList().distinctBy {
            it.enchantment
        }
        return if (hoeCache.containsKey(key)) {
            hoeCache[key]!!
        } else {
            val stack = ItemStack(Items.NETHERITE_HOE).apply {
                for (instance in instances) {
                    enchant(instance.enchantment, instance.level)
                }
            }
            hoeCache[key] = stack
            stack
        }
    }
}
