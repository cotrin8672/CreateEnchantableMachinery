package io.github.cotrin8672.registrate

import com.jozufozu.flywheel.api.MaterialManager
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance
import com.simibubi.create.content.kinetics.drill.DrillInstance
import com.simibubi.create.content.kinetics.saw.SawInstance
import com.simibubi.create.foundation.data.CreateBlockEntityBuilder
import com.tterrag.registrate.util.entry.BlockEntityEntry
import com.tterrag.registrate.util.nullness.NonNullFunction
import io.github.cotrin8672.CreateEnchantableMachinery.REGISTRATE
import io.github.cotrin8672.content.block.drill.EnchantableDrillBlockEntity
import io.github.cotrin8672.content.block.drill.EnchantableDrillRenderer
import io.github.cotrin8672.content.block.harvester.EnchantableHarvesterBlockEntity
import io.github.cotrin8672.content.block.harvester.EnchantableHarvesterRenderer
import io.github.cotrin8672.content.block.saw.EnchantableSawBlockEntity
import io.github.cotrin8672.content.block.saw.EnchantableSawRenderer
import net.minecraft.world.level.block.entity.BlockEntity
import java.util.function.BiFunction

object BlockEntityRegistration {
    val ENCHANTABLE_MECHANICAL_DRILL: BlockEntityEntry<EnchantableDrillBlockEntity> =
        REGISTRATE.blockEntity<EnchantableDrillBlockEntity>("enchantable_drill") { type, pos, state ->
            EnchantableDrillBlockEntity(type, pos, state)
        }
            .instance(renderNormally = true) { ::DrillInstance }
            .validBlocks(BlockRegistration.ENCHANTABLE_MECHANICAL_DRILL)
            .renderer { NonNullFunction(::EnchantableDrillRenderer) }
            .register()

    val ENCHANTABLE_MECHANICAL_HARVESTER: BlockEntityEntry<EnchantableHarvesterBlockEntity> =
        REGISTRATE.blockEntity<EnchantableHarvesterBlockEntity>("enchantable_harvester") { type, pos, state ->
            EnchantableHarvesterBlockEntity(type, pos, state)
        }
            .validBlocks(BlockRegistration.ENCHANTABLE_MECHANICAL_HARVESTER)
            .renderer { NonNullFunction(::EnchantableHarvesterRenderer) }
            .register()

    val ENCHANTABLE_MECHANICAL_SAW: BlockEntityEntry<EnchantableSawBlockEntity> =
        REGISTRATE.blockEntity<EnchantableSawBlockEntity>("enchantable_saw") { type, pos, state ->
            EnchantableSawBlockEntity(type, pos, state)
        }
            .instance(renderNormally = true) { ::SawInstance }
            .validBlocks(BlockRegistration.ENCHANTABLE_MECHANICAL_SAW)
            .renderer { NonNullFunction(::EnchantableSawRenderer) }
            .register()

    fun register() {}

    private fun <T : BlockEntity, P> CreateBlockEntityBuilder<T, P>.instance(
        renderNormally: Boolean = false,
        instanceFactory: () -> ((MaterialManager, T) -> BlockEntityInstance<in T>),
    ): CreateBlockEntityBuilder<T, P> {
        return this.instance({
            BiFunction { materialManager: MaterialManager, be: T ->
                instanceFactory()(materialManager, be)
            }
        }, renderNormally)
    }
}
