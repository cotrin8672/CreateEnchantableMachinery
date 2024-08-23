package io.github.cotrin8672.block

import com.simibubi.create.AllBlocks
import com.simibubi.create.content.kinetics.drill.DrillBlock
import com.simibubi.create.content.kinetics.drill.DrillBlockEntity
import com.simibubi.create.foundation.placement.IPlacementHelper
import com.simibubi.create.foundation.placement.PlacementHelpers
import com.simibubi.create.foundation.placement.PlacementOffset
import io.github.cotrin8672.blockentity.EnchantableBlockEntity
import io.github.cotrin8672.registry.BlockEntityRegistration
import io.github.cotrin8672.registry.BlockRegistration
import io.github.cotrin8672.util.placeAlternativeBlockInWorld
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.EnchantmentCategory
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import java.util.function.Predicate

class EnchantableDrillBlock(properties: Properties) : DrillBlock(properties), EnchantableBlock {
    companion object {
        private val enchantedPlacementHelperId = PlacementHelpers.register(PlacementHelper())
    }

    override fun getName(): MutableComponent {
        return AllBlocks.MECHANICAL_DRILL.get().name
    }

    override fun getBlockEntityType(): BlockEntityType<out DrillBlockEntity> {
        return BlockEntityRegistration.ENCHANTABLE_MECHANICAL_DRILL.get()
    }

    @Deprecated("Deprecated in Java")
    override fun getDrops(blockState: BlockState, builder: LootContext.Builder): MutableList<ItemStack> {
        val blockEntity = builder.getParameter(LootContextParams.BLOCK_ENTITY)
        val stack = ItemStack(AllBlocks.MECHANICAL_DRILL.get())
        if (blockEntity is EnchantableBlockEntity) {
            blockEntity.getEnchantments().forEach {
                stack.enchant(it.enchantment, it.level)
            }
        }
        return mutableListOf(stack)
    }

    override fun asItem(): Item {
        return AllBlocks.MECHANICAL_DRILL.get().asItem()
    }

    override fun getCloneItemStack(
        state: BlockState, target: HitResult, level: BlockGetter, pos: BlockPos, player: Player,
    ): ItemStack {
        val blockEntity = level.getBlockEntity(pos)
        val stack = ItemStack(AllBlocks.MECHANICAL_DRILL.get())
        if (blockEntity is EnchantableBlockEntity) {
            blockEntity.getEnchantments().forEach {
                stack.enchant(it.enchantment, it.level)
            }
        }
        return stack
    }

    @Deprecated("Deprecated in Java")
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
            blockEntity.setEnchantment(stack.enchantmentTags)
        }
    }

    override fun canApply(enchantment: Enchantment): Boolean {
        return when {
            enchantment == Enchantments.UNBREAKING -> false
            enchantment == Enchantments.MENDING -> false
            enchantment.category == EnchantmentCategory.DIGGER -> true
            else -> false
        }
    }

    private class PlacementHelper : IPlacementHelper {
        override fun getItemPredicate(): Predicate<ItemStack> {
            return Predicate { stack -> AllBlocks.MECHANICAL_DRILL.isIn(stack) }
        }

        override fun getStatePredicate(): Predicate<BlockState> {
            return Predicate { state -> BlockRegistration.ENCHANTABLE_MECHANICAL_DRILL.has(state) }
        }

        override fun getOffset(
            player: Player?, world: Level, state: BlockState, pos: BlockPos,
            ray: BlockHitResult,
        ): PlacementOffset {
            val directions = IPlacementHelper.orderedByDistanceExceptAxis(
                pos, ray.location,
                state.getValue(FACING)
                    .axis
            ) { dir: Direction ->
                world.getBlockState(pos.relative(dir)).material.isReplaceable
            }

            return if (directions.isEmpty()) PlacementOffset.fail()
            else {
                PlacementOffset.success(
                    pos.relative(directions[0])
                ) { s: BlockState ->
                    s.setValue(FACING, state.getValue(FACING))
                }
            }
        }
    }
}
