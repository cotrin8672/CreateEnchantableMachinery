package io.github.cotrin8672.cem.content.block.press

import com.simibubi.create.AllBlocks
import com.simibubi.create.content.kinetics.press.MechanicalPressBlock
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity
import io.github.cotrin8672.cem.registry.BlockEntityRegistration
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.ItemEnchantments
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.HitResult

class EnchantableMechanicalPressBlock(properties: Properties) : MechanicalPressBlock(properties) {
    override fun getName(): MutableComponent {
        return AllBlocks.MECHANICAL_PRESS.get().name
    }

    override fun getBlockEntityType(): BlockEntityType<out MechanicalPressBlockEntity> {
        return BlockEntityRegistration.ENCHANTABLE_MECHANICAL_PRESS.get()
    }

    override fun asItem(): Item {
        return AllBlocks.MECHANICAL_PRESS.asItem()
    }

    override fun getCloneItemStack(
        state: BlockState,
        target: HitResult,
        level: LevelReader,
        pos: BlockPos,
        player: Player,
    ): ItemStack {
        val blockEntity = level.getBlockEntity(pos)
        val stack = ItemStack(AllBlocks.MECHANICAL_PRESS)
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
            blockEntity.setEnchantment(stack.get(DataComponents.ENCHANTMENTS) ?: ItemEnchantments.EMPTY)
        }
    }
}
