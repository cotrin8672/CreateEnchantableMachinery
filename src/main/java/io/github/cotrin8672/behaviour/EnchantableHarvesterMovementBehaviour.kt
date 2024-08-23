package io.github.cotrin8672.behaviour

import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.foundation.utility.BlockHelper
import com.simibubi.create.infrastructure.config.AllConfigs
import io.github.cotrin8672.util.EnchantedItemFactory
import net.minecraft.core.BlockPos
import net.minecraft.tags.BlockTags
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.EnchantmentInstance
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.CocoaBlock
import net.minecraft.world.level.block.CropBlock
import net.minecraft.world.level.block.GrowingPlantBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.IntegerProperty
import org.apache.commons.lang3.mutable.MutableBoolean

class EnchantableHarvesterMovementBehaviour : HarvesterMovementBehaviour() {
    private fun sameItem(stack: ItemStack, otherStack: ItemStack): Boolean {
        return !otherStack.isEmpty && stack.`is`(otherStack.item)
    }

    override fun visitNewPosition(context: MovementContext, pos: BlockPos) {
        val world = context.world
        val stateVisited = world.getBlockState(pos)
        var notCropButCuttable = false

        if (world.isClientSide) return

        if (!isValidCrop(world, pos, stateVisited)) {
            if (isValidOther(world, pos, stateVisited)) notCropButCuttable = true
            else return
        }

        val enchantments = EnchantmentHelper.getEnchantments(ItemStack.EMPTY.apply {
            tag = context.blockEntityData
        }).map { EnchantmentInstance(it.key, it.value) }
        var item = EnchantedItemFactory.getPickaxeItemStack(*enchantments.toTypedArray())
        var effectChance = 1f

        if (stateVisited.`is`(BlockTags.LEAVES)) {
            item = ItemStack(Items.SHEARS)
            effectChance = .45f
        }

        val seedSubtracted = MutableBoolean(notCropButCuttable)
        BlockHelper.destroyBlockAs(
            world, pos, null, item, effectChance
        ) { stack: ItemStack ->
            if (AllConfigs.server().kinetics.harvesterReplants.get() && !seedSubtracted.value
                && sameItem(stack, ItemStack(stateVisited.block))
            ) {
                stack.shrink(1)
                seedSubtracted.setTrue()
            }
            dropItem(context, stack)
        }

        val cutCrop = cutCrop(world, pos, stateVisited)
        world.setBlockAndUpdate(pos, if (cutCrop.canSurvive(world, pos)) cutCrop else Blocks.AIR.defaultBlockState())
    }

    private fun cutCrop(world: Level, pos: BlockPos, state: BlockState): BlockState {
        if (!AllConfigs.server().kinetics.harvesterReplants.get()) {
            if (state.fluidState
                    .isEmpty
            ) return Blocks.AIR.defaultBlockState()
            return state.fluidState
                .createLegacyBlock()
        }

        val block = state.block
        if (block is CropBlock) {
            return block.getStateForAge(0)
        }
        if (block === Blocks.SWEET_BERRY_BUSH) {
            return state.setValue(BlockStateProperties.AGE_3, 1)
        }
        if (block === Blocks.SUGAR_CANE || block is GrowingPlantBlock) {
            if (state.fluidState
                    .isEmpty
            ) return Blocks.AIR.defaultBlockState()
            return state.fluidState
                .createLegacyBlock()
        }
        if (state.getCollisionShape(world, pos)
                .isEmpty || block is CocoaBlock
        ) {
            for (property in state.properties) {
                if (property !is IntegerProperty) continue
                if (property.getName() != BlockStateProperties.AGE_1.name) continue
                return state.setValue(property, 0)
            }
        }

        if (state.fluidState
                .isEmpty
        ) return Blocks.AIR.defaultBlockState()
        return state.fluidState
            .createLegacyBlock()
    }
}
