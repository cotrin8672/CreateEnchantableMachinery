package io.github.cotrin8672.cem.content.plough

import com.zurrtum.create.foundation.blockEntity.SyncedBlockEntity
import io.github.cotrin8672.cem.registry.CemBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

/** Holds only the vanilla components (enchantments) of a placed plough; it has no data of its own. */
class PloughEnchantmentBlockEntity(pos: BlockPos, state: BlockState) :
    SyncedBlockEntity(CemBlockEntityTypes.PLOUGH, pos, state)
