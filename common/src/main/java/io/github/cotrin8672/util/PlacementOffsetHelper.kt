package io.github.cotrin8672.util

import com.simibubi.create.foundation.placement.PlacementOffset
import io.github.cotrin8672.CreateEnchantableMachinery
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult

fun PlacementOffset.placeAlternativeBlockInWorld(
    world: Level,
    blockItem: BlockItem,
    player: Player,
    hand: InteractionHand,
    ray: BlockHitResult,
): InteractionResult {
    return CreateEnchantableMachinery.alternativePlacementHelper.placeAlternativeBlockInWorld(
        this, world, blockItem, player, hand, ray
    )
}
