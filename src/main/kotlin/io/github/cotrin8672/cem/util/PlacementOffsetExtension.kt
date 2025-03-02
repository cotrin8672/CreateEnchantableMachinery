package io.github.cotrin8672.cem.util

import net.createmod.catnip.placement.PlacementOffset
import net.createmod.catnip.platform.CatnipServices
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.BlockHitResult

fun PlacementOffset.placeAlternativeBlockInWorld(
    world: Level,
    blockItem: BlockItem,
    player: Player,
    hand: InteractionHand,
    ray: BlockHitResult,
): ItemInteractionResult {
    if (!isReplaceable(world)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
    if (world.isClientSide) return ItemInteractionResult.SUCCESS

    val context = UseOnContext(player, hand, ray)
    val newPos = BlockPos(pos)
    val stackBefore = player.getItemInHand(hand).copy()

    if (!world.mayInteract(player, newPos)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION

    var state = transform.apply(
        EnchantableBlockMapping.getAlternativeBlock(blockItem.block)?.getStateForPlacement(
            BlockPlaceContext(
                player,
                hand,
                player.getItemInHand(hand),
                ray
            )
        )
    )
    if (state.hasProperty(BlockStateProperties.WATERLOGGED)) {
        val fluidState = world.getFluidState(newPos)
        state = state.setValue(BlockStateProperties.WATERLOGGED, fluidState.type == Fluids.WATER)
    }

    if (CatnipServices.HOOKS.playerPlaceSingleBlock(player, world, newPos, state)) {
        return ItemInteractionResult.FAIL
    }

    val newState = world.getBlockState(newPos)
    val soundType = newState.getSoundType(world, newPos, player)
    world.playSound(
        null,
        newPos,
        soundType.placeSound,
        SoundSource.BLOCKS,
        (soundType.volume + 1f) / 2f,
        soundType.pitch * 0.8f
    )

    player.awardStat(Stats.ITEM_USED.get(blockItem))
    newState.block.setPlacedBy(world, newPos, newState, player, stackBefore)

    if (player is ServerPlayer) CriteriaTriggers.PLACED_BLOCK.trigger(player, newPos, context.itemInHand)
    if (!player.isCreative) context.itemInHand.shrink(1)

    return ItemInteractionResult.SUCCESS
}
