package io.github.cotrin8672.cem.platform

import com.simibubi.create.foundation.data.CreateBlockEntityBuilder
import com.simibubi.create.foundation.data.CreateRegistrate
import com.tterrag.registrate.util.nullness.NonNullSupplier
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.cotrin8672.cem.Cem
import net.minecraft.world.level.block.entity.BlockEntity

@ContributesBinding(PlatformScope::class)
@Inject
class ForgePlatformGraph : PlatformGraph {
    override val registrate: CreateRegistrate
        get() = Cem.registrate()

    override fun <T : BlockEntity, P> applyVisual(
        builder: CreateBlockEntityBuilder<T, P>,
        renderNormally: Boolean,
        instanceFactory: () -> SimpleBlockEntityVisualizer.Factory<T>,
    ): CreateBlockEntityBuilder<T, P> {
        builder.visual(NonNullSupplier { instanceFactory() }, renderNormally)
        return builder
    }
}
