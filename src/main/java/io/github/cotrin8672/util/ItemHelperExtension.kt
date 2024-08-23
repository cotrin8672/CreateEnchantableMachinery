package io.github.cotrin8672.util

import net.minecraft.world.item.ItemStack

fun sameItem(stack: ItemStack, otherStack: ItemStack): Boolean {
    return !otherStack.isEmpty && stack.`is`(otherStack.item)
}
