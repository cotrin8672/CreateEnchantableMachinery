package io.github.cotrin8672.registrate

import com.jozufozu.flywheel.api.MaterialManager
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance
import com.simibubi.create.content.kinetics.base.CutoutRotatingInstance
import com.simibubi.create.content.kinetics.drill.DrillInstance
import com.simibubi.create.content.kinetics.fan.FanInstance
import com.simibubi.create.content.kinetics.millstone.MillstoneCogInstance
import com.simibubi.create.content.kinetics.saw.SawInstance
import com.simibubi.create.foundation.data.CreateBlockEntityBuilder
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
import io.github.cotrin8672.content.block.plough.EnchantablePloughBlockEntity
import io.github.cotrin8672.content.block.plough.EnchantablePloughRenderer
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

    val ENCHANTABLE_MECHANICAL_PLOUGH: BlockEntityEntry<EnchantablePloughBlockEntity> =
        REGISTRATE.blockEntity<EnchantablePloughBlockEntity>("enchantable_plough") { type, pos, state ->
            EnchantablePloughBlockEntity(type, pos, state)
        }
            .validBlocks(BlockRegistration.ENCHANTABLE_MECHANICAL_PLOUGH)
            .renderer { NonNullFunction(::EnchantablePloughRenderer) }
            .register()

    val ENCHANTABLE_ENCASED_FAN: BlockEntityEntry<EnchantableEncasedFanBlockEntity> =
        REGISTRATE.blockEntity<EnchantableEncasedFanBlockEntity>("enchantable_encase_fan") { type, pos, state ->
            EnchantableEncasedFanBlockEntity(type, pos, state)
        }
            .instance(renderNormally = true) { ::FanInstance }
            .validBlocks(BlockRegistration.ENCHANTABLE_ENCASED_FAN)
            .renderer { NonNullFunction(::EnchantableEncasedFanRenderer) }
            .register()

    val ENCHANTABLE_MILLSTONE: BlockEntityEntry<EnchantableMillstoneBlockEntity> =
        REGISTRATE.blockEntity<EnchantableMillstoneBlockEntity>("enchantable_millstone") { type, pos, state ->
            EnchantableMillstoneBlockEntity(type, pos, state)
        }
            .instance(renderNormally = true) { ::MillstoneCogInstance }
            .validBlocks(BlockRegistration.ENCHANTABLE_MILLSTONE)
            .renderer { NonNullFunction(::EnchantableMillstoneRenderer) }
            .register()

    val ENCHANTABLE_CRUSHING_WHEEL_CONTROLLER: BlockEntityEntry<EnchantableCrushingWheelControllerBlockEntity> =
        REGISTRATE.blockEntity<EnchantableCrushingWheelControllerBlockEntity>(
            "enchantable_crushing_wheel_controller"
        ) { type, pos, state ->
            EnchantableCrushingWheelControllerBlockEntity(type, pos, state)
        }
            .validBlocks(BlockRegistration.ENCHANTABLE_CRUSHING_WHEEL_CONTROLLER)
            .register()

    val ENCHANTABLE_CRUSHING_WHEEL: BlockEntityEntry<EnchantableCrushingWheelBlockEntity> =
        REGISTRATE.blockEntity<EnchantableCrushingWheelBlockEntity>("enchantable_crushing_wheel") { type, pos, state ->
            EnchantableCrushingWheelBlockEntity(type, pos, state)
        }
            .instance(renderNormally = true) { ::CutoutRotatingInstance }
            .validBlocks(BlockRegistration.ENCHANTABLE_CRUSHING_WHEEL)
            .renderer { NonNullFunction(::EnchantableCrushingWheelRenderer) }
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
