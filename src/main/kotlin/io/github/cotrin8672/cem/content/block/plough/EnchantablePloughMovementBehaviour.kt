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
import net.minecraft.nbt.NbtUtils
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.FallingBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.Vec3
import java.util.*

class EnchantablePloughMovementBehaviour : PloughMovementBehaviour() {
    private var enchantedTools: MutableMap<MovementContext, ItemStack> = WeakHashMap()

    override fun destroyBlock(context: MovementContext?, breakingPos: BlockPos?) {
        context ?: return

        if (enchantedTools[context] == null)
            enchantedTools[context] = EnchantedItemFactory.getPickaxeItemStack(context.blockEntityData, context)

        BlockHelper.destroyBlockAs(context.world, breakingPos, null, enchantedTools[context], 1f) {
            this.dropItem(context, it)
        }
    }

    override fun onBlockBroken(context: MovementContext, pos: BlockPos, brokenState: BlockState) {
        if (brokenState.block is FallingBlock) {
            val data = context.data
            data.putInt("WaitingTicks", 10)
            data.put("LastPos", NbtUtils.writeBlockPos(pos))
            context.stall = true
        }

        if (brokenState.block == Blocks.SNOW && context.world is ServerLevel) {
            val world = context.world as ServerLevel
            val enchantments = EnchantedItemFactory.getEnchantments(context.blockEntityData, context)
            val shovel = EnchantedItemFactory.getToolItemStack(Items.IRON_SHOVEL, enchantments)
            brokenState.getDrops(
                LootParams.Builder(world)
                    .withParameter(LootContextParams.BLOCK_STATE, brokenState)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                    .withParameter(LootContextParams.THIS_ENTITY, context.contraption.entity)
                    .withParameter(LootContextParams.TOOL, shovel)
            ).forEach { dropItem(context, it) }
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
