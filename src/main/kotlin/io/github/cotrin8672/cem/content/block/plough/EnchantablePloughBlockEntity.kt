package io.github.cotrin8672.cem.content.block.plough

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation
import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity
import com.simibubi.create.foundation.utility.CreateLang
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntityDelegate
import joptsimple.internal.Strings
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.enchantment.Enchantment.getFullname
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

        if (getEnchantments().entrySet().isEmpty()) return false
        for (instance in getEnchantments().entrySet()) {
            CreateLang.text(Strings.repeat(' ', 0))
                .add(getFullname(instance.key, instance.intValue))
                .forGoggles(tooltip)
        }
        return true
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        readEnchantments(tag, registries)
        super.loadAdditional(tag, registries)
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        writeEnchantments(tag, registries)
        super.saveAdditional(tag, registries)
    }

//    override fun getEnchantments(): ItemEnchantments {
//        return components().get(DataComponents.ENCHANTMENTS) ?: ItemEnchantments.EMPTY
//    }
//
//    override fun setEnchantment(enchantments: ItemEnchantments) {
//        val components = DataComponentMap.builder()
//            .addAll(components())
//            .set(DataComponents.ENCHANTMENTS, enchantments)
//            .build()
//
//        setComponents(components)
//    }
}
