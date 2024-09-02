package io.github.cotrin8672.blockentity

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation
import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity
import com.simibubi.create.foundation.utility.Lang
import joptsimple.internal.Strings
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class EnchantablePloughBlockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState,
    private val delegate: EnchantableBlockEntityDelegate = EnchantableBlockEntityDelegate(),
) : SyncedBlockEntity(type, pos, state),
    IHaveGoggleInformation,
    EnchantableBlockEntity by delegate {
    override fun addToGoggleTooltip(tooltip: MutableList<Component>, isPlayerSneaking: Boolean): Boolean {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking)
        for (instance in delegate.enchantmentInstances) {
            val level = instance.level
            Lang.text(Strings.repeat(' ', 0))
                .add(instance.enchantment.getFullname(level).copy())
                .forGoggles(tooltip)
        }
        return true
    }

    override fun load(tag: CompoundTag) {
        delegate.enchantmentsTag = tag.getList(ItemStack.TAG_ENCH, Tag.TAG_COMPOUND.toInt())
        super.load(tag)
    }

    override fun saveAdditional(tag: CompoundTag) {
        tag.remove(ItemStack.TAG_ENCH)
        delegate.enchantmentsTag?.let { tag.put(ItemStack.TAG_ENCH, it) }
        super.saveAdditional(tag)
    }
}
