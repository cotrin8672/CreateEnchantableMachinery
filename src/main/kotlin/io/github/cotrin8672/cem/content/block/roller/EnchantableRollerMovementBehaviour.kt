package io.github.cotrin8672.cem.content.block.roller

import com.simibubi.create.AllBlocks
import com.simibubi.create.content.contraptions.actors.roller.RollerMovementBehaviour
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.render.ActorVisual
import com.simibubi.create.content.contraptions.render.ContraptionMatrices
import com.simibubi.create.foundation.utility.BlockHelper
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld
import dev.engine_room.flywheel.api.visualization.VisualizationContext
import dev.engine_room.flywheel.api.visualization.VisualizationManager
import io.github.cotrin8672.cem.util.EnchantedItemFactory
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.nbt.Tag
import net.minecraft.tags.BlockTags
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.Enchantments
import java.util.*

class EnchantableRollerMovementBehaviour : RollerMovementBehaviour() {
    private var enchantedTools: MutableMap<MovementContext, ItemStack> = WeakHashMap()

    override fun createVisual(
        visualizationContext: VisualizationContext,
        simulationWorld: VirtualRenderWorld,
        movementContext: MovementContext,
    ): ActorVisual {
        return EnchantableRollerActorVisual(visualizationContext, simulationWorld, movementContext)
    }

    override fun destroyBlock(context: MovementContext?, breakingPos: BlockPos) {
        context ?: return
        val blockState = context.world.getBlockState(breakingPos)
        val noHarvest = (
                blockState.`is`(BlockTags.NEEDS_IRON_TOOL)
                        || blockState.`is`(BlockTags.NEEDS_STONE_TOOL)
                        || blockState.`is`(BlockTags.NEEDS_DIAMOND_TOOL)
                )

        if (enchantedTools[context] == null) {
            enchantedTools[context] = EnchantedItemFactory.getPickaxeItemStack(context)
        }

        BlockHelper.destroyBlockAs(context.world, breakingPos, null, enchantedTools[context], 1f) {
            if ((noHarvest || context.world.random.nextBoolean()))
                return@destroyBlockAs
            this.dropItem(context, it)
        }

        super.destroyBlock(context, breakingPos)
    }

    override fun getBlockBreakingSpeed(context: MovementContext): Float {
        val enchantedTool = enchantedTools[context]
        val efficiencyLevel = if (enchantedTool == null) {
            val enchantmentTag = context.blockEntityData.getList("Enchantments", Tag.TAG_COMPOUND.toInt())
            EnchantmentHelper.deserializeEnchantments(enchantmentTag)[Enchantments.BLOCK_EFFICIENCY]
        } else {
            enchantedTool.getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY)
        }

        return super.getBlockBreakingSpeed(context) * ((efficiencyLevel ?: 0) + 1)
    }

    override fun canBeDisabledVia(context: MovementContext?): ItemStack? {
        return AllBlocks.MECHANICAL_ROLLER.asStack()
    }

    override fun renderInContraption(
        context: MovementContext,
        renderWorld: VirtualRenderWorld,
        matrices: ContraptionMatrices,
        buffers: MultiBufferSource,
    ) {
        if (!VisualizationManager.supportsVisualization(context.world))
            EnchantableRollerRenderer.renderInContraption(context, renderWorld, matrices, buffers)
    }
}
