package io.github.cotrin8672.blockentity

import com.simibubi.create.content.kinetics.millstone.MillstoneBlockEntity
import com.simibubi.create.foundation.utility.Lang
import joptsimple.internal.Strings
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import kotlin.math.pow

class EnchantableMillstoneBlockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState,
    private val delegate: EnchantableBlockEntityDelegate = EnchantableBlockEntityDelegate(),
) : MillstoneBlockEntity(type, pos, state), EnchantableBlockEntity by delegate {
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

    override fun read(compound: CompoundTag, clientPacket: Boolean) {
        delegate.enchantmentsTag = compound.getList(ItemStack.TAG_ENCH, Tag.TAG_COMPOUND.toInt())
        super.read(compound, clientPacket)
    }

    override fun write(compound: CompoundTag, clientPacket: Boolean) {
        compound.remove(ItemStack.TAG_ENCH)
        delegate.enchantmentsTag?.let { compound.put(ItemStack.TAG_ENCH, it) }
        super.write(compound, clientPacket)
    }

    override fun getProcessingSpeed(): Int {
        return (super.getProcessingSpeed() * 1.3.pow(delegate.getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY))).toInt()
    }
}
