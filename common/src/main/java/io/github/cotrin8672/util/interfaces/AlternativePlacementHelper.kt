package io.github.cotrin8672.util.interfaces

import com.simibubi.create.foundation.placement.PlacementOffset
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface AlternativePlacementHelper {
    companion object : KoinComponent {
        private val instance: AlternativePlacementHelper by inject()

        operator fun invoke() = instance
    }

    fun placeAlternativeBlockInWorld(
        placementOffset: PlacementOffset,
        world: Level,
        blockItem: BlockItem,
        player: Player,
        hand: InteractionHand,
        ray: BlockHitResult,
    ): InteractionResult
}
