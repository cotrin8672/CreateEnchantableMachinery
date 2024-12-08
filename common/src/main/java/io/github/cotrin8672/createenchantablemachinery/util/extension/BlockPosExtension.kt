package io.github.cotrin8672.createenchantablemachinery.util.extension

import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3

val BlockPos.center: Vec3
    get() = Vec3(x.toDouble() + 0.5, y.toDouble() + 0.5, z.toDouble() + 0.5)
