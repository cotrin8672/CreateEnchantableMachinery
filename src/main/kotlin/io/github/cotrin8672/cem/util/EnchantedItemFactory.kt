package io.github.cotrin8672.cem.util

import com.simibubi.create.content.contraptions.behaviour.MovementContext
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

object EnchantedItemFactory {
    private val toolCache: MutableMap<Pair<ListTag?, Item>, ItemStack> = mutableMapOf()

    fun getToolForBlock(level: Level, pos: BlockPos, enchantmentTag: ListTag?): ItemStack {
        val blockState = level.getBlockState(pos)
        val tool = selectToolForBlock(blockState)
        return getToolItemStack(enchantmentTag, tool)
    }

    fun getToolForBlock(context: MovementContext, pos: BlockPos): ItemStack {
        val blockState = context.world.getBlockState(pos)
        val tool = selectToolForBlock(blockState)
        val blockEntityData = context.blockEntityData ?: return ItemStack.EMPTY
        val enchantmentTag = blockEntityData.getList(ItemStack.TAG_ENCH, Tag.TAG_COMPOUND.toInt())
        return getToolItemStack(enchantmentTag, tool)
    }

    private fun selectToolForBlock(blockState: BlockState): Item {
        return when {
            blockState.`is`(net.minecraft.tags.BlockTags.MINEABLE_WITH_AXE) -> Items.NETHERITE_AXE
            blockState.`is`(net.minecraft.tags.BlockTags.MINEABLE_WITH_HOE) -> Items.NETHERITE_HOE
            blockState.`is`(net.minecraft.tags.BlockTags.MINEABLE_WITH_SHOVEL) -> Items.NETHERITE_SHOVEL
            blockState.`is`(net.minecraft.tags.BlockTags.MINEABLE_WITH_PICKAXE) -> Items.NETHERITE_PICKAXE
            blockState.requiresCorrectToolForDrops() -> Items.NETHERITE_PICKAXE
            else -> Items.NETHERITE_PICKAXE
        }
    }

    private fun getToolItemStack(enchantmentTag: ListTag?, tool: Item): ItemStack {
        enchantmentTag ?: return ItemStack.EMPTY
        val cacheKey = enchantmentTag to tool

        return toolCache.getOrPut(cacheKey) {
            val stack = ItemStack(tool)
            val nbt = CompoundTag().apply {
                putByte("Unbreakable", 1)
                put("Enchantments", enchantmentTag)
            }
            stack.tag = nbt
            stack
        }
    }

    fun getPickaxeItemStack(enchantmentTag: ListTag?): ItemStack {
        return getToolItemStack(enchantmentTag, Items.NETHERITE_PICKAXE)
    }

    fun getPickaxeItemStack(movementContext: MovementContext?): ItemStack {
        movementContext ?: return ItemStack.EMPTY
        val blockEntityData = movementContext.blockEntityData ?: return ItemStack.EMPTY
        val enchantmentTag = blockEntityData.getList(ItemStack.TAG_ENCH, Tag.TAG_COMPOUND.toInt()) ?: return ItemStack.EMPTY
        return getToolItemStack(enchantmentTag, Items.NETHERITE_PICKAXE)
    }
}