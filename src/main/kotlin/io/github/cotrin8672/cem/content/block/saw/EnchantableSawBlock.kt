package io.github.cotrin8672.cem.content.block.saw

import com.simibubi.create.AllBlocks
import com.simibubi.create.content.kinetics.saw.SawBlock
import com.simibubi.create.content.kinetics.saw.SawBlockEntity
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity
import io.github.cotrin8672.cem.registry.BlockEntityRegistration
import io.github.cotrin8672.cem.registry.BlockRegistration
import io.github.cotrin8672.cem.util.placeAlternativeBlockInWorld
import net.createmod.catnip.placement.IPlacementHelper
import net.createmod.catnip.placement.PlacementHelpers
import net.createmod.catnip.placement.PlacementOffset
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.ItemEnchantments
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import java.util.function.Predicate

class EnchantableSawBlock(properties: Properties) : SawBlock(properties) {
    companion object {
        private val placementHelperId = PlacementHelpers.register(PlacementHelper())
    }

    override fun getName(): MutableComponent {
        return AllBlocks.MECHANICAL_SAW.get().name
    }

    override fun getBlockEntityType(): BlockEntityType<out SawBlockEntity> {
        return BlockEntityRegistration.ENCHANTABLE_MECHANICAL_SAW.get()
    }

    override fun asItem(): Item {
        return AllBlocks.MECHANICAL_SAW.asItem()
    }

    override fun getCloneItemStack(
        state: BlockState,
        target: HitResult,
        level: LevelReader,
        pos: BlockPos,
        player: Player,
    ): ItemStack {
        val blockEntity = level.getBlockEntity(pos)
        val stack = ItemStack(AllBlocks.MECHANICAL_SAW)
        if (blockEntity is EnchantableBlockEntity) {
            val enchantments = blockEntity.getEnchantments().entrySet()
            enchantments.forEach {
                stack.enchant(it.key, it.intValue)
            }
        }
        return stack
    }

    public override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult,
    ): ItemInteractionResult {
        if (!player.getItemInHand(hand).isEnchanted)
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult)

        val heldItem = player.getItemInHand(hand)
        val placementHelper = PlacementHelpers.get(placementHelperId)
        if (!player.isShiftKeyDown && player.mayBuild()) {
            if (placementHelper.matchesItem(heldItem)) {
                placementHelper.getOffset(player, level, state, pos, hitResult)
                    .placeAlternativeBlockInWorld(level, heldItem.item as BlockItem, player, hand, hitResult)
                return ItemInteractionResult.SUCCESS
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
    }

    override fun setPlacedBy(
        worldIn: Level,
        pos: BlockPos,
        state: BlockState,
        placer: LivingEntity?,
        stack: ItemStack,
    ) {
        super.setPlacedBy(worldIn, pos, state, placer, stack)
        val blockEntity = worldIn.getBlockEntity(pos)
        if (blockEntity is EnchantableBlockEntity) {
            blockEntity.setEnchantment(stack.get(DataComponents.ENCHANTMENTS) ?: ItemEnchantments.EMPTY)
        }
    }

    private class PlacementHelper : IPlacementHelper {
        override fun getItemPredicate(): Predicate<ItemStack> {
            return Predicate { stack -> AllBlocks.MECHANICAL_SAW.isIn(stack) }
        }

        override fun getStatePredicate(): Predicate<BlockState> {
            return Predicate { state -> BlockRegistration.ENCHANTABLE_MECHANICAL_SAW.has(state) }
        }

        override fun getOffset(
            player: Player,
            world: Level,
            state: BlockState,
            pos: BlockPos,
            ray: BlockHitResult,
        ): PlacementOffset {
            val directions = IPlacementHelper.orderedByDistanceExceptAxis(
                pos, ray.location,
                state.getValue(FACING).axis
            ) { dir: Direction ->
                world.getBlockState(pos.relative(dir)).canBeReplaced()
            }

            return if (directions.isEmpty()) PlacementOffset.fail()
            else {
                PlacementOffset.success(pos.relative(directions[0])) { s: BlockState ->
                    s.setValue(FACING, state.getValue(FACING))
                        .setValue(AXIS_ALONG_FIRST_COORDINATE, state.getValue(AXIS_ALONG_FIRST_COORDINATE))
                        .setValue(FLIPPED, state.getValue(FLIPPED))
                }
            }
        }
    }
}
