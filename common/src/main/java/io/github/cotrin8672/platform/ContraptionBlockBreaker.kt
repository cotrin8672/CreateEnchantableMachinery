package io.github.cotrin8672.platform

import com.simibubi.create.content.contraptions.behaviour.MovementContext
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface ContraptionBlockBreaker {
    companion object : KoinComponent {
        private val instance by inject<ContraptionBlockBreaker>()

        operator fun invoke(level: ServerLevel, context: MovementContext?) =
            instance.get(level, context)

        operator fun invoke(level: ServerLevel, context: MovementContext?, heldItem: ItemStack) =
            instance.get(level, context, heldItem)
    }

    fun get(level: ServerLevel, context: MovementContext?): Player

    fun get(level: ServerLevel, context: MovementContext?, heldItem: ItemStack): Player
}
