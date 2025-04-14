package io.github.cotrin8672.cem.content.block.millstone

import com.simibubi.create.content.kinetics.millstone.MillstoneBlockEntity
import com.simibubi.create.foundation.utility.CreateLang
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntityDelegate
import joptsimple.internal.Strings
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.LazyOptional

class EnchantableMillstoneBlockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState,
) : MillstoneBlockEntity(type, pos, state), EnchantableBlockEntity by EnchantableBlockEntityDelegate() {
    companion object {
//        fun registerCapabilities(event: RegisterCapabilitiesEvent) {
//            event.registerBlockEntity(
//                Capabilities.ItemHandler.BLOCK,
//                BlockEntityRegistration.ENCHANTABLE_MILLSTONE.get()
//            ) { be: MillstoneBlockEntity, _: Direction? -> be.capability }
//        }
    }

    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        if (isItemHandlerCap(cap)) return capability.cast()
        return super.getCapability(cap, side)
    }

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

    override fun getProcessingSpeed(): Int {
        return (super.getProcessingSpeed() * (1 + 0.2 * getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY))).toInt()
    }
}
