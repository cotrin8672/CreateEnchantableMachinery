package io.github.cotrin8672.cem.content.block.plough

import com.simibubi.create.AllBlocks
import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement
import com.simibubi.create.content.contraptions.actors.plough.PloughBlock
import com.simibubi.create.content.schematics.requirement.ItemRequirement
import com.simibubi.create.foundation.block.IBE
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity
import io.github.cotrin8672.cem.registry.BlockEntityRegistration
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.ItemEnchantments
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.HitResult

class EnchantablePloughBlock(properties: Properties) : PloughBlock(properties), IBE<EnchantablePloughBlockEntity>,
    SpecialBlockItemRequirement {
    override fun getName(): MutableComponent {
        return AllBlocks.MECHANICAL_PLOUGH.get().name
    }

    override fun getBlockEntityClass(): Class<EnchantablePloughBlockEntity> {
        return EnchantablePloughBlockEntity::class.java
    }

    override fun getBlockEntityType(): BlockEntityType<out EnchantablePloughBlockEntity> {
        return BlockEntityRegistration.ENCHANTABLE_MECHANICAL_PLOUGH.get()
    }

    override fun getCloneItemStack(
        state: BlockState,
        target: HitResult,
        level: LevelReader,
        pos: BlockPos,
        player: Player,
    ): ItemStack {
        val blockEntity = level.getBlockEntity(pos)
        val stack = ItemStack(AllBlocks.MECHANICAL_PLOUGH)
        if (blockEntity is EnchantableBlockEntity) {
            val enchantments = blockEntity.getEnchantments().entrySet()
            enchantments.forEach {
                stack.enchant(it.key, it.intValue)
            }
        }
        return stack
    }

    override fun setPlacedBy(
        worldIn: Level,
        pos: BlockPos,
        state: BlockState,
        placer: LivingEntity?,
        stack: ItemStack,
    ) {
        super.setPlacedBy(worldIn, pos, state, placer, stack)
        val blockEntity = worldIn.getBlockEntity(pos)
        if (blockEntity is EnchantableBlockEntity) {
            val enchantments = stack.get(DataComponents.ENCHANTMENTS) ?: ItemEnchantments.EMPTY
            blockEntity.setEnchantment(enchantments)
            val components = DataComponentMap.builder()
                .addAll(blockEntity.components())
                .set(DataComponents.ENCHANTMENTS, stack.get(DataComponents.ENCHANTMENTS) ?: ItemEnchantments.EMPTY)
                .build()
            blockEntity.setComponents(components)
        }
    }

    override fun getRequiredItems(state: BlockState, blockEntity: BlockEntity?): ItemRequirement {
        val stack = ItemStack(AllBlocks.MECHANICAL_PLOUGH)
        if (blockEntity is EnchantableBlockEntity) {
            val enchantments = blockEntity.getEnchantments()
            stack.set(DataComponents.ENCHANTMENTS, enchantments)
        }
        val strictRequirement = ItemRequirement.StrictNbtStackRequirement(stack, ItemRequirement.ItemUseType.CONSUME)
        return ItemRequirement(strictRequirement)
    }
}
