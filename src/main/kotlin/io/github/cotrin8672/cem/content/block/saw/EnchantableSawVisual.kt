package io.github.cotrin8672.cem.content.block.saw

import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual
import com.simibubi.create.content.kinetics.base.RotatingInstance
import com.simibubi.create.content.kinetics.saw.SawBlock
import com.simibubi.create.content.kinetics.saw.SawVisual
import com.simibubi.create.foundation.render.AllInstanceTypes
import dev.engine_room.flywheel.api.instance.Instance
import dev.engine_room.flywheel.api.instance.InstancerProvider
import dev.engine_room.flywheel.api.visualization.VisualizationContext
import dev.engine_room.flywheel.lib.material.Materials
import dev.engine_room.flywheel.lib.model.baked.BakedModelBuilder
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import java.util.function.Consumer

class EnchantableSawVisual(
    context: VisualizationContext?,
    blockEntity: EnchantableSawBlockEntity?,
    partialTick: Float,
) : KineticBlockEntityVisual<EnchantableSawBlockEntity>(context, blockEntity, partialTick) {
    companion object {
        fun enchantedShaft(instancerProvider: InstancerProvider, state: BlockState): RotatingInstance {
            val facing = state.getValue(BlockStateProperties.FACING)
            val axis = facing.axis
            return if (axis.isHorizontal) {
                val align = facing.opposite
                instancerProvider.instancer(
                    AllInstanceTypes.ROTATING,
                    BakedModelBuilder(AllPartialModels.SHAFT_HALF.get()).materialFunc { _, _ -> Materials.GLINT }
                        .build()
                )
                    .createInstance()
                    .rotateTo(0f, 0f, 1f, align.stepX.toFloat(), align.stepY.toFloat(), align.stepZ.toFloat())
            } else {
                instancerProvider.instancer(
                    AllInstanceTypes.ROTATING,
                    BakedModelBuilder(AllPartialModels.SHAFT.get()).materialFunc { _, _ -> Materials.GLINT }.build()
                )
                    .createInstance()
                    .rotateToFace(if (state.getValue(SawBlock.AXIS_ALONG_FIRST_COORDINATE)) Direction.Axis.X else Direction.Axis.Z)
            }
        }
    }

    private val rotatingModel: RotatingInstance = SawVisual.shaft(instancerProvider(), blockState)
        .setup(blockEntity)
        .setPosition(visualPosition)

    private val enchantedRotatingModel: RotatingInstance = enchantedShaft(instancerProvider(), blockState)
        .setup(blockEntity)
        .setPosition(visualPosition)

    init {
        rotatingModel.setChanged()
        enchantedRotatingModel.setChanged()
    }

    override fun update(partialTick: Float) {
        rotatingModel.setup(blockEntity).setChanged()
        enchantedRotatingModel.setup(blockEntity).setChanged()
    }

    override fun _delete() {
        rotatingModel.delete()
        enchantedRotatingModel.delete()
    }

    override fun collectCrumblingInstances(consumer: Consumer<Instance?>) {
        consumer.accept(rotatingModel)
        consumer.accept(enchantedRotatingModel)
    }

    override fun updateLight(partialTick: Float) {
        relight(rotatingModel)
        relight(enchantedRotatingModel)
    }
}
