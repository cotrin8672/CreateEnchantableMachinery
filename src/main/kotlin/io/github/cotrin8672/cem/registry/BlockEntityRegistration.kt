package io.github.cotrin8672.cem.registry

import com.simibubi.create.AllPartialModels
import com.simibubi.create.foundation.data.CreateBlockEntityBuilder
import com.tterrag.registrate.util.entry.BlockEntityEntry
import com.tterrag.registrate.util.nullness.NonNullFunction
import com.tterrag.registrate.util.nullness.NonNullSupplier
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
import io.github.cotrin8672.cem.CreateEnchantableMachinery.Companion.REGISTRATE
import io.github.cotrin8672.cem.client.visual.EnchantedOrientedRotatingVisual
import io.github.cotrin8672.cem.client.visual.EnchantedSingleAxisRotatingVisual
import io.github.cotrin8672.cem.content.block.crusher.EnchantableCrushingWheelBlockEntity
import io.github.cotrin8672.cem.content.block.crusher.EnchantableCrushingWheelControllerBlockEntity
import io.github.cotrin8672.cem.content.block.crusher.EnchantableCrushingWheelRenderer
import io.github.cotrin8672.cem.content.block.drill.EnchantableDrillBlockEntity
import io.github.cotrin8672.cem.content.block.drill.EnchantableDrillRenderer
import io.github.cotrin8672.cem.content.block.fan.EnchantableEncasedFanBlockEntity
import io.github.cotrin8672.cem.content.block.fan.EnchantableEncasedFanRenderer
import io.github.cotrin8672.cem.content.block.fan.EnchantableFanVisual
import io.github.cotrin8672.cem.content.block.harvester.EnchantableHarvesterBlockEntity
import io.github.cotrin8672.cem.content.block.harvester.EnchantableHarvesterRenderer
import io.github.cotrin8672.cem.content.block.millstone.EnchantableMillstoneBlockEntity
import io.github.cotrin8672.cem.content.block.millstone.EnchantableMillstoneRenderer
import io.github.cotrin8672.cem.content.block.mixer.EnchantableMechanicalMixerBlockEntity
import io.github.cotrin8672.cem.content.block.mixer.EnchantableMechanicalMixerRenderer
import io.github.cotrin8672.cem.content.block.mixer.EnchantableMixerVisual
import io.github.cotrin8672.cem.content.block.plough.EnchantablePloughBlockEntity
import io.github.cotrin8672.cem.content.block.plough.EnchantablePloughRenderer
import io.github.cotrin8672.cem.content.block.press.EnchantableMechanicalPressBlockEntity
import io.github.cotrin8672.cem.content.block.press.EnchantableMechanicalPressRenderer
import io.github.cotrin8672.cem.content.block.press.EnchantablePressVisual
import io.github.cotrin8672.cem.content.block.saw.EnchantableSawBlockEntity
import io.github.cotrin8672.cem.content.block.saw.EnchantableSawRenderer
import io.github.cotrin8672.cem.content.block.saw.EnchantableSawVisual
import net.minecraft.world.level.block.entity.BlockEntity

object BlockEntityRegistration {
    val ENCHANTABLE_MECHANICAL_DRILL: BlockEntityEntry<EnchantableDrillBlockEntity> = REGISTRATE
        .blockEntity<EnchantableDrillBlockEntity>("enchantable_drill", ::EnchantableDrillBlockEntity)
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

    val ENCHANTABLE_MECHANICAL_HARVESTER: BlockEntityEntry<EnchantableHarvesterBlockEntity> = REGISTRATE
        .blockEntity<EnchantableHarvesterBlockEntity>("enchantable_harvester", ::EnchantableHarvesterBlockEntity)
        .validBlocks(BlockRegistration.ENCHANTABLE_MECHANICAL_HARVESTER)
        .renderer { NonNullFunction(::EnchantableHarvesterRenderer) }
        .register()

    val ENCHANTABLE_ENCASED_FAN: BlockEntityEntry<EnchantableEncasedFanBlockEntity> = REGISTRATE
        .blockEntity<EnchantableEncasedFanBlockEntity>("enchantable_encased_fan", ::EnchantableEncasedFanBlockEntity)
        .visual(renderNormally = true) { SimpleBlockEntityVisualizer.Factory(::EnchantableFanVisual) }
        .validBlocks(BlockRegistration.ENCHANTABLE_ENCASED_FAN)
        .renderer { NonNullFunction(::EnchantableEncasedFanRenderer) }
        .register()

