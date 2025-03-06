package io.github.cotrin8672.cem.registry

import com.simibubi.create.AllPartialModels
import com.simibubi.create.foundation.data.CreateBlockEntityBuilder
import com.tterrag.registrate.util.entry.BlockEntityEntry
import com.tterrag.registrate.util.nullness.NonNullFunction
import com.tterrag.registrate.util.nullness.NonNullSupplier
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
import io.github.cotrin8672.cem.CreateEnchantableMachinery.Companion.REGISTRATE
import io.github.cotrin8672.cem.content.block.EnchantedOrientedRotatingVisual
import io.github.cotrin8672.cem.content.block.drill.EnchantableDrillBlockEntity
import io.github.cotrin8672.cem.content.block.drill.EnchantableDrillRenderer
import io.github.cotrin8672.cem.content.block.saw.EnchantableSawBlockEntity
import io.github.cotrin8672.cem.content.block.saw.EnchantableSawRenderer
import io.github.cotrin8672.cem.content.block.saw.EnchantableSawVisual
import net.minecraft.world.level.block.entity.BlockEntity

object BlockEntityRegistration {
    val ENCHANTABLE_MECHANICAL_DRILL: BlockEntityEntry<EnchantableDrillBlockEntity> =
        REGISTRATE.blockEntity<EnchantableDrillBlockEntity>("enchantable_drill") { type, pos, state ->
            EnchantableDrillBlockEntity(type, pos, state)
        }
            .visual(renderNormally = true) { EnchantedOrientedRotatingVisual.of(AllPartialModels.DRILL_HEAD) }
            .validBlocks(BlockRegistration.ENCHANTABLE_MECHANICAL_DRILL)
            .renderer { NonNullFunction(::EnchantableDrillRenderer) }
            .register()

    val ENCHANTABLE_MECHANICAL_SAW: BlockEntityEntry<EnchantableSawBlockEntity> = REGISTRATE
        .blockEntity<EnchantableSawBlockEntity>("enchantable_saw", ::EnchantableSawBlockEntity)
        .visual(renderNormally = true) { SimpleBlockEntityVisualizer.Factory(::EnchantableSawVisual) }
        .validBlocks(BlockRegistration.ENCHANTABLE_MECHANICAL_SAW)
        .renderer { NonNullFunction(::EnchantableSawRenderer) }
        .register()

    fun register() {}

    private fun <T : BlockEntity, P> CreateBlockEntityBuilder<T, P>.visual(
        renderNormally: Boolean = false,
        instanceFactory: () -> SimpleBlockEntityVisualizer.Factory<T>,
    ): CreateBlockEntityBuilder<T, P> {
        return this.visual(NonNullSupplier(instanceFactory), renderNormally)
    }
}
