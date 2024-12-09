package io.github.cotrin8672.createenchantablemachinery.fabric.platform.entity

import com.simibubi.create.content.contraptions.behaviour.MovementContext
import io.github.cotrin8672.createenchantablemachinery.platform.ContraptionBlockBreaker
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

class ContraptionBlockBreakerImpl : ContraptionBlockBreaker {
    override fun get(level: ServerLevel, context: MovementContext?): Player {
        return ContraptionBlockBreakPlayer.getBlockBreakerForMovementContext(level, context)
    }

    override fun get(level: ServerLevel, context: MovementContext?, heldItem: ItemStack): Player {
        return ContraptionBlockBreakPlayer.getBlockBreakerForMovementContext(level, context, heldItem)
    }
}
