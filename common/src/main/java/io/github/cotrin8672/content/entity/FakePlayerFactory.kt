package io.github.cotrin8672.content.entity

import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

interface FakePlayerFactory {
    fun getBlockBreaker(level: ServerLevel, blockEntity: BlockBreakingKineticBlockEntity): Player

    fun getContraptionBlockBreaker(level: ServerLevel, context: MovementContext?): Player

    fun getContraptionBlockBreaker(level: ServerLevel, context: MovementContext?, heldItem: ItemStack): Player
}
