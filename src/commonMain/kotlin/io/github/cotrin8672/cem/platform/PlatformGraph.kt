package io.github.cotrin8672.cem.platform

import com.simibubi.create.foundation.data.CreateBlockEntityBuilder
import com.simibubi.create.foundation.data.CreateRegistrate
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
import dev.zacsweers.metro.DependencyGraph
import net.minecraft.world.level.block.entity.BlockEntity

object PlatformScope

@DependencyGraph(scope = PlatformScope::class)
interface PlatformGraph {
    val registrate: CreateRegistrate

    fun <T : BlockEntity, P> applyVisual(
        builder: CreateBlockEntityBuilder<T, P>,
        renderNormally: Boolean,
        instanceFactory: () -> SimpleBlockEntityVisualizer.Factory<T>,
    ): CreateBlockEntityBuilder<T, P>
}
