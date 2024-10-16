package io.github.cotrin8672.forge.util

import com.simibubi.create.foundation.placement.IPlacementHelper
import com.simibubi.create.foundation.placement.PlacementOffset
import io.github.cotrin8672.util.EnchantableBlockMapping
import io.github.cotrin8672.util.interfaces.AlternativePlacementHelper
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.BlockHitResult
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.util.BlockSnapshot
import net.minecraftforge.event.level.BlockEvent

class AlternativePlacementHelperImpl : AlternativePlacementHelper {
    override fun placeAlternativeBlockInWorld(
        placementOffset: PlacementOffset,
        world: Level,
        blockItem: BlockItem,
        player: Player,
        hand: InteractionHand,
        ray: BlockHitResult,
    ): InteractionResult {
        with(placementOffset) {
            if (!isReplaceable(world)) return InteractionResult.PASS
            if (world.isClientSide) return InteractionResult.SUCCESS

            val context = UseOnContext(player, hand, ray)
            val newPos = BlockPos(pos)
            val stackBefore = player.getItemInHand(hand).copy()

            if (!world.mayInteract(player, newPos)) return InteractionResult.PASS

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

            val snapshot = BlockSnapshot.create(world.dimension(), world, newPos)
            world.setBlockAndUpdate(newPos, state)

            val event = BlockEvent.EntityPlaceEvent(snapshot, IPlacementHelper.ID, player)
            if (MinecraftForge.EVENT_BUS.post(event)) {
                snapshot.restore(true, false)
                return InteractionResult.FAIL
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

            return InteractionResult.SUCCESS
        }
    }
}
