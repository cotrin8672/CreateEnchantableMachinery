package io.github.cotrin8672.block

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.EnchantmentInstance

object EnchantedItemFactory {
    private val cache: MutableMap<List<EnchantmentInstance>, ItemStack> = mutableMapOf()

    fun getPickaxeItemStack(vararg instances: EnchantmentInstance): ItemStack {
        val key = instances.toList().distinctBy {
            it.enchantment
        }
        return if (cache.containsKey(key)) {
            cache[key]!!
        } else {
            val stack = ItemStack(Items.NETHERITE_PICKAXE).apply {
                for (instance in instances) {
                    enchant(instance.enchantment, instance.level)
                }
            }
            cache[key] = stack
            stack
        }
    }
}
