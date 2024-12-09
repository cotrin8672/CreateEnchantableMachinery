package io.github.cotrin8672.createenchantablemachinery.platform

import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface BlockBreaker {
    companion object : KoinComponent {
        private val instance by inject<BlockBreaker>()

        operator fun invoke(level: ServerLevel, blockEntity: BlockBreakingKineticBlockEntity): Player {
            return instance.get(level, blockEntity)
        }
    }

    fun get(level: ServerLevel, blockEntity: BlockBreakingKineticBlockEntity): Player
}
