package io.github.cotrin8672.cem.content.block.roller

import com.simibubi.create.AllBlocks
import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement
import com.simibubi.create.content.contraptions.actors.roller.RollerBlock
import com.simibubi.create.content.contraptions.actors.roller.RollerBlockEntity
import com.simibubi.create.content.schematics.requirement.ItemRequirement
import com.simibubi.create.foundation.placement.PoleHelper
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity
import io.github.cotrin8672.cem.registry.BlockEntityRegistration
import io.github.cotrin8672.cem.registry.BlockRegistration
import io.github.cotrin8672.cem.util.placeAlternativeBlockInWorld
import net.createmod.catnip.placement.PlacementHelpers
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.component.DataComponentMap
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
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import java.util.function.Predicate

class EnchantableRollerBlock(properties: Properties) : RollerBlock(properties), SpecialBlockItemRequirement {
    companion object {
        private val placementHelperId = PlacementHelpers.register(PlacementHelper())
    }

    override fun getName(): MutableComponent {
        return AllBlocks.MECHANICAL_ROLLER.get().name
    }

    override fun getBlockEntityType(): BlockEntityType<out RollerBlockEntity> {
        return BlockEntityRegistration.ENCHANTABLE_MECHANICAL_ROLLER.get()
    }

    override fun asItem(): Item {
        return AllBlocks.MECHANICAL_ROLLER.asItem()
    }

    override fun getCloneItemStack(
        state: BlockState,
        target: HitResult,
        level: LevelReader,
        pos: BlockPos,
        player: Player,
    ): ItemStack {
        val blockEntity = level.getBlockEntity(pos)
        val stack = ItemStack(AllBlocks.MECHANICAL_ROLLER)
        if (blockEntity is EnchantableBlockEntity) {
            val enchantments = blockEntity.getEnchantments().entrySet()
            enchantments.forEach {
                stack.enchant(it.key, it.intValue)
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
            blockEntity.setEnchantment(stack.get(DataComponents.ENCHANTMENTS) ?: ItemEnchantments.EMPTY)
            val components = DataComponentMap.builder()
                .addAll(blockEntity.components())
                .set(DataComponents.ENCHANTMENTS, stack.get(DataComponents.ENCHANTMENTS) ?: ItemEnchantments.EMPTY)
                .build()
            blockEntity.setComponents(components)
        }
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
        val heldItem = player.getItemInHand(hand)

        val placementHelper = PlacementHelpers.get(placementHelperId)
        if (!player.isShiftKeyDown && player.mayBuild()) {
            if (placementHelper.matchesItem(heldItem)) {
                if (heldItem.isEnchanted) {
                    placementHelper.getOffset(player, level, state, pos, hitResult)
                        .placeAlternativeBlockInWorld(level, heldItem.item as BlockItem, player, hand, hitResult)
                    return ItemInteractionResult.SUCCESS
                } else {
                    placementHelper.getOffset(player, level, state, pos, hitResult)
                        .placeInWorld(level, heldItem.item as BlockItem, player, hand, hitResult)
                    return ItemInteractionResult.SUCCESS
                }
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
    }

    private class PlacementHelper : PoleHelper<Direction>(
        AllBlocks.MECHANICAL_ROLLER::has,
        { it.getValue(FACING).clockWise.axis },
        FACING
    ) {
        override fun getItemPredicate(): Predicate<ItemStack> {
            return Predicate { stack -> AllBlocks.MECHANICAL_ROLLER.isIn(stack) }
        }

        override fun getStatePredicate(): Predicate<BlockState> {
            return Predicate { state -> BlockRegistration.ENCHANTABLE_MECHANICAL_ROLLER.has(state) }
        }
    }

    override fun getRequiredItems(state: BlockState, blockEntity: BlockEntity?): ItemRequirement {
        val stack = ItemStack(AllBlocks.MECHANICAL_DRILL).apply {
            if (blockEntity is EnchantableBlockEntity) {
                set(DataComponents.ENCHANTMENTS, blockEntity.getEnchantments())
            }
        }
        return ItemRequirement(ItemRequirement.ItemUseType.CONSUME, stack)
    }
}
