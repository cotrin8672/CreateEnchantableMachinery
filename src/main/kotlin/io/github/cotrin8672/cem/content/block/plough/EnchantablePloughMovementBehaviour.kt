package io.github.cotrin8672.cem.content.block.plough

import com.simibubi.create.content.contraptions.actors.plough.PloughMovementBehaviour
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.render.ContraptionMatrices
import com.simibubi.create.foundation.utility.BlockHelper
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld
import dev.engine_room.flywheel.api.visualization.VisualizationManager
import io.github.cotrin8672.cem.config.CemConfig
import io.github.cotrin8672.cem.util.EnchantedItemFactory
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.nbt.Tag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.Enchantments

class EnchantablePloughMovementBehaviour : PloughMovementBehaviour() {
    private var enchantedTool: ItemStack? = null

    override fun destroyBlock(context: MovementContext?, breakingPos: BlockPos?) {
        context ?: return

        if (enchantedTool == null) {
            enchantedTool = EnchantedItemFactory.getPickaxeItemStack(context)
        }

        BlockHelper.destroyBlockAs(context.world, breakingPos, null, enchantedTool, 1f) {
            this.dropItem(context, it)
        }
    }

    override fun getBlockBreakingSpeed(context: MovementContext): Float {
        val efficiencyLevel = if (enchantedTool == null) {
            val enchantmentTag = context.blockEntityData.getList("Enchantments", Tag.TAG_COMPOUND.toInt())
            EnchantmentHelper.deserializeEnchantments(enchantmentTag)[Enchantments.BLOCK_EFFICIENCY]
        } else {
            enchantedTool?.getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY)
        }

        return super.getBlockBreakingSpeed(context) * ((efficiencyLevel ?: 0) + 1)
    }

    override fun renderInContraption(
        context: MovementContext,
        renderWorld: VirtualRenderWorld,
        matrices: ContraptionMatrices,
        buffer: MultiBufferSource,
    ) {
        super.renderInContraption(context, renderWorld, matrices, buffer)
        if (CemConfig.CONFIG.renderGlint.get())
            if (!VisualizationManager.supportsVisualization(context.world))
                EnchantablePloughRenderer.renderInContraption(context, matrices, buffer)
    }
}
