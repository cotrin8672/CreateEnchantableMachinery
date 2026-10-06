package io.github.cotrin8672.cem.util

import net.minecraft.world.level.block.entity.BlockEntity

/** Bridges from common code into the client source set, which sets these callbacks during client init. */
object CemHooks {
    @JvmStatic
    var clientEnchantmentsChanged: (BlockEntity) -> Unit = {}

    @JvmStatic
    fun onClientEnchantmentsChanged(blockEntity: BlockEntity) {
        if (blockEntity.level?.isClientSide() == true) clientEnchantmentsChanged(blockEntity)
    }
}
