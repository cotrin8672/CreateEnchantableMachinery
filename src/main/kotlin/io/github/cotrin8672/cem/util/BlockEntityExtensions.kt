package io.github.cotrin8672.cem.util

import net.minecraft.core.HolderLookup
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.entity.BlockEntity

fun <T> BlockEntity.holderLookup(registryKey: ResourceKey<out Registry<out T>>): HolderLookup<T> =
    checkNotNull(level).holderLookup(registryKey)
