package io.github.cotrin8672.cem.platform

import com.simibubi.create.foundation.data.CreateBlockEntityBuilder
import com.simibubi.create.foundation.data.CreateRegistrate
import com.simibubi.create.infrastructure.fabric.SimpleBlockEntityVisualFactory
import dev.engine_room.flywheel.api.visualization.VisualizationContext
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.cotrin8672.cem.FabricCem
import com.tterrag.registrate.util.nullness.NonNullSupplier
import net.minecraft.world.level.block.entity.BlockEntity

@ContributesBinding(PlatformScope::class)
@Inject
class FabricPlatformGraph : PlatformGraph {
    override val registrate: CreateRegistrate
        get() = FabricCem.registrate()

    override fun <T : BlockEntity, P> applyVisual(
        builder: CreateBlockEntityBuilder<T, P>,
        renderNormally: Boolean,
        instanceFactory: () -> dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer.Factory<T>,
    ): CreateBlockEntityBuilder<T, P> {
        val adapted: SimpleBlockEntityVisualFactory<T> = SimpleBlockEntityVisualFactory { context, blockEntity, partialTick ->
            instanceFactory().create(context as VisualizationContext, blockEntity, partialTick)
        }

        builder.visual(NonNullSupplier { adapted }, renderNormally)
        return builder
    }
}
