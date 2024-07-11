package io.github.cotrin8672.block

import com.simibubi.create.AllBlocks
import com.simibubi.create.content.kinetics.drill.DrillBlock
import com.simibubi.create.content.kinetics.drill.DrillBlockEntity
import com.simibubi.create.foundation.placement.IPlacementHelper
import com.simibubi.create.foundation.placement.PlacementHelpers
import com.simibubi.create.foundation.placement.PlacementOffset
import io.github.cotrin8672.registry.BlockEntityRegistration
import io.github.cotrin8672.registry.BlockRegistration
import io.github.cotrin8672.util.EnchantableBlockMapping
import io.github.cotrin8672.util.placeAlternativeBlockInWorld
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import java.util.function.Predicate
import kotlin.math.min

class EnchantableDrillBlock(properties: Properties) : DrillBlock(properties) {
    companion object {
        private val enchantedPlacementHelperId = PlacementHelpers.register(PlacementHelper())

        val EFFICIENCY: IntegerProperty = EnchantmentProperties.EFFICIENCY_LEVEL
        val FORTUNE: IntegerProperty = EnchantmentProperties.FORTUNE_LEVEL
        val SILK_TOUCH: IntegerProperty = EnchantmentProperties.SILK_TOUCH_LEVEL
    }

    override fun getName(): MutableComponent {
        return AllBlocks.MECHANICAL_DRILL.get().name
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(EFFICIENCY, FORTUNE, SILK_TOUCH)
        super.createBlockStateDefinition(builder)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        val stack = context.itemInHand
        var state = super.getStateForPlacement(context)
        val efficiency =
            min(stack.getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY), Enchantments.BLOCK_EFFICIENCY.maxLevel + 1)
        val fortune =
            min(stack.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE), Enchantments.BLOCK_FORTUNE.maxLevel + 1)
        val silkTouch = stack.getEnchantmentLevel(Enchantments.SILK_TOUCH)
        if (efficiency != 0) state = state?.setValue(EFFICIENCY, efficiency)
        if (fortune != 0) state = state?.setValue(FORTUNE, fortune)
        if (silkTouch != 0) state = state?.setValue(SILK_TOUCH, silkTouch)
        return state
    }

    override fun getBlockEntityType(): BlockEntityType<out DrillBlockEntity> {
        return BlockEntityRegistration.ENCHANTABLE_MECHANICAL_DRILL.get()
    }

    @Deprecated("Deprecated in Java")
    override fun getDrops(blockState: BlockState, builder: LootParams.Builder): MutableList<ItemStack> {
        val list = mutableListOf(
            ItemStack(BlockRegistration.ENCHANTABLE_MECHANICAL_DRILL.get().asItem()).apply {
                val efficiency = blockState.getValue(EFFICIENCY)
                val fortune = blockState.getValue(FORTUNE)
                val silkTouch = blockState.getValue(SILK_TOUCH)
                if (efficiency != 0) enchant(Enchantments.BLOCK_EFFICIENCY, efficiency)
                if (fortune != 0) enchant(Enchantments.BLOCK_FORTUNE, fortune)
                if (silkTouch != 0) enchant(Enchantments.SILK_TOUCH, silkTouch)
            }
        )
        return list
    }

    override fun asItem(): Item {
        return AllBlocks.MECHANICAL_DRILL.asItem()
    }

    override fun getCloneItemStack(
        state: BlockState, target: HitResult, level: BlockGetter, pos: BlockPos, player: Player,
    ): ItemStack {
        val efficiency = state.getValue(EFFICIENCY)
        val fortune = state.getValue(FORTUNE)
        val silkTouch = state.getValue(SILK_TOUCH)
        return ItemStack(AllBlocks.MECHANICAL_DRILL.asItem()).apply {
            if (efficiency != 0) enchant(Enchantments.BLOCK_EFFICIENCY, efficiency)
            if (fortune != 0) enchant(Enchantments.BLOCK_FORTUNE, fortune)
            if (silkTouch != 0) enchant(Enchantments.SILK_TOUCH, silkTouch)
        }
    }

    override fun use(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        ray: BlockHitResult,
    ): InteractionResult {
        val heldItem = player.getItemInHand(hand)
        val placementHelper = PlacementHelpers.get(enchantedPlacementHelperId)
        if (!player.isShiftKeyDown && player.mayBuild()) {
            if (placementHelper.matchesItem(heldItem)) {
                placementHelper.getOffset(player, world, state, pos, ray)
                    .placeAlternativeBlockInWorld(world, heldItem.item as BlockItem, player, hand, ray)
                return InteractionResult.SUCCESS
            }
        }
        return InteractionResult.PASS
    }

    private class PlacementHelper : IPlacementHelper {
        override fun getItemPredicate(): Predicate<ItemStack> {
            return Predicate { stack -> AllBlocks.MECHANICAL_DRILL.isIn(stack) }
        }

        override fun getStatePredicate(): Predicate<BlockState> {
            return Predicate { state -> BlockRegistration.ENCHANTABLE_MECHANICAL_DRILL.has(state) }
        }

        override fun getOffset(
            player: Player,
            world: Level,
            state: BlockState,
            pos: BlockPos,
            ray: BlockHitResult,
        ): PlacementOffset {
            val directions = IPlacementHelper.orderedByDistanceExceptAxis(
                pos,
                ray.location,
                state.getValue(FACING).axis
            ) { dir ->
                world.getBlockState(pos.relative(dir)).canBeReplaced()
            }

            return if (directions.isEmpty()) PlacementOffset.fail()
            else PlacementOffset.success(pos.relative(directions[0])) { blockState: BlockState ->
                blockState.setValue(FACING, blockState.getValue(FACING))
            }
        }

        override fun getOffset(
            player: Player,
            world: Level,
            state: BlockState,
            pos: BlockPos,
            ray: BlockHitResult,
            heldItem: ItemStack,
        ): PlacementOffset {
            var offset = getOffset(player, world, state, pos, ray)
            if (heldItem.item is BlockItem) {
                val blockItem = heldItem.item as BlockItem
                val block = EnchantableBlockMapping.getAlternativeBlock(blockItem.block) ?: blockItem.block
                offset = offset.withGhostState(
                    block.getStateForPlacement(
                        BlockPlaceContext(
                            player,
                            if (heldItem == player.mainHandItem) InteractionHand.MAIN_HAND else InteractionHand.OFF_HAND,
                            heldItem,
                            ray
                        )
                    )
                )
            }
            return offset
        }
    }
}
