package io.github.cotrin8672.cem.platform

import com.simibubi.create.foundation.data.CreateBlockEntityBuilder
import com.simibubi.create.infrastructure.fabric.SimpleBlockEntityVisualFactory
import dev.engine_room.flywheel.api.visualization.VisualizationContext
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
import net.minecraft.world.level.block.entity.BlockEntity

class BlockEntityBuilderHelperImpl : BlockEntityBuilderHelper {
    override fun <T : BlockEntity, P> visual(
        builder: CreateBlockEntityBuilder<T, P>,
        renderNormally: Boolean,
        instanceFactory: () -> SimpleBlockEntityVisualizer.Factory<T>,
    ): CreateBlockEntityBuilder<T, P> {
        val adapted: SimpleBlockEntityVisualFactory<T> =
            SimpleBlockEntityVisualFactory { context, blockEntity, partialTick ->
                instanceFactory().create(context as VisualizationContext, blockEntity, partialTick)
            }
        return builder.apply { visual({ adapted }, renderNormally) }
    }
}