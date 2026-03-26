package io.github.cotrin8672.cem.util

import com.simibubi.create.content.equipment.wrench.IWrenchable
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.level.BlockEvent

fun handleSneakWrenchWithSourceItem(state: BlockState, context: UseOnContext): InteractionResult {
    val level = context.level
    val pos = context.clickedPos
    val player = context.player ?: return InteractionResult.SUCCESS
    val serverLevel = level as? ServerLevel ?: return InteractionResult.SUCCESS

    val breakEvent = BlockEvent.BreakEvent(level, pos, level.getBlockState(pos), player)
    NeoForge.EVENT_BUS.post(breakEvent)
    if (breakEvent.isCanceled) return InteractionResult.SUCCESS

    if (!player.isCreative) {
        val stack = createWrenchDropStack(level, pos, state)
        player.inventory.placeItemBackInInventory(stack)
    }

    state.spawnAfterBreak(serverLevel, pos, ItemStack.EMPTY, true)
    level.destroyBlock(pos, false)
    IWrenchable.playRemoveSound(level, pos)
    return InteractionResult.SUCCESS
}

private fun createWrenchDropStack(level: Level, pos: BlockPos, state: BlockState): ItemStack {
    val blockEntity = level.getBlockEntity(pos) as? EnchantableBlockEntity
    val sourceItem = blockEntity?.getSourceItem() ?: state.block.asItem()
    val stack = ItemStack(sourceItem)

    val enchantments = blockEntity?.getEnchantments()
    if (enchantments != null && !enchantments.isEmpty) {
        stack.set(DataComponents.ENCHANTMENTS, enchantments)
    }
    return stack
}
