package io.github.cotrin8672.cem.registry

import com.simibubi.create.AllPartialModels
import com.simibubi.create.foundation.data.CreateBlockEntityBuilder
import com.tterrag.registrate.util.entry.BlockEntityEntry
import com.tterrag.registrate.util.nullness.NonNullFunction
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
import io.github.cotrin8672.cem.Cem.REGISTRATE
import io.github.cotrin8672.cem.client.visual.EnchantedOrientedRotatingVisual
import io.github.cotrin8672.cem.content.block.drill.EnchantableDrillBlockEntity
import io.github.cotrin8672.cem.content.block.drill.EnchantableDrillRenderer
import io.github.cotrin8672.cem.platform.PlatformGraph
import dev.zacsweers.metro.createGraph
import net.minecraft.world.level.block.entity.BlockEntity

object CemBlockEntityTypes {
    val ENCHANTABLE_MECHANICAL_DRILL: BlockEntityEntry<EnchantableDrillBlockEntity> = REGISTRATE
        .blockEntity<EnchantableDrillBlockEntity>("enchantable_drill", ::EnchantableDrillBlockEntity)
        .visual(renderNormally = true) { EnchantedOrientedRotatingVisual.of(AllPartialModels.DRILL_HEAD) }
        .validBlocks(CemBlocks.ENCHANTABLE_MECHANICAL_DRILL)
        .renderer { NonNullFunction(::EnchantableDrillRenderer) }
        .register()

    fun register() {}

    private fun <T : BlockEntity, P> CreateBlockEntityBuilder<T, P>.visual(
        renderNormally: Boolean = false,
        instanceFactory: () -> SimpleBlockEntityVisualizer.Factory<T>,
    ): CreateBlockEntityBuilder<T, P> {
        return createGraph<PlatformGraph>().applyVisual(this, renderNormally, instanceFactory)
    }
}
