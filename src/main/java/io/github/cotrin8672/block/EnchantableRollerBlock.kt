package io.github.cotrin8672.block

import com.simibubi.create.AllBlocks
import com.simibubi.create.content.contraptions.actors.roller.RollerBlock
import com.simibubi.create.content.contraptions.actors.roller.RollerBlockEntity
import com.simibubi.create.foundation.placement.PlacementHelpers
import com.simibubi.create.foundation.placement.PoleHelper
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
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import java.util.function.Predicate

class EnchantableRollerBlock(properties: Properties) : RollerBlock(properties), EnchantableBlock {
    companion object {
        private val placementHelperId = PlacementHelpers.register(PlacementHelper())
    }

    override fun getName(): MutableComponent {
        return AllBlocks.MECHANICAL_ROLLER.get().name
    }

    override fun getBlockEntityType(): BlockEntityType<out RollerBlockEntity> {
        return BlockEntityRegistration.ENCHANTABLE_MECHANICAL_ROLLER.get()
    }

    @Deprecated("Deprecated in Java")
    override fun getDrops(blockState: BlockState, builder: LootParams.Builder): MutableList<ItemStack> {
        val blockEntity = builder.getParameter(LootContextParams.BLOCK_ENTITY)
        val stack = ItemStack(AllBlocks.MECHANICAL_ROLLER)
        if (blockEntity is EnchantableBlockEntity) {
            blockEntity.getEnchantments().forEach {
                stack.enchant(it.enchantment, it.level)
            }
        }
        return mutableListOf(stack)
    }

    override fun asItem(): Item {
        return AllBlocks.MECHANICAL_ROLLER.asItem()
    }

    override fun getCloneItemStack(
        state: BlockState, target: HitResult, level: BlockGetter, pos: BlockPos, player: Player,
    ): ItemStack {
        val blockEntity = level.getBlockEntity(pos)
        val stack = ItemStack(AllBlocks.MECHANICAL_ROLLER)
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
        world: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        ray: BlockHitResult,
    ): InteractionResult {
        val heldItem = player.getItemInHand(hand)

        val placementHelper = PlacementHelpers.get(placementHelperId)
        if (!player.isShiftKeyDown && player.mayBuild()) {
            if (placementHelper.matchesItem(heldItem)) {
                placementHelper.getOffset(player, world, state, pos, ray)
                    .placeAlternativeBlockInWorld(world, heldItem.item as BlockItem, player, hand, ray)
                return InteractionResult.SUCCESS
            }
        }

        return InteractionResult.PASS
    }

    override fun canApply(enchantment: Enchantment): Boolean {
        return when {
            enchantment == Enchantments.UNBREAKING -> false
            enchantment == Enchantments.MENDING -> false
            enchantment.category == EnchantmentCategory.DIGGER -> true
            else -> false
        }
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
}