package io.github.cotrin8672.content.block.press

import com.simibubi.create.AllBlocks
import com.simibubi.create.content.kinetics.press.MechanicalPressBlock
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity
import io.github.cotrin8672.content.block.EnchantableBlock
import io.github.cotrin8672.content.block.EnchantableBlockEntity
import io.github.cotrin8672.registrate.BlockEntityRegistration
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.entity.LivingEntity
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

class EnchantableMechanicalPressBlock(properties: Properties) : MechanicalPressBlock(properties), EnchantableBlock {
    override fun getName(): MutableComponent {
        return AllBlocks.MECHANICAL_PRESS.get().name
    }

    override fun getBlockEntityType(): BlockEntityType<out MechanicalPressBlockEntity> {
        return BlockEntityRegistration.ENCHANTABLE_MECHANICAL_PRESS.get()
    }

    @Deprecated("Deprecated in Java")
    override fun getDrops(blockState: BlockState, builder: LootContext.Builder): MutableList<ItemStack> {
        val blockEntity = builder.getParameter(LootContextParams.BLOCK_ENTITY)
        val stack = ItemStack(AllBlocks.MECHANICAL_PRESS.get())
        if (blockEntity is EnchantableBlockEntity) {
            blockEntity.getEnchantments().forEach {
                stack.enchant(it.enchantment, it.level)
            }
        }
        return mutableListOf(stack)
    }

    override fun asItem(): Item {
        return AllBlocks.MECHANICAL_PRESS.get().asItem()
    }

    override fun getCloneItemStack(level: BlockGetter, pos: BlockPos, state: BlockState): ItemStack {
        val blockEntity = level.getBlockEntity(pos)
        val stack = ItemStack(AllBlocks.MECHANICAL_PRESS.get())
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
