package io.github.cotrin8672.createenchantablemachinery.content.block.fan

import com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity
import com.simibubi.create.foundation.utility.Lang
import io.github.cotrin8672.createenchantablemachinery.content.block.EnchantableBlockEntity
import io.github.cotrin8672.createenchantablemachinery.content.block.EnchantableBlockEntityDelegate
import joptsimple.internal.Strings
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class EnchantableEncasedFanBlockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState,
    private val delegate: EnchantableBlockEntityDelegate = EnchantableBlockEntityDelegate(),
) : EncasedFanBlockEntity(type, pos, state), EnchantableBlockEntity by delegate {
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

    override fun read(tag: CompoundTag, clientPacket: Boolean) {
        super.read(tag, clientPacket)
        readEnchantments(tag)
        if (airCurrent !is EnchantableAirCurrent)
            airCurrent = EnchantableAirCurrent(this, getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY))
    }

    override fun write(tag: CompoundTag, clientPacket: Boolean) {
        super.write(tag, clientPacket)
        tag.remove(ItemStack.TAG_ENCH)
        writeEnchantments(tag)
    }

    override fun setEnchantment(listTag: ListTag) {
        delegate.setEnchantment(listTag)
        airCurrent = EnchantableAirCurrent(this, this.getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY))
    }
}
