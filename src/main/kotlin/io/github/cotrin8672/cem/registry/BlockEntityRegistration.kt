package io.github.cotrin8672.cem.registry

import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual
import com.simibubi.create.foundation.data.CreateBlockEntityBuilder
import com.tterrag.registrate.util.entry.BlockEntityEntry
import com.tterrag.registrate.util.nullness.NonNullFunction
import com.tterrag.registrate.util.nullness.NonNullSupplier
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
import io.github.cotrin8672.cem.CreateEnchantableMachinery.Companion.REGISTRATE
import io.github.cotrin8672.cem.content.block.drill.EnchantableDrillBlockEntity
import io.github.cotrin8672.cem.content.block.drill.EnchantableDrillRenderer
import net.minecraft.world.level.block.entity.BlockEntity

object BlockEntityRegistration {
    val ENCHANTABLE_MECHANICAL_DRILL: BlockEntityEntry<EnchantableDrillBlockEntity> =
        REGISTRATE.blockEntity<EnchantableDrillBlockEntity>("enchantable_drill") { type, pos, state ->
            EnchantableDrillBlockEntity(type, pos, state)
        }
            .visual(renderNormally = true) { OrientedRotatingVisual.of(AllPartialModels.DRILL_HEAD) }
            .validBlocks(BlockRegistration.ENCHANTABLE_MECHANICAL_DRILL)
            .renderer { NonNullFunction(::EnchantableDrillRenderer) }
            .register()

    fun register() {}

    private fun <T : BlockEntity, P> CreateBlockEntityBuilder<T, P>.visual(
        renderNormally: Boolean = false,
        instanceFactory: () -> SimpleBlockEntityVisualizer.Factory<T>,
    ): CreateBlockEntityBuilder<T, P> {
        return this.visual(NonNullSupplier(instanceFactory), renderNormally)
    }
}
