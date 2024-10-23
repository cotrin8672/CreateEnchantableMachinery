package io.github.cotrin8672.registrate

import com.jozufozu.flywheel.api.MaterialManager
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance
import com.simibubi.create.content.kinetics.base.CutoutRotatingInstance
import com.simibubi.create.content.kinetics.drill.DrillInstance
import com.simibubi.create.content.kinetics.fan.FanInstance
import com.simibubi.create.content.kinetics.millstone.MillstoneCogInstance
import com.simibubi.create.content.kinetics.mixer.MixerInstance
import com.simibubi.create.content.kinetics.press.PressInstance
import com.simibubi.create.content.kinetics.saw.SawInstance
import com.simibubi.create.foundation.data.CreateBlockEntityBuilder
import com.tterrag.registrate.builders.BlockEntityBuilder
import com.tterrag.registrate.util.entry.BlockEntityEntry
import com.tterrag.registrate.util.nullness.NonNullFunction
import io.github.cotrin8672.CreateEnchantableMachinery.REGISTRATE
import io.github.cotrin8672.content.block.crusher.EnchantableCrushingWheelBlockEntity
import io.github.cotrin8672.content.block.crusher.EnchantableCrushingWheelControllerBlockEntity
import io.github.cotrin8672.content.block.crusher.EnchantableCrushingWheelRenderer
import io.github.cotrin8672.content.block.drill.EnchantableDrillBlockEntity
import io.github.cotrin8672.content.block.drill.EnchantableDrillRenderer
import io.github.cotrin8672.content.block.fan.EnchantableEncasedFanBlockEntity
import io.github.cotrin8672.content.block.fan.EnchantableEncasedFanRenderer
import io.github.cotrin8672.content.block.harvester.EnchantableHarvesterBlockEntity
import io.github.cotrin8672.content.block.harvester.EnchantableHarvesterRenderer
import io.github.cotrin8672.content.block.millstone.EnchantableMillstoneBlockEntity
import io.github.cotrin8672.content.block.millstone.EnchantableMillstoneRenderer
import io.github.cotrin8672.content.block.mixer.EnchantableMechanicalMixerBlockEntity
import io.github.cotrin8672.content.block.mixer.EnchantableMechanicalMixerRenderer
import io.github.cotrin8672.content.block.plough.EnchantablePloughBlockEntity
import io.github.cotrin8672.content.block.plough.EnchantablePloughRenderer
import io.github.cotrin8672.content.block.press.EnchantableMechanicalPressBlockEntity
import io.github.cotrin8672.content.block.press.EnchantableMechanicalPressRenderer
import io.github.cotrin8672.content.block.roller.EnchantableRollerBlockEntity
import io.github.cotrin8672.content.block.roller.EnchantableRollerRenderer
import io.github.cotrin8672.content.block.saw.EnchantableSawBlockEntity
import io.github.cotrin8672.content.block.saw.EnchantableSawRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.level.block.entity.BlockEntity
import java.util.function.BiFunction

object BlockEntityRegistration {
    val ENCHANTABLE_MECHANICAL_DRILL: BlockEntityEntry<EnchantableDrillBlockEntity> =
        REGISTRATE.blockEntity<EnchantableDrillBlockEntity>(
            "enchantable_drill",
            ::EnchantableDrillBlockEntity
        )
            .instance(renderNormally = true) { ::DrillInstance }
            .validBlocks(BlockRegistration.ENCHANTABLE_MECHANICAL_DRILL)
            .renderer(::EnchantableDrillRenderer)
            .register()

    val ENCHANTABLE_MECHANICAL_HARVESTER: BlockEntityEntry<EnchantableHarvesterBlockEntity> =
        REGISTRATE.blockEntity<EnchantableHarvesterBlockEntity>(
            "enchantable_harvester",
            ::EnchantableHarvesterBlockEntity
        )
            .renderer(::EnchantableHarvesterRenderer)
            .register()

    val ENCHANTABLE_MECHANICAL_SAW: BlockEntityEntry<EnchantableSawBlockEntity> =
        REGISTRATE.blockEntity<EnchantableSawBlockEntity>("enchantable_saw", ::EnchantableSawBlockEntity)
            .instance(renderNormally = true) { ::SawInstance }
            .validBlocks(BlockRegistration.ENCHANTABLE_MECHANICAL_SAW)
            .renderer(::EnchantableSawRenderer)
            .register()

