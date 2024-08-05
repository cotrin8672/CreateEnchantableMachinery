package io.github.cotrin8672.block

import com.simibubi.create.AllBlocks
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterBlock
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterBlockEntity
import io.github.cotrin8672.blockentity.EnchantableBlockEntity
import io.github.cotrin8672.registry.BlockEntityRegistration
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.HitResult

class EnchantableHarvesterBlock(properties: Properties) : HarvesterBlock(properties), EnchantableBlock {
    override fun getName(): MutableComponent {
        return AllBlocks.MECHANICAL_HARVESTER.get().name
    }

    override fun getBlockEntityType(): BlockEntityType<out HarvesterBlockEntity> {
        return BlockEntityRegistration.ENCHANTABLE_MECHANICAL_HARVESTER.get()
    }

    override fun asItem(): Item {
        return AllBlocks.MECHANICAL_HARVESTER.asItem()
    }

    override fun getCloneItemStack(
        state: BlockState, target: HitResult, level: BlockGetter, pos: BlockPos, player: Player,
    ): ItemStack {
        val blockEntity = level.getBlockEntity(pos)
        val stack = ItemStack(AllBlocks.MECHANICAL_HARVESTER)
        if (blockEntity is EnchantableBlockEntity) {
            blockEntity.getEnchantments().forEach {
                stack.enchant(it.enchantment, it.level)
            }
        }
        return stack
    }

    override fun canApply(enchantment: Enchantment): Boolean {
        return enchantment == Enchantments.BLOCK_FORTUNE
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
            blockEntity.setEnchantment(stack.enchantmentTags)
        }
    }
}
