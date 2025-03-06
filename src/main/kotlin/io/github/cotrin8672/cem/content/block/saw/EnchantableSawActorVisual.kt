package io.github.cotrin8672.cem.content.block.saw

import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.render.ActorVisual
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual
import com.simibubi.create.content.kinetics.saw.SawVisual
import dev.engine_room.flywheel.api.visualization.VisualizationContext
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class EnchantableSawActorVisual(
    visualizationContext: VisualizationContext,
    world: BlockAndTintGetter,
    context: MovementContext,
) : ActorVisual(visualizationContext, world, context) {
    private val state: BlockState = context.state
    private val localPos: BlockPos = context.localPos
    private val facing: Direction = state.getValue(BlockStateProperties.FACING)
    private val axis: Direction.Axis = facing.axis

    private val shaft = SawVisual.shaft(instancerProvider, state)

    private val enchantedShaft = EnchantableSawVisual.enchantedShaft(instancerProvider, state)

    init {
        shaft.setRotationAxis(axis)
            .setRotationOffset(KineticBlockEntityVisual.rotationOffset(state, axis, localPos))
            .setPosition(localPos)
            .light(localBlockLight(), 0)
            .setChanged()

        enchantedShaft.setRotationAxis(axis)
            .setRotationOffset(KineticBlockEntityVisual.rotationOffset(state, axis, localPos))
            .setPosition(localPos)
            .light(localBlockLight(), 0)
            .setChanged()
    }

    override fun _delete() {
        shaft.delete()
        enchantedShaft.delete()
    }
}
