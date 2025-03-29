package io.github.cotrin8672.cem.util

import com.simibubi.create.foundation.utility.AbstractBlockBreakQueue
import net.minecraft.core.BlockPos
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

fun AbstractBlockBreakQueue.destroyBlocks(
    level: Level,
    tool: ItemStack,
    callback: (BlockPos, ItemStack) -> Unit,
) {
    destroyBlocks(level, tool, null, callback)
}
