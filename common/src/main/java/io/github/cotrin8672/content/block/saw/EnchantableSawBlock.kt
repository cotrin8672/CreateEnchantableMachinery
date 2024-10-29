package io.github.cotrin8672.content.block.saw

import com.simibubi.create.AllBlocks
import com.simibubi.create.content.kinetics.saw.SawBlock
import com.simibubi.create.content.kinetics.saw.SawBlockEntity
import com.simibubi.create.foundation.placement.IPlacementHelper
import com.simibubi.create.foundation.placement.PlacementHelpers
import com.simibubi.create.foundation.placement.PlacementOffset
import io.github.cotrin8672.CreateEnchantableMachinery.itemStackHandlerHelper
import io.github.cotrin8672.content.block.EnchantableBlock
import io.github.cotrin8672.content.block.EnchantableBlockEntity
import io.github.cotrin8672.registrate.BlockEntityRegistration
import io.github.cotrin8672.registrate.BlockRegistration
import io.github.cotrin8672.util.extension.placeAlternativeBlockInWorld
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
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
import java.util.function.Predicate

class EnchantableSawBlock(properties: Properties) : SawBlock(properties), EnchantableBlock {
    companion object {
        private val placementHelperId = PlacementHelpers.register(PlacementHelper())
    }

    override fun getBlockEntityType(): BlockEntityType<out SawBlockEntity> {
        return BlockEntityRegistration.ENCHANTABLE_MECHANICAL_SAW.get()
    }

    @Deprecated("Deprecated in Java")
    override fun getDrops(blockState: BlockState, builder: LootContext.Builder): MutableList<ItemStack> {
        val blockEntity = builder.getParameter(LootContextParams.BLOCK_ENTITY)
        val stack = ItemStack(AllBlocks.MECHANICAL_SAW.get())
        if (blockEntity is EnchantableBlockEntity) {
            blockEntity.getEnchantments().forEach {
                stack.enchant(it.enchantment, it.level)
            }
        }
        return mutableListOf(stack)
    }

    override fun asItem(): Item {
        return AllBlocks.MECHANICAL_SAW.get().asItem()
    }

    override fun getCloneItemStack(level: BlockGetter, pos: BlockPos, state: BlockState): ItemStack {
        val blockEntity = level.getBlockEntity(pos)
        val stack = ItemStack(AllBlocks.MECHANICAL_SAW.get())
        if (blockEntity is EnchantableBlockEntity) {
            blockEntity.getEnchantments().forEach {
                stack.enchant(it.enchantment, it.level)
            }
        }
        return stack
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

    @Deprecated("Deprecated in Java")
    override fun use(
        state: BlockState,
        worldIn: Level,
        pos: BlockPos,
        player: Player,
        handIn: InteractionHand,
        hit: BlockHitResult,
    ): InteractionResult {
        if (!player.getItemInHand(handIn).isEnchanted) return super.use(state, worldIn, pos, player, handIn, hit)

        val heldItem = player.getItemInHand(handIn)
        val placementHelper = PlacementHelpers.get(placementHelperId)
        if (!player.isShiftKeyDown && player.mayBuild()) {
            if (
                placementHelper.matchesItem(heldItem) && placementHelper.getOffset(player, worldIn, state, pos, hit)
                    .placeAlternativeBlockInWorld(worldIn, heldItem.item as BlockItem, player, handIn, hit)
                    .consumesAction()
            )
                return InteractionResult.SUCCESS
        }

        if (player.isSpectator || !player.getItemInHand(handIn).isEmpty) return InteractionResult.PASS
        if (state.getOptionalValue(FACING).orElse(Direction.WEST) != Direction.UP) return InteractionResult.PASS

        return onBlockEntityUse(worldIn, pos) { be: SawBlockEntity ->
            for (i in 0 until itemStackHandlerHelper.getSlots(be.inventory)) {
                val heldItemStack = be.inventory.getStackInSlot(i)
                if (!worldIn.isClientSide && !heldItemStack.isEmpty)
                    player.inventory.placeItemBackInInventory(heldItemStack)
            }
            be.inventory.clear()
            be.notifyUpdate()
            InteractionResult.SUCCESS
        }
    }

    override fun canApply(enchantment: Enchantment): Boolean {
        return when {
            enchantment == Enchantments.BLOCK_EFFICIENCY -> true
            enchantment.category == EnchantmentCategory.DIGGER -> true
            else -> false
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
                world.getBlockState(pos.relative(dir)).material.isReplaceable
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
