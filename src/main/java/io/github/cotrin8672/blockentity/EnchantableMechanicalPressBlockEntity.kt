package io.github.cotrin8672.blockentity

import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
import com.simibubi.create.foundation.utility.Lang
import io.github.cotrin8672.behaviour.EnchantablePressBehaviour
import joptsimple.internal.Strings
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class EnchantableMechanicalPressBlockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState,
    private val delegate: EnchantableBlockEntityDelegate = EnchantableBlockEntityDelegate(),
) : MechanicalPressBlockEntity(type, pos, state), EnchantableBlockEntity by delegate {
    override fun addBehaviours(behaviours: MutableList<BlockEntityBehaviour>) {
        super.addBehaviours(behaviours)
        pressingBehaviour = EnchantablePressBehaviour(this)
        behaviours.add(pressingBehaviour)
    }

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
}
