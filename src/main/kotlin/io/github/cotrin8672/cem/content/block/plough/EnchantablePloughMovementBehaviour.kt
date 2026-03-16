package io.github.cotrin8672.cem.content.block.plough

import com.simibubi.create.AllBlocks
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
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.NbtUtils
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.FallingBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.Vec3
import java.util.*
import kotlin.jvm.optionals.getOrNull

class EnchantablePloughMovementBehaviour : PloughMovementBehaviour() {
    private val enchantedTools: MutableMap<MovementContext, ItemStack> = WeakHashMap()

    override fun destroyBlock(context: MovementContext?, breakingPos: BlockPos?) {
        context ?: return

        if (enchantedTools[context] == null)
            enchantedTools[context] = EnchantedItemFactory.getPickaxeItemStack(context.blockEntityData, context)

        BlockHelper.destroyBlockAs(context.world, breakingPos, null, enchantedTools[context], 1f) {
            this.collectOrDropItem(context, it)
        }
    }

    override fun onBlockBroken(context: MovementContext, pos: BlockPos, brokenState: BlockState) {
        // Keep base breaker behavior (falling-block wait).
        if (brokenState.block is FallingBlock) {
            val data = context.data
            data.putInt("WaitingTicks", 10)
            data.put("LastPos", NbtUtils.writeBlockPos(pos))
            context.stall = true
        }

        // Keep plough's snow special handling, but use an enchanted shovel tool
        // instead of a fixed vanilla iron shovel.
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
            ).forEach { collectOrDropItem(context, it) }
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

    override fun canBeDisabledVia(context: MovementContext?): ItemStack? {
        return AllBlocks.MECHANICAL_PLOUGH.asStack()
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
