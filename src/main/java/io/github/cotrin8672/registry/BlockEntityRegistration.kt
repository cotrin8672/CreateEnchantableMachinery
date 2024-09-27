package io.github.cotrin8672.registry

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
import com.tterrag.registrate.util.entry.BlockEntityEntry
import com.tterrag.registrate.util.nullness.NonNullFunction
import io.github.cotrin8672.CreateEnchantableMachinery.Companion.REGISTRATE
import io.github.cotrin8672.blockentity.*
import io.github.cotrin8672.blockentity.fan.EnchantableEncasedFanBlockEntity
import io.github.cotrin8672.renderer.*
import net.minecraft.world.level.block.entity.BlockEntity
import java.util.function.BiFunction

@Suppress("unused")
class BlockEntityRegistration {
    companion object {
        @JvmStatic
        val ENCHANTABLE_MECHANICAL_DRILL: BlockEntityEntry<EnchantableDrillBlockEntity> =
            REGISTRATE.blockEntity<EnchantableDrillBlockEntity>("enchantable_drill") { type, pos, state ->
                EnchantableDrillBlockEntity(type, pos, state)
            }
                .instance(renderNormally = true) { ::DrillInstance }
                .validBlocks(BlockRegistration.ENCHANTABLE_MECHANICAL_DRILL)
                .renderer { NonNullFunction(::EnchantableDrillRenderer) }
                .register()

        @JvmStatic
        val ENCHANTABLE_MECHANICAL_HARVESTER: BlockEntityEntry<EnchantableHarvesterBlockEntity> =
            REGISTRATE.blockEntity<EnchantableHarvesterBlockEntity>("enchantable_harvester") { type, pos, state ->
                EnchantableHarvesterBlockEntity(type, pos, state)
            }
                .validBlocks(BlockRegistration.ENCHANTABLE_MECHANICAL_HARVESTER)
                .renderer { NonNullFunction(::EnchantableHarvesterRenderer) }
                .register()

        @JvmStatic
        val ENCHANTABLE_MECHANICAL_SAW: BlockEntityEntry<EnchantableSawBlockEntity> =
            REGISTRATE.blockEntity<EnchantableSawBlockEntity>("enchantable_saw") { type, pos, state ->
                EnchantableSawBlockEntity(type, pos, state)
            }
                .instance(renderNormally = true) { ::SawInstance }
                .validBlocks(BlockRegistration.ENCHANTABLE_MECHANICAL_SAW)
                .renderer { NonNullFunction(::EnchantableSawRenderer) }
                .register()

        @JvmStatic
        val ENCHANTABLE_MECHANICAL_PLOUGH: BlockEntityEntry<EnchantablePloughBlockEntity> =
            REGISTRATE.blockEntity<EnchantablePloughBlockEntity>("enchantable_plough") { type, pos, state ->
                EnchantablePloughBlockEntity(type, pos, state)
            }
                .validBlocks(BlockRegistration.ENCHANTABLE_MECHANICAL_PLOUGH)
                .renderer { NonNullFunction(::EnchantablePloughRenderer) }
                .register()

        @JvmStatic
        val ENCHANTABLE_ENCASED_FAN: BlockEntityEntry<EnchantableEncasedFanBlockEntity> =
            REGISTRATE.blockEntity<EnchantableEncasedFanBlockEntity>("enchantable_encased_fan") { type, pos, state ->
                EnchantableEncasedFanBlockEntity(type, pos, state)
            }
                .instance(renderNormally = true) { ::FanInstance }
                .validBlocks(BlockRegistration.ENCHANTABLE_ENCASED_FAN)
                .renderer { NonNullFunction(::EnchantableEncasedFanRenderer) }
                .register()

        @JvmStatic
        val ENCHANTABLE_MILLSTONE: BlockEntityEntry<EnchantableMillstoneBlockEntity> =
            REGISTRATE.blockEntity<EnchantableMillstoneBlockEntity>("enchantable_millstone") { type, pos, state ->
                EnchantableMillstoneBlockEntity(type, pos, state)
            }
                .instance(renderNormally = true) { ::MillstoneCogInstance }
                .validBlocks(BlockRegistration.ENCHANTABLE_MILLSTONE)
                .renderer { NonNullFunction(::EnchantableMillstoneRenderer) }
                .register()

        @JvmStatic
        val ENCHANTABLE_CRUSHING_WHEEL_CONTROLLER: BlockEntityEntry<EnchantableCrushingWheelControllerBlockEntity> =
            REGISTRATE.blockEntity<EnchantableCrushingWheelControllerBlockEntity>(
                "enchantable_crushing_wheel_controller"
            ) { type, pos, state ->
                EnchantableCrushingWheelControllerBlockEntity(type, pos, state)
            }
                .validBlocks(BlockRegistration.ENCHANTABLE_CRUSHING_WHEEL_CONTROLLER)
                .register()

        @JvmStatic
        val ENCHANTABLE_CRUSHING_WHEEL: BlockEntityEntry<EnchantableCrushingWheelBlockEntity> =
            REGISTRATE.blockEntity<EnchantableCrushingWheelBlockEntity>("enchantable_crushing_wheel") { type, pos, state ->
                EnchantableCrushingWheelBlockEntity(type, pos, state)
            }
                .instance(renderNormally = true) { ::CutoutRotatingInstance }
                .validBlocks(BlockRegistration.ENCHANTABLE_CRUSHING_WHEEL)
                .renderer { NonNullFunction(::EnchantableCrushingWheelRenderer) }
                .register()

        @JvmStatic
        val ENCHANTABLE_MECHANICAL_PRESS: BlockEntityEntry<EnchantableMechanicalPressBlockEntity> =
            REGISTRATE.blockEntity<EnchantableMechanicalPressBlockEntity>("enchantable_mechanical_press") { type, pos, state ->
                EnchantableMechanicalPressBlockEntity(type, pos, state)
            }
                .instance(renderNormally = true) { ::PressInstance }
                .validBlocks(BlockRegistration.ENCHANTABLE_MECHANICAL_PRESS)
                .renderer { NonNullFunction(::EnchantableMechanicalPressRenderer) }
                .register()

        @JvmStatic
        val ENCHANTABLE_MECHANICAL_MIXER: BlockEntityEntry<EnchantableMechanicalMixerBlockEntity> =
            REGISTRATE.blockEntity<EnchantableMechanicalMixerBlockEntity>("enchantable_mechanical_mixer") { type, pos, state ->
                EnchantableMechanicalMixerBlockEntity(type, pos, state)
            }
                .instance(renderNormally = true) { ::MixerInstance }
                .validBlocks(BlockRegistration.ENCHANTABLE_MECHANICAL_MIXER)
                .renderer { NonNullFunction(::EnchantableMechanicalMixerRenderer) }
                .register()

        @JvmStatic
        fun register() {
        }
    }
}

fun <T : BlockEntity, P> CreateBlockEntityBuilder<T, P>.instance(
    renderNormally: Boolean = false,
    instanceFactory: () -> ((MaterialManager, T) -> BlockEntityInstance<in T>),
): CreateBlockEntityBuilder<T, P> {
    return this.instance({
        BiFunction { materialManager: MaterialManager, be: T ->
            instanceFactory()(materialManager, be)
        }
    }, renderNormally)
}
