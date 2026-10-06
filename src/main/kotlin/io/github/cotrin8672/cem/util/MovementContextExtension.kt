package io.github.cotrin8672.cem.util

import net.minecraft.world.item.enchantment.ItemEnchantments

/** Implemented on Create's `MovementContext` by a mixin; caches the actor's enchantments. */
interface MovementContextExtension {
    fun cem_getEnchantments(): ItemEnchantments
}
