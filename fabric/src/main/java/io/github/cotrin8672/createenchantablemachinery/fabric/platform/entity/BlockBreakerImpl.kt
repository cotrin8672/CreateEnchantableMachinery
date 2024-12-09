package io.github.cotrin8672.createenchantablemachinery.fabric.platform.entity

import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity
import io.github.cotrin8672.createenchantablemachinery.platform.BlockBreaker
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player

class BlockBreakerImpl : BlockBreaker {
    override fun get(level: ServerLevel, blockEntity: BlockBreakingKineticBlockEntity): Player {
        return BlockBreakPlayer(level, blockEntity)
    }
}
