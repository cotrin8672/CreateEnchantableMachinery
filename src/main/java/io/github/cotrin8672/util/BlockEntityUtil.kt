package io.github.cotrin8672.util

import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity

val BlockEntity.nonNullLevel: Level
    get() = checkNotNull(level)
