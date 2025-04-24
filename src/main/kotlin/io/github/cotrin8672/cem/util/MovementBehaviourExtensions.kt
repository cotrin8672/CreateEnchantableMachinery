package io.github.cotrin8672.cem.util

import com.simibubi.create.content.contraptions.behaviour.MovementContext
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.NbtOps
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.ItemEnchantments

fun MovementContext.getEnchantmentLevel(enchantment: ResourceKey<Enchantment>): Int {
    val holderLookup = world.holderLookup(Registries.ENCHANTMENT)
    val holder = holderLookup.get(enchantment)
    if (holder.isEmpty) return 0
    if (!blockEntityData.contains("Enchantments")) return 0
    var itemEnchantments = ItemEnchantments.EMPTY
    ItemEnchantments.CODEC
        .parse(
            world.registryAccess().createSerializationContext(NbtOps.INSTANCE),
            blockEntityData.get("Enchantments")
        )
        .resultOrPartial()
        .ifPresent {
            itemEnchantments = it
        }
    return itemEnchantments.getLevel(holder.get())
}
