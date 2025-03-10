package io.github.cotrin8672.cem.content.block.crusher

import com.simibubi.create.content.kinetics.crusher.CrushingWheelBlockEntity
import com.simibubi.create.foundation.utility.CreateLang
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntityDelegate
import joptsimple.internal.Strings
import net.createmod.catnip.data.Iterate
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.enchantment.Enchantment.getFullname
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

        if (getEnchantments().entrySet().isEmpty()) return false
        for (instance in getEnchantments().entrySet()) {
            CreateLang.text(Strings.repeat(' ', 0))
                .add(getFullname(instance.key, instance.intValue))
                .forGoggles(tooltip)
        }
        return true
    }

    override fun read(compound: CompoundTag, registries: HolderLookup.Provider, clientPacket: Boolean) {
        readEnchantments(compound, registries)
        super.read(compound, registries, clientPacket)
    }

    override fun write(compound: CompoundTag, registries: HolderLookup.Provider, clientPacket: Boolean) {
        writeEnchantments(compound, registries)
        super.write(compound, registries, clientPacket)
    }
}
