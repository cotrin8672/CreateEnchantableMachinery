package io.github.cotrin8672.cem.client.visual

import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import dev.engine_room.flywheel.api.visualization.VisualizationContext
import dev.engine_room.flywheel.lib.model.Models

open class EnchantedShaftVisual<T : KineticBlockEntity>(
    context: VisualizationContext,
    blockEntity: T,
    partialTick: Float,
) : EnchantedSingleAxisRotatingVisual<T>(
    context,
    blockEntity,
    partialTick,
    Models.partial(AllPartialModels.SHAFT),
    AllPartialModels.SHAFT
)

