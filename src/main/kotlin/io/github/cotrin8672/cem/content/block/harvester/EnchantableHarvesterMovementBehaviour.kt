package io.github.cotrin8672.cem.content.block.harvester

import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterRenderer
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.render.ActorVisual
import com.simibubi.create.content.contraptions.render.ContraptionMatrices
import com.simibubi.create.foundation.item.ItemHelper
import com.simibubi.create.foundation.utility.BlockHelper
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld
import com.simibubi.create.infrastructure.config.AllConfigs
import dev.engine_room.flywheel.api.visualization.VisualizationContext
import io.github.cotrin8672.cem.config.CemConfig
import io.github.cotrin8672.cem.util.EnchantedItemFactory
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.tags.BlockTags
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.CocoaBlock
import net.minecraft.world.level.block.CropBlock
import net.minecraft.world.level.block.GrowingPlantBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.IntegerProperty
import org.apache.commons.lang3.mutable.MutableBoolean

class EnchantableHarvesterMovementBehaviour : HarvesterMovementBehaviour() {
    override fun visitNewPosition(context: MovementContext, pos: BlockPos) {
        val world = context.world
        val stateVisited = world.getBlockState(pos)
        var notCropButCuttable = false

        if (world.isClientSide) return

        if (!isValidCrop(world, pos, stateVisited)) {
            if (isValidOther(world, pos, stateVisited)) notCropButCuttable = true
            else return
        }

        var item = EnchantedItemFactory.getPickaxeItemStack(context.blockEntityData, context)
        var effectChance = 1f

        if (stateVisited.`is`(BlockTags.LEAVES)) {
            item = ItemStack(Items.SHEARS)
            effectChance = .45f
        }

        val seedSubtracted = MutableBoolean(notCropButCuttable)
        BlockHelper.destroyBlockAs(world, pos, null, item, effectChance) { stack: ItemStack ->
            if (
                AllConfigs.server().kinetics.harvesterReplants.get() && !seedSubtracted.value
                && ItemHelper.sameItem(stack, ItemStack(stateVisited.block))
            ) {
                stack.shrink(1)
                seedSubtracted.setTrue()
            }
            if (!stack.isEmpty) dropItem(context, stack)
        }

        val cutCrop = cutCrop(world, pos, stateVisited)
        world.setBlockAndUpdate(pos, if (cutCrop.canSurvive(world, pos)) cutCrop else Blocks.AIR.defaultBlockState())
    }

    private fun cutCrop(world: Level, pos: BlockPos, state: BlockState): BlockState {
        if (!AllConfigs.server().kinetics.harvesterReplants.get()) {
            if (state.fluidState.isEmpty) return Blocks.AIR.defaultBlockState()
            return state.fluidState.createLegacyBlock()
        }

        val block = state.block
        if (block is CropBlock) {
            return block.getStateForAge(0)
        }
        if (block === Blocks.SWEET_BERRY_BUSH) {
            return state.setValue(BlockStateProperties.AGE_3, 1)
        }
        if (block === Blocks.SUGAR_CANE || block is GrowingPlantBlock) {
            if (state.fluidState.isEmpty) return Blocks.AIR.defaultBlockState()
            return state.fluidState.createLegacyBlock()
        }
        if (state.getCollisionShape(world, pos).isEmpty || block is CocoaBlock) {
            for (property in state.properties) {
                if (property !is IntegerProperty) continue
                if (property.getName() != BlockStateProperties.AGE_1.name) continue
                return state.setValue(property, 0)
            }
        }

        if (state.fluidState.isEmpty) return Blocks.AIR.defaultBlockState()
        return state.fluidState.createLegacyBlock()
    }

    override fun createVisual(
        visualizationContext: VisualizationContext,
        simulationWorld: VirtualRenderWorld,
        movementContext: MovementContext,
    ): ActorVisual? {
        return null
    }

    override fun renderInContraption(
        context: MovementContext,
        renderWorld: VirtualRenderWorld,
        matrices: ContraptionMatrices,
        buffers: MultiBufferSource,
    ) {
        HarvesterRenderer.renderInContraption(context, renderWorld, matrices, buffers)
        if (CemConfig.CONFIG.renderGlint.get())
            EnchantableHarvesterRenderer.renderInContraption(context, renderWorld, matrices, buffers)
    }
}
