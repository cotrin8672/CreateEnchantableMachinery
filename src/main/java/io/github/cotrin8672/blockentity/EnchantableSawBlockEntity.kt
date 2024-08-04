package io.github.cotrin8672.blockentity

import com.simibubi.create.content.kinetics.saw.SawBlockEntity
import com.simibubi.create.foundation.utility.BlockHelper
import com.simibubi.create.foundation.utility.Lang
import com.simibubi.create.foundation.utility.VecHelper
import io.github.cotrin8672.entity.BlockBreaker
import io.github.cotrin8672.util.EnchantedItemFactory
import joptsimple.internal.Strings
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.EnchantmentInstance
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3

class EnchantableSawBlockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState,
) : SawBlockEntity(type, pos, state), EnchantableBlockEntity {
    private val enchantedItem: ItemStack
        get() = EnchantedItemFactory.getPickaxeItemStack(*getEnchantments().toTypedArray())

    private var enchantmentsTag: ListTag? = null
    private val enchantmentInstances: List<EnchantmentInstance>
        get() = enchantmentsTag?.let { tag ->
            EnchantmentHelper.deserializeEnchantments(tag).map {
                EnchantmentInstance(it.key, it.value)
            }
        } ?: listOf()
    private val fakePlayer by lazy {
        val nonNullLevel = checkNotNull(this.level)
        if (nonNullLevel is ServerLevel)
            BlockBreaker(nonNullLevel, this@EnchantableSawBlockEntity)
        else null
    }

    override fun getBreakSpeed(): Float {
        val efficiencyLevel = getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY)
        return super.getBreakSpeed() * (efficiencyLevel + 1)
    }

    override fun onBlockBroken(stateToBreak: BlockState) {
        val nonNullLevel = checkNotNull(level)
        val vec = VecHelper.offsetRandomly(VecHelper.getCenterOf(breakingPos), nonNullLevel.random, .125f)
        BlockHelper.destroyBlockAs(
            nonNullLevel,
            breakingPos,
            fakePlayer,
            enchantedItem,
            1f
        ) { stack: ItemStack ->
            if (stack.isEmpty) return@destroyBlockAs
            if (!nonNullLevel.gameRules.getBoolean(GameRules.RULE_DOBLOCKDROPS)) return@destroyBlockAs
            if (nonNullLevel.restoringBlockSnapshots) return@destroyBlockAs

            val entity = ItemEntity(nonNullLevel, vec.x, vec.y, vec.z, stack)
            entity.setDefaultPickUpDelay()
            entity.deltaMovement = Vec3.ZERO
            nonNullLevel.addFreshEntity(entity)
        }
    }

    override fun addToGoggleTooltip(tooltip: MutableList<Component>, isPlayerSneaking: Boolean): Boolean {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking)
        for (instance in enchantmentInstances) {
            val level = instance.level
            Lang.text(Strings.repeat(' ', 0))
                .add(instance.enchantment.getFullname(level).copy())
                .forGoggles(tooltip)
        }
        return true
    }

    override fun getEnchantments(): List<EnchantmentInstance> {
        return enchantmentInstances
    }

    override fun setEnchantment(listTag: ListTag) {
        enchantmentsTag = listTag
    }

    override fun read(compound: CompoundTag, clientPacket: Boolean) {
        enchantmentsTag = compound.getList(ItemStack.TAG_ENCH, Tag.TAG_COMPOUND.toInt())
        super.read(compound, clientPacket)
    }

    override fun write(compound: CompoundTag, clientPacket: Boolean) {
        compound.remove(ItemStack.TAG_ENCH)
        enchantmentsTag?.let { compound.put(ItemStack.TAG_ENCH, it) }
        super.write(compound, clientPacket)
    }
}
