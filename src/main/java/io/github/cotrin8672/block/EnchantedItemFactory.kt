package io.github.cotrin8672.block

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantments

object EnchantedItemFactory {
    private val enchantedPickaxes: MutableMap<Triple<Int, Int, Int>, ItemStack> = mutableMapOf()

    fun getPickaxeItemStack(efficiencyLevel: Int, fortuneLevel: Int, silkTouchLevel: Int): ItemStack {
        val key = Triple(efficiencyLevel, fortuneLevel, silkTouchLevel)
        return if (enchantedPickaxes.containsKey(key)) {
            enchantedPickaxes[key]!!
        } else {
            val stack = ItemStack(Items.DIAMOND_PICKAXE).apply {
                if (efficiencyLevel != 0) enchant(Enchantments.BLOCK_EFFICIENCY, efficiencyLevel)
                if (fortuneLevel != 0) enchant(Enchantments.BLOCK_FORTUNE, fortuneLevel)
                if (silkTouchLevel != 0) enchant(Enchantments.SILK_TOUCH, silkTouchLevel)
            }
            enchantedPickaxes[key] = stack
            stack
        }
    }
}
