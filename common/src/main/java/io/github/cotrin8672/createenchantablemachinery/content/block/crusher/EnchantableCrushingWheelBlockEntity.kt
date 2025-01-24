package io.github.cotrin8672.createenchantablemachinery.content.block.crusher

import com.simibubi.create.content.kinetics.crusher.CrushingWheelBlockEntity
import com.simibubi.create.foundation.utility.Iterate
import com.simibubi.create.foundation.utility.Lang
import io.github.cotrin8672.createenchantablemachinery.content.block.EnchantableBlockEntity
import io.github.cotrin8672.createenchantablemachinery.content.block.EnchantableBlockEntityDelegate
import joptsimple.internal.Strings
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class EnchantableCrushingWheelBlockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState,
) : CrushingWheelBlockEntity(type, pos, state), EnchantableBlockEntity by EnchantableBlockEntityDelegate() {
    override fun fixControllers() {
        for (direction in Iterate.directions) {
            (blockState.block as EnchantableCrushingWheelBlock).updateControllers(
                blockState,
                level,
                blockPos,
                direction
            )
        }
    }

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

    override fun read(compound: CompoundTag, clientPacket: Boolean) {
        readEnchantments(compound)
        super.read(compound, clientPacket)
    }

    override fun write(compound: CompoundTag, clientPacket: Boolean) {
        compound.remove(ItemStack.TAG_ENCH)
        writeEnchantments(compound)
        super.write(compound, clientPacket)
    }
}