    val ENCHANTABLE_MECHANICAL_PLOUGH: BlockEntityEntry<EnchantablePloughBlockEntity> =
        REGISTRATE.blockEntity<EnchantablePloughBlockEntity>("enchantable_plough", ::EnchantablePloughBlockEntity)
            .validBlocks(BlockRegistration.ENCHANTABLE_MECHANICAL_PLOUGH)
            .renderer(::EnchantablePloughRenderer)
            .register()

    val ENCHANTABLE_ENCASED_FAN: BlockEntityEntry<EnchantableEncasedFanBlockEntity> =
        REGISTRATE.blockEntity<EnchantableEncasedFanBlockEntity>(
            "enchantable_encase_fan",
            ::EnchantableEncasedFanBlockEntity
        )
            .instance(renderNormally = true) { ::FanInstance }
            .validBlocks(BlockRegistration.ENCHANTABLE_ENCASED_FAN)
            .renderer(::EnchantableEncasedFanRenderer)
            .register()

    val ENCHANTABLE_MILLSTONE: BlockEntityEntry<EnchantableMillstoneBlockEntity> =
        REGISTRATE.blockEntity<EnchantableMillstoneBlockEntity>(
            "enchantable_millstone",
            ::EnchantableMillstoneBlockEntity
        )
            .instance(renderNormally = true) { ::MillstoneCogInstance }
            .validBlocks(BlockRegistration.ENCHANTABLE_MILLSTONE)
            .renderer(::EnchantableMillstoneRenderer)
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
            .instance(renderNormally = true) { ::CutoutRotatingInstance }
            .validBlocks(BlockRegistration.ENCHANTABLE_CRUSHING_WHEEL)
            .renderer(::EnchantableCrushingWheelRenderer)
            .register()

    val ENCHANTABLE_MECHANICAL_PRESS: BlockEntityEntry<EnchantableMechanicalPressBlockEntity> =
        REGISTRATE.blockEntity<EnchantableMechanicalPressBlockEntity>(
            "enchantable_mechanical_press",
            ::EnchantableMechanicalPressBlockEntity
        )
            .instance(renderNormally = true) { ::PressInstance }
            .validBlocks(BlockRegistration.ENCHANTABLE_MECHANICAL_PRESS)
            .renderer(::EnchantableMechanicalPressRenderer)
            .register()

    val ENCHANTABLE_MECHANICAL_MIXER: BlockEntityEntry<EnchantableMechanicalMixerBlockEntity> =
        REGISTRATE.blockEntity<EnchantableMechanicalMixerBlockEntity>(
            "enchantable_mechanical_mixer",
            ::EnchantableMechanicalMixerBlockEntity
        )
            .instance(renderNormally = true) { ::MixerInstance }
            .validBlocks(BlockRegistration.ENCHANTABLE_MECHANICAL_MIXER)
            .renderer(::EnchantableMechanicalMixerRenderer)
            .register()

    val ENCHANTABLE_MECHANICAL_ROLLER: BlockEntityEntry<EnchantableRollerBlockEntity> =
        REGISTRATE.blockEntity<EnchantableRollerBlockEntity>(
            "enchantable_mechanical_roller",
            ::EnchantableRollerBlockEntity
        )
            .validBlocks(BlockRegistration.ENCHANTABLE_MECHANICAL_ROLLER)
            .renderer(::EnchantableRollerRenderer)
            .register()

    fun register() {}

    private inline fun <T : BlockEntity, P> CreateBlockEntityBuilder<T, P>.instance(
        renderNormally: Boolean = false,
        crossinline instanceFactory: () -> ((MaterialManager, T) -> BlockEntityInstance<in T>),
    ): CreateBlockEntityBuilder<T, P> = this.instance({
        BiFunction { materialManager: MaterialManager, be: T ->
            instanceFactory()(materialManager, be)
        }
    }, renderNormally)

    private fun <T : BlockEntity, P> BlockEntityBuilder<T, P>.renderer(
        block: (BlockEntityRendererProvider.Context) -> BlockEntityRenderer<T>,
    ) = this.renderer { NonNullFunction(block) }
}
