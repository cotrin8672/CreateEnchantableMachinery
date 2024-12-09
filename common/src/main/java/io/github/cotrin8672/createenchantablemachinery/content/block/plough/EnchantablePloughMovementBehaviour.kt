package io.github.cotrin8672.createenchantablemachinery.content.block.plough

import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld
import com.simibubi.create.content.contraptions.actors.plough.PloughMovementBehaviour
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.render.ContraptionMatrices
import com.simibubi.create.foundation.utility.BlockHelper
import io.github.cotrin8672.createenchantablemachinery.config.Config
import io.github.cotrin8672.createenchantablemachinery.platform.ContraptionBlockBreaker
import io.github.cotrin8672.createenchantablemachinery.util.EnchantedItemFactory
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.EnchantmentInstance
import net.minecraft.world.item.enchantment.Enchantments

class EnchantablePloughMovementBehaviour : PloughMovementBehaviour() {
    override fun destroyBlock(context: MovementContext?, breakingPos: BlockPos?) {
        val level = context?.world
        val enchantedItem = EnchantedItemFactory.getHoeItemStack(
            *EnchantmentHelper.getEnchantments(ItemStack.EMPTY.apply { tag = context?.blockEntityData })
                .map { EnchantmentInstance(it.key, it.value) }
                .toTypedArray()
        )
        val fakePlayer = if (level is ServerLevel) {
            ContraptionBlockBreaker(level, context, enchantedItem)
        } else null
        BlockHelper.destroyBlockAs(context?.world, breakingPos, fakePlayer, fakePlayer?.mainHandItem, 1f) {
            this.dropItem(context, it)
        }
    }

    override fun getBlockBreakingSpeed(context: MovementContext?): Float {
        val enchantments = EnchantmentHelper.getEnchantments(ItemStack.EMPTY.apply {
            tag = context?.blockEntityData
        })
        return super.getBlockBreakingSpeed(context) * ((enchantments[Enchantments.BLOCK_EFFICIENCY] ?: 0) + 1)
    }

    override fun renderInContraption(
        context: MovementContext,
        renderWorld: VirtualRenderWorld,
        matrices: ContraptionMatrices,
        buffer: MultiBufferSource,
    ) {
        super.renderInContraption(context, renderWorld, matrices, buffer)
        if (Config.renderGlint.get())
            EnchantablePloughRenderer.renderInContraption(context, matrices, buffer)
    }
}
