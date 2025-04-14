package io.github.cotrin8672.cem.content.block.fan

import com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity
import com.simibubi.create.foundation.utility.CreateLang
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntityDelegate
import joptsimple.internal.Strings
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
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
            CreateLang.text(Strings.repeat(' ', 0))
                .add(instance.enchantment.getFullname(level).copy())
                .forGoggles(tooltip)
        }
        return true
    }

    override fun read(compound: CompoundTag, clientPacket: Boolean) {
        readEnchantments(compound)
        super.read(compound, clientPacket)
    }

    override fun write(compound: CompoundTag, clientPacket: Boolean) {
        compound.remove(ItemStack.TAG_ENCH)
        writeEnchantments(compound)
        super.write(compound, clientPacket)
    }

    override fun tick() {
        if (airCurrent !is EnchantableAirCurrent) {
            val efficiencyLevel = getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY)
            airCurrent = EnchantableAirCurrent(this, efficiencyLevel)
        }
        super.tick()
    }
}
