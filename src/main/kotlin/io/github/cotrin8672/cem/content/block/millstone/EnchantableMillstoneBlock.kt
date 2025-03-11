package io.github.cotrin8672.cem.content.block.millstone

import com.simibubi.create.AllBlocks
import com.simibubi.create.content.kinetics.millstone.MillstoneBlock
import com.simibubi.create.content.kinetics.millstone.MillstoneBlockEntity
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

class EnchantableMillstoneBlock(properties: Properties) : MillstoneBlock(properties) {
    override fun getName(): MutableComponent {
        return AllBlocks.MILLSTONE.get().name
    }

    override fun getBlockEntityType(): BlockEntityType<out MillstoneBlockEntity?> {
        return BlockEntityRegistration.ENCHANTABLE_MILLSTONE.get()
    }

    override fun asItem(): Item {
        return AllBlocks.MILLSTONE.asItem()
    }

    override fun getCloneItemStack(
        state: BlockState,
        target: HitResult,
        level: LevelReader,
        pos: BlockPos,
        player: Player,
    ): ItemStack {
        val blockEntity = level.getBlockEntity(pos)
        val stack = ItemStack(AllBlocks.MILLSTONE)
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
