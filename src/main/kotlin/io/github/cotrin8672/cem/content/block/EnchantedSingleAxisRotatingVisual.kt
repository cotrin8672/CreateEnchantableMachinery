package io.github.cotrin8672.cem.content.block

import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual
import com.simibubi.create.foundation.render.AllInstanceTypes
import dev.engine_room.flywheel.api.instance.Instance
import dev.engine_room.flywheel.api.model.Model
import dev.engine_room.flywheel.api.visualization.VisualizationContext
import dev.engine_room.flywheel.lib.material.Materials
import dev.engine_room.flywheel.lib.model.Models
import dev.engine_room.flywheel.lib.model.baked.BakedModelBuilder
import dev.engine_room.flywheel.lib.model.baked.PartialModel
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
import net.minecraft.core.Direction
import java.util.function.Consumer

class EnchantedSingleAxisRotatingVisual<T : KineticBlockEntity>(
    context: VisualizationContext,
    blockEntity: T,
    partialTick: Float,
    from: Direction,
    model: Model,
    partialModel: PartialModel,
) : SingleAxisRotatingVisual<T>(context, blockEntity, partialTick, from, model) {
    private val enchantedRotatingModel = instancerProvider().instancer(
        AllInstanceTypes.ROTATING,
        BakedModelBuilder(partialModel.get()).materialFunc { _, _ -> Materials.GLINT }.build()
    )
        .createInstance()
        .rotateToFace(from, rotationAxis())
        .setup(blockEntity)
        .setPosition(visualPosition)

    companion object {
        fun <T : KineticBlockEntity> of(partial: PartialModel): SimpleBlockEntityVisualizer.Factory<T> {
            return SimpleBlockEntityVisualizer.Factory { context: VisualizationContext, blockEntity: T, partialTick: Float ->
                EnchantedSingleAxisRotatingVisual(
                    context,
                    blockEntity,
                    partialTick,
                    Direction.UP,
                    Models.partial(partial),
                    partial,
                )
            }
        }
    }

    init {
        enchantedRotatingModel.setChanged()
    }

    override fun update(pt: Float) {
        super.update(pt)
        enchantedRotatingModel.setup(blockEntity).setChanged()
    }

    override fun updateLight(partialTick: Float) {
        super.updateLight(partialTick)
        relight(enchantedRotatingModel)
    }

    override fun _delete() {
        super._delete()
        enchantedRotatingModel.delete()
    }

    override fun collectCrumblingInstances(consumer: Consumer<Instance?>) {
        super.collectCrumblingInstances(consumer)
        consumer.accept(enchantedRotatingModel)
    }
}
