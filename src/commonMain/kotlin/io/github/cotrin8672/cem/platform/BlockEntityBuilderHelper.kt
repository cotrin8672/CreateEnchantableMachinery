package io.github.cotrin8672.cem.platform

import com.simibubi.create.foundation.data.CreateBlockEntityBuilder
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
import net.minecraft.world.level.block.entity.BlockEntity
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface BlockEntityBuilderHelper {
    companion object : KoinComponent {
        private val instance by inject<BlockEntityBuilderHelper>()

        operator fun invoke() = instance
    }

    fun <T : BlockEntity, P> visual(
        builder: CreateBlockEntityBuilder<T, P>,
        renderNormally: Boolean,
        instanceFactory: () -> SimpleBlockEntityVisualizer.Factory<T>,
    ): CreateBlockEntityBuilder<T, P>
}