    val ENCHANTABLE_MILLSTONE: BlockEntityEntry<EnchantableMillstoneBlockEntity> = REGISTRATE
        .blockEntity<EnchantableMillstoneBlockEntity>("enchantable_millstone", ::EnchantableMillstoneBlockEntity)
        .visual(renderNormally = true) { EnchantedSingleAxisRotatingVisual.of(AllPartialModels.MILLSTONE_COG) }
        .validBlocks(BlockRegistration.ENCHANTABLE_MILLSTONE)
        .renderer { NonNullFunction(::EnchantableMillstoneRenderer) }
        .register()

    val ENCHANTABLE_CRUSHING_WHEEL_CONTROLLER: BlockEntityEntry<EnchantableCrushingWheelControllerBlockEntity> =
        REGISTRATE.blockEntity<EnchantableCrushingWheelControllerBlockEntity>(
            "enchantable_crushing_wheel_controller",
            ::EnchantableCrushingWheelControllerBlockEntity
        )
            .validBlocks(BlockRegistration.ENCHANTABLE_CRUSHING_WHEEL_CONTROLLER)
            .register()

    val ENCHANTABLE_CRUSHING_WHEEL: BlockEntityEntry<EnchantableCrushingWheelBlockEntity> =
        REGISTRATE.blockEntity<EnchantableCrushingWheelBlockEntity>(
            "enchantable_crushing_wheel",
            ::EnchantableCrushingWheelBlockEntity
        )
            .visual(renderNormally = true) { EnchantedSingleAxisRotatingVisual.of(AllPartialModels.CRUSHING_WHEEL) }
            .validBlocks(BlockRegistration.ENCHANTABLE_CRUSHING_WHEEL)
            .renderer { NonNullFunction(::EnchantableCrushingWheelRenderer) }
            .register()

    val ENCHANTABLE_MECHANICAL_PLOUGH: BlockEntityEntry<EnchantablePloughBlockEntity> = REGISTRATE
        .blockEntity<EnchantablePloughBlockEntity>("enchantable_plough", ::EnchantablePloughBlockEntity)
        .validBlocks(BlockRegistration.ENCHANTABLE_MECHANICAL_PLOUGH)
        .renderer { NonNullFunction(::EnchantablePloughRenderer) }
        .register()

    val ENCHANTABLE_MECHANICAL_PRESS: BlockEntityEntry<EnchantableMechanicalPressBlockEntity> =
        REGISTRATE.blockEntity<EnchantableMechanicalPressBlockEntity>(
            "enchantable_mechanical_press",
            ::EnchantableMechanicalPressBlockEntity
        )
            .visual(renderNormally = true) { SimpleBlockEntityVisualizer.Factory(::EnchantablePressVisual) }
            .validBlocks(BlockRegistration.ENCHANTABLE_MECHANICAL_PRESS)
            .renderer { NonNullFunction(::EnchantableMechanicalPressRenderer) }
            .register()

    val ENCHANTABLE_MECHANICAL_MIXER: BlockEntityEntry<EnchantableMechanicalMixerBlockEntity> = REGISTRATE
        .blockEntity<EnchantableMechanicalMixerBlockEntity>(
            "enchantable_mechanical_mixer",
            ::EnchantableMechanicalMixerBlockEntity
        )
        .visual(renderNormally = true) { SimpleBlockEntityVisualizer.Factory(::EnchantableMixerVisual) }
        .validBlocks(BlockRegistration.ENCHANTABLE_MECHANICAL_MIXER)
        .renderer { NonNullFunction(::EnchantableMechanicalMixerRenderer) }
        .register()

    fun register() {}

    private fun <T : BlockEntity, P> CreateBlockEntityBuilder<T, P>.visual(
        renderNormally: Boolean = false,
        instanceFactory: () -> SimpleBlockEntityVisualizer.Factory<T>,
    ): CreateBlockEntityBuilder<T, P> {
        return this.visual(NonNullSupplier(instanceFactory), renderNormally)
    }
}
