package io.github.cotrin8672.block

import com.simibubi.create.AllBlocks
import com.simibubi.create.content.kinetics.millstone.MillstoneBlock
import com.simibubi.create.content.kinetics.millstone.MillstoneBlockEntity
import io.github.cotrin8672.blockentity.EnchantableBlockEntity
import io.github.cotrin8672.registry.BlockEntityRegistration
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.HitResult

class EnchantableMillstoneBlock(properties: Properties) : MillstoneBlock(properties), EnchantableBlock {
    override fun getName(): MutableComponent {
        return AllBlocks.MILLSTONE.get().name
    }

    override fun getBlockEntityType(): BlockEntityType<out MillstoneBlockEntity?> {
        return BlockEntityRegistration.ENCHANTABLE_MILLSTONE.get()
    }

    @Deprecated("Deprecated in Java")
    override fun getDrops(blockState: BlockState, builder: LootContext.Builder): MutableList<ItemStack> {
        val blockEntity = builder.getParameter(LootContextParams.BLOCK_ENTITY)
        val stack = ItemStack(AllBlocks.MILLSTONE.get())
        if (blockEntity is EnchantableBlockEntity) {
            blockEntity.getEnchantments().forEach {
                stack.enchant(it.enchantment, it.level)
            }
        }
        return mutableListOf(stack)
    }

    override fun asItem(): Item {
        return AllBlocks.MILLSTONE.get().asItem()
    }

    override fun getCloneItemStack(
        state: BlockState, target: HitResult, level: BlockGetter, pos: BlockPos, player: Player,
    ): ItemStack {
        val blockEntity = level.getBlockEntity(pos)
        val stack = ItemStack(AllBlocks.MILLSTONE.get())
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

    override fun canApply(enchantment: Enchantment): Boolean {
        return enchantment == Enchantments.BLOCK_EFFICIENCY
    }
}
