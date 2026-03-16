package io.github.cotrin8672.cem.content.block.drill

import com.simibubi.create.AllBlocks
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.render.ActorVisual
import com.simibubi.create.content.contraptions.render.ContraptionMatrices
import com.simibubi.create.content.kinetics.drill.DrillMovementBehaviour
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld
import dev.engine_room.flywheel.api.visualization.VisualizationContext
import dev.engine_room.flywheel.api.visualization.VisualizationManager
import io.github.cotrin8672.cem.util.EnchantedItemFactory
import io.github.cotrin8672.cem.util.destroyBlockAs
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.nbt.Tag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import java.util.*

class EnchantableDrillMovementBehaviour : DrillMovementBehaviour() {
    private var enchantedTools: MutableMap<MovementContext, ItemStack> = WeakHashMap()

    override fun destroyBlock(context: MovementContext?, breakingPos: BlockPos) {
        context ?: return
        if (enchantedTools[context] == null)
            enchantedTools[context] = EnchantedItemFactory.getPickaxeItemStack(context.blockEntityData, context)

        destroyBlockAs(context.world, breakingPos, null, enchantedTools[context], 1f) {
            this.dropItem(context, it)
        }
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

    override fun createVisual(
        visualizationContext: VisualizationContext,
        simulationWorld: VirtualRenderWorld,
        movementContext: MovementContext,
    ): ActorVisual {
        return EnchantableDrillActorVisual(visualizationContext, simulationWorld, movementContext)
    }

    override fun canBeDisabledVia(context: MovementContext?): ItemStack? {
        return AllBlocks.MECHANICAL_DRILL.asStack()
    }

    @OnlyIn(Dist.CLIENT)
    override fun renderInContraption(
        context: MovementContext,
        renderWorld: VirtualRenderWorld,
        matrices: ContraptionMatrices,
        buffer: MultiBufferSource,
    ) {
        if (!VisualizationManager.supportsVisualization(context.world))
            EnchantableDrillRenderer.renderInContraption(context, renderWorld, matrices, buffer)
    }
}
