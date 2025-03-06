package io.github.cotrin8672.cem.util

import com.simibubi.create.content.contraptions.behaviour.MovementContext
import it.unimi.dsi.fastutil.objects.Object2IntMap
import net.minecraft.core.Holder
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.ItemEnchantments

object EnchantedItemFactory {
    private val pickaxeCache: MutableMap<Set<Object2IntMap.Entry<Holder<Enchantment>>>, ItemStack> = mutableMapOf()
    private val hoeCache: MutableMap<Set<Object2IntMap.Entry<Holder<Enchantment>>>, ItemStack> = mutableMapOf()

    fun getPickaxeItemStack(enchantmentSet: Set<Object2IntMap.Entry<Holder<Enchantment>>>): ItemStack {
        return if (pickaxeCache.containsKey(enchantmentSet)) {
            pickaxeCache[enchantmentSet]!!
        } else {
            val stack = ItemStack(Items.NETHERITE_PICKAXE).apply {
                if (enchantmentSet.isEmpty()) return@apply
                for (instance in enchantmentSet) {
                    enchant(instance.key, instance.intValue)
                }
            }
            pickaxeCache[enchantmentSet] = stack
            stack
        }
    }

    fun getPickaxeItemStack(tag: CompoundTag?, context: MovementContext?): ItemStack {
        if (tag == null) return getPickaxeItemStack(setOf())
        if (context == null) return getPickaxeItemStack(setOf())
        var enchantments: ItemEnchantments = ItemEnchantments.EMPTY
        val registryOps = context.world.registryAccess().createSerializationContext(NbtOps.INSTANCE)
        ItemEnchantments.CODEC
            .parse(registryOps, tag.get("Enchantments"))
            .resultOrPartial()
            .ifPresent { enchantments = it }

        return getPickaxeItemStack(enchantments.entrySet())
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
