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
import io.github.cotrin8672.cem.util.destroyBlocks
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.tags.BlockTags
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.state.BlockState
import java.util.*
import kotlin.jvm.optionals.getOrNull

class EnchantableSawMovementBehaviour : SawMovementBehaviour() {
    private val enchantedTools: MutableMap<MovementContext, ItemStack> = WeakHashMap()

    override fun destroyBlock(context: MovementContext?, breakingPos: BlockPos) {
        context ?: return

        if (enchantedTools[context] == null)
            enchantedTools[context] = EnchantedItemFactory.getPickaxeItemStack(context.blockEntityData, context)

        BlockHelper.destroyBlockAs(context.world, breakingPos, null, enchantedTools[context], 1f) {
            this.dropItem(context, it)
        }
    }

    override fun onBlockBroken(context: MovementContext?, pos: BlockPos?, brokenState: BlockState) {
        context ?: return
        if (brokenState.`is`(BlockTags.LEAVES)) return

        if (enchantedTools[context] == null)
            enchantedTools[context] = EnchantedItemFactory.getPickaxeItemStack(context.blockEntityData, context)

        val dynamicTree = TreeCutter.findDynamicTree(brokenState.block, pos)
        if (dynamicTree.isPresent) {
            dynamicTree.get().destroyBlocks(context.world, enchantedTools[context]!!) { stack, dropPos ->
                dropItemFromCutTree(context, stack, dropPos)
            }
            return
        }

        TreeCutter.findTree(context.world, pos, brokenState)
            .destroyBlocks(context.world, enchantedTools[context]!!) { stack, dropPos ->
                dropItemFromCutTree(context, stack, dropPos)
            }
    }

    override fun getBlockBreakingSpeed(context: MovementContext): Float {
        if (enchantedTools[context] == null)
            enchantedTools[context] = EnchantedItemFactory.getPickaxeItemStack(context.blockEntityData, context)

        val holderLookup = context.world.holderLookup(Registries.ENCHANTMENT)
        val holder =
            holderLookup.get(Enchantments.EFFICIENCY).getOrNull() ?: return super.getBlockBreakingSpeed(context)
        val efficiencyLevel =
            enchantedTools[context]?.getEnchantmentLevel(holder) ?: return super.getBlockBreakingSpeed(context)

        return super.getBlockBreakingSpeed(context) * (efficiencyLevel + 1)
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
