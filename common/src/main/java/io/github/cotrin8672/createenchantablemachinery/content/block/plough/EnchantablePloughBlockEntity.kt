package io.github.cotrin8672.createenchantablemachinery.content.block.plough

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation
import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity
import com.simibubi.create.foundation.utility.Lang
import io.github.cotrin8672.createenchantablemachinery.content.block.EnchantableBlockEntity
import io.github.cotrin8672.createenchantablemachinery.content.block.EnchantableBlockEntityDelegate
import joptsimple.internal.Strings
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class EnchantablePloughBlockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState,
) : SyncedBlockEntity(type, pos, state),
    IHaveGoggleInformation,
    EnchantableBlockEntity by EnchantableBlockEntityDelegate() {
    override fun addToGoggleTooltip(tooltip: MutableList<Component>, isPlayerSneaking: Boolean): Boolean {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking)
        for (instance in getEnchantments()) {
            val level = instance.level
            Lang.text(Strings.repeat(' ', 0))
                .add(instance.enchantment.getFullname(level).copy())
                .forGoggles(tooltip)
        }
        return true
    }

    override fun load(tag: CompoundTag) {
        readEnchantments(tag)
        super.load(tag)
    }

    override fun saveAdditional(tag: CompoundTag) {
        tag.remove(ItemStack.TAG_ENCH)
        writeEnchantments(tag)
        super.saveAdditional(tag)
    }
}
