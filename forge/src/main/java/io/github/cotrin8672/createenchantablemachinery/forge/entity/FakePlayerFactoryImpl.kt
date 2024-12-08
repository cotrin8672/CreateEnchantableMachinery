package io.github.cotrin8672.createenchantablemachinery.forge.entity

import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity
import io.github.cotrin8672.content.entity.FakePlayerFactory
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

class FakePlayerFactoryImpl : FakePlayerFactory {
    override fun getBlockBreaker(level: ServerLevel, blockEntity: BlockBreakingKineticBlockEntity): Player {
        return BlockBreaker(level, blockEntity)
    }

    override fun getContraptionBlockBreaker(level: ServerLevel, context: MovementContext?): Player {
        return ContraptionBlockBreaker.getBlockBreakerForMovementContext(level, context)
    }

    override fun getContraptionBlockBreaker(
        level: ServerLevel,
        context: MovementContext?,
        heldItem: ItemStack,
    ): Player {
        return ContraptionBlockBreaker.getBlockBreakerForMovementContext(level, context, heldItem)
    }
}
