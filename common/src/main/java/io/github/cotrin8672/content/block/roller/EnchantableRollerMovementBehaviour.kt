package io.github.cotrin8672.content.block.roller

import com.jozufozu.flywheel.api.MaterialManager
import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld
import com.simibubi.create.content.contraptions.actors.roller.RollerMovementBehaviour
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.render.ActorInstance
import com.simibubi.create.content.contraptions.render.ContraptionMatrices
import com.simibubi.create.foundation.utility.BlockHelper
import io.github.cotrin8672.config.Config
import io.github.cotrin8672.content.entity.FakePlayerFactory
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.BlockTags
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.Enchantments
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class EnchantableRollerMovementBehaviour : RollerMovementBehaviour(), KoinComponent {
    private val fakePlayerFactory: FakePlayerFactory by inject()

    override fun createInstance(
        materialManager: MaterialManager?,
        simulationWorld: VirtualRenderWorld?,
        context: MovementContext?,
    ): ActorInstance? {
        return null
    }

    override fun hasSpecialInstancedRendering(): Boolean {
        return false
    }

    override fun destroyBlock(context: MovementContext?, breakingPos: BlockPos) {
        context ?: return
        val blockState = context.world.getBlockState(breakingPos)
        val noHarvest = (
                blockState.`is`(BlockTags.NEEDS_IRON_TOOL)
                        || blockState.`is`(BlockTags.NEEDS_STONE_TOOL)
                        || blockState.`is`(BlockTags.NEEDS_DIAMOND_TOOL)
                )

        val level = context.world
        val fakePlayer = if (level is ServerLevel) {
            fakePlayerFactory.getContraptionBlockBreaker(level, context)
        } else null
        val enchantments = EnchantmentHelper.getEnchantments(ItemStack.EMPTY.apply {
            tag = context.blockEntityData
        })
        BlockHelper.destroyBlockAs(context.world, breakingPos, fakePlayer, fakePlayer?.mainHandItem, 1f) {
            if ((!enchantments.contains(Enchantments.SILK_TOUCH)) && (noHarvest || context.world.random.nextBoolean()))
                return@destroyBlockAs
            this.dropItem(context, it)
        }

        super.destroyBlock(context, breakingPos)
    }

    override fun getBlockBreakingSpeed(context: MovementContext?): Float {
        val speed = super.getBlockBreakingSpeed(context)
        val enchantments = EnchantmentHelper.getEnchantments(ItemStack.EMPTY.apply {
            tag = context?.blockEntityData
        })
        return speed * ((enchantments[Enchantments.BLOCK_EFFICIENCY] ?: 0) + 1)
    }

    override fun renderInContraption(
        context: MovementContext,
        renderWorld: VirtualRenderWorld,
        matrices: ContraptionMatrices,
        buffers: MultiBufferSource,
    ) {
        super.renderInContraption(context, renderWorld, matrices, buffers)
        if (Config.renderGlint.get())
            EnchantableRollerRenderer.renderInContraption(context, renderWorld, matrices, buffers)
    }
}
