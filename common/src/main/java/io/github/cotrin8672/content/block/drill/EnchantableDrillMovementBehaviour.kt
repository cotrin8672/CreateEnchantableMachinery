package io.github.cotrin8672.content.block.drill

import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.render.ContraptionMatrices
import com.simibubi.create.content.kinetics.drill.DrillMovementBehaviour
import com.simibubi.create.foundation.utility.BlockHelper
import io.github.cotrin8672.config.Config
import io.github.cotrin8672.platform.ContraptionBlockBreaker
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.Enchantments

class EnchantableDrillMovementBehaviour : DrillMovementBehaviour() {
    override fun destroyBlock(context: MovementContext?, breakingPos: BlockPos) {
        val level = context?.world
        val fakePlayer = if (level is ServerLevel) {
            ContraptionBlockBreaker(level, context)
        } else null
        BlockHelper.destroyBlockAs(context?.world, breakingPos, fakePlayer, fakePlayer?.mainHandItem, 1f) {
            this.dropItem(context, it)
        }
    }

    override fun getBlockBreakingSpeed(context: MovementContext): Float {
        val enchantments = EnchantmentHelper.getEnchantments(ItemStack.EMPTY.apply {
            tag = context.blockEntityData
        })
        return super.getBlockBreakingSpeed(context) * ((enchantments[Enchantments.BLOCK_EFFICIENCY] ?: 0) + 1)
    }

    @Environment(EnvType.CLIENT)
    override fun renderInContraption(
        context: MovementContext,
        renderWorld: VirtualRenderWorld,
        matrices: ContraptionMatrices,
        buffer: MultiBufferSource,
    ) {
        super.renderInContraption(context, renderWorld, matrices, buffer)
        if (Config.renderGlint.get())
            EnchantableDrillRenderer.renderInContraption(context, renderWorld, matrices, buffer)
    }
}
