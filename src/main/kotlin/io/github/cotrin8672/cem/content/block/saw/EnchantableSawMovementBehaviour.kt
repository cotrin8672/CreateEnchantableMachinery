package io.github.cotrin8672.cem.content.block.saw

import com.simibubi.create.AllBlocks
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.render.ActorVisual
import com.simibubi.create.content.contraptions.render.ContraptionMatrices
import com.simibubi.create.content.kinetics.saw.SawMovementBehaviour
import com.simibubi.create.content.kinetics.saw.TreeCutter
import com.simibubi.create.foundation.utility.BlockHelper
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld
import dev.engine_room.flywheel.api.visualization.VisualizationContext
import io.github.cotrin8672.cem.util.EnchantedItemFactory
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.nbt.Tag
import net.minecraft.tags.BlockTags
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.state.BlockState
import java.util.*

class EnchantableSawMovementBehaviour : SawMovementBehaviour() {
    private var enchantedTools: MutableMap<MovementContext, ItemStack> = WeakHashMap()

    override fun destroyBlock(context: MovementContext?, breakingPos: BlockPos) {
        context ?: return

        if (enchantedTools[context] == null) {
            enchantedTools[context] = EnchantedItemFactory.getPickaxeItemStack(context)
        }

        BlockHelper.destroyBlockAs(context.world, breakingPos, null, enchantedTools[context], 1f) {
            this.dropItem(context, it)
        }
    }

    override fun onBlockBroken(context: MovementContext?, pos: BlockPos?, brokenState: BlockState) {
        if (brokenState.`is`(BlockTags.LEAVES)) return
        context ?: return
        if (enchantedTools[context] == null) {
            enchantedTools[context] = EnchantedItemFactory.getPickaxeItemStack(context)
        }

        val dynamicTree = TreeCutter.findDynamicTree(brokenState.block, pos)
        if (dynamicTree.isPresent) {
            dynamicTree.get().destroyBlocks(context.world, enchantedTools[context], null) { stack, dropPos ->
                dropItemFromCutTree(context, stack, dropPos)
            }
            return
        }

        TreeCutter.findTree(context.world, pos, brokenState)
            .destroyBlocks(context.world, enchantedTools[context], null) { stack, dropPos ->
                dropItemFromCutTree(context, stack, dropPos)
            }
    }

    override fun getBlockBreakingSpeed(context: MovementContext): Float {
        val enchantmentTag = context.blockEntityData.getList("Enchantments", Tag.TAG_COMPOUND.toInt())
        val efficiencyLevel = EnchantmentHelper.deserializeEnchantments(enchantmentTag)[Enchantments.BLOCK_EFFICIENCY]

        return super.getBlockBreakingSpeed(context) * ((efficiencyLevel ?: 0) + 1)
    }

    override fun createVisual(
        visualizationContext: VisualizationContext,
        simulationWorld: VirtualRenderWorld,
        movementContext: MovementContext,
    ): ActorVisual {
        return EnchantableSawActorVisual(visualizationContext, simulationWorld, movementContext)
    }

    override fun canBeDisabledVia(context: MovementContext?): ItemStack? {
        return AllBlocks.MECHANICAL_SAW.asStack()
    }

    override fun renderInContraption(
        context: MovementContext,
        renderWorld: VirtualRenderWorld,
        matrices: ContraptionMatrices,
        buffer: MultiBufferSource,
    ) {
        EnchantableSawRenderer.renderInContraption(context, renderWorld, matrices, buffer)
    }
}
