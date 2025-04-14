package io.github.cotrin8672.cem.content.block.saw

import com.simibubi.create.AllBlocks
import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement
import com.simibubi.create.content.kinetics.saw.SawBlock
import com.simibubi.create.content.kinetics.saw.SawBlockEntity
import com.simibubi.create.content.schematics.requirement.ItemRequirement
import io.github.cotrin8672.cem.content.block.EnchantableBlock
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity
import io.github.cotrin8672.cem.registry.BlockEntityRegistration
import io.github.cotrin8672.cem.registry.BlockRegistration
import io.github.cotrin8672.cem.util.placeAlternativeBlockInWorld
import net.createmod.catnip.placement.IPlacementHelper
import net.createmod.catnip.placement.PlacementHelpers
import net.createmod.catnip.placement.PlacementOffset
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
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import java.util.function.Predicate

class EnchantableSawBlock(properties: Properties) : SawBlock(properties), SpecialBlockItemRequirement,
    EnchantableBlock {
    companion object {
        private val placementHelperId = PlacementHelpers.register(PlacementHelper())
    }

    override fun getName(): MutableComponent {
        return AllBlocks.MECHANICAL_SAW.get().name
    }

    override fun getBlockEntityType(): BlockEntityType<out SawBlockEntity> {
        return BlockEntityRegistration.ENCHANTABLE_MECHANICAL_SAW.get()
    }

    @Deprecated("Deprecated in Java")
    override fun getDrops(blockState: BlockState, builder: LootParams.Builder): MutableList<ItemStack> {
        val blockEntity = builder.getParameter(LootContextParams.BLOCK_ENTITY)
        val stack = ItemStack(AllBlocks.MECHANICAL_SAW)
        if (blockEntity is EnchantableBlockEntity) {
            blockEntity.getEnchantments().forEach {
                stack.enchant(it.enchantment, it.level)
            }
        }
        return mutableListOf(stack)
    }

    override fun asItem(): Item {
        return AllBlocks.MECHANICAL_SAW.asItem()
    }

    override fun getCloneItemStack(
        state: BlockState,
        target: HitResult,
        level: BlockGetter,
        pos: BlockPos,
        player: Player,
    ): ItemStack {
        val blockEntity = level.getBlockEntity(pos)
        val stack = ItemStack(AllBlocks.MECHANICAL_SAW)
        if (blockEntity is EnchantableBlockEntity) {
            val enchantments = blockEntity.getEnchantments()
            enchantments.forEach {
                stack.enchant(it.enchantment, it.level)
            }
        }
        return stack
    }

    @Deprecated("Deprecated in Java")
    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult,
    ): InteractionResult {
        if (!player.getItemInHand(hand).isEnchanted)
            return super.use(state, level, pos, player, hand, hit)

        val stack = player.getItemInHand(hand)
        val placementHelper = PlacementHelpers.get(placementHelperId)
        if (!player.isShiftKeyDown && player.mayBuild()) {
            if (placementHelper.matchesItem(stack) && placementHelper.getOffset(
                    player,
                    level,
                    state,
                    pos,
                    hit,
                )
                    .placeAlternativeBlockInWorld(level, stack.item as BlockItem, player, hand, hit)
                    .consumesAction()
            ) return InteractionResult.SUCCESS
        }

        if (player.isSpectator || !stack.isEmpty)
            return InteractionResult.PASS
        if (state.getOptionalValue(FACING).orElse(Direction.WEST) != Direction.UP)
            return InteractionResult.PASS


        withBlockEntityDo(level, pos) { be: SawBlockEntity ->
            for (i in 0..<be.inventory.slots) {
                val heldItemStack = be.inventory.getStackInSlot(i)
                if (!level.isClientSide && !heldItemStack.isEmpty)
                    player.inventory.placeItemBackInInventory(heldItemStack)
            }
            be.inventory.clear()
            be.notifyUpdate()
        }
        return InteractionResult.SUCCESS
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

    override fun getRequiredItems(state: BlockState, blockEntity: BlockEntity?): ItemRequirement {
        val stack = ItemStack(AllBlocks.MECHANICAL_SAW)
        if (blockEntity is EnchantableBlockEntity) {
            val enchantments = blockEntity.getEnchantments()
            enchantments.forEach {
                stack.enchant(it.enchantment, it.level)
            }
        }
        val strictRequirement = ItemRequirement.StrictNbtStackRequirement(stack, ItemRequirement.ItemUseType.CONSUME)
        return ItemRequirement(strictRequirement)
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
