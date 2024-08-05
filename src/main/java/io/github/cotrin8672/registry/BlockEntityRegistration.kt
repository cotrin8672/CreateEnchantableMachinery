package io.github.cotrin8672.registry

import com.jozufozu.flywheel.api.MaterialManager
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance
import com.simibubi.create.content.kinetics.drill.DrillInstance
import com.simibubi.create.content.kinetics.drill.DrillRenderer
import com.simibubi.create.content.kinetics.saw.SawInstance
import com.simibubi.create.foundation.data.CreateBlockEntityBuilder
import com.tterrag.registrate.util.entry.BlockEntityEntry
import com.tterrag.registrate.util.nullness.NonNullFunction
import io.github.cotrin8672.CreateEnchantableMachinery.Companion.REGISTRATE
import io.github.cotrin8672.blockentity.EnchantableDrillBlockEntity
import io.github.cotrin8672.blockentity.EnchantableSawBlockEntity
import io.github.cotrin8672.renderer.EnchantableSawRenderer
import net.minecraft.world.level.block.entity.BlockEntity
import java.util.function.BiFunction

@Suppress("unused")
class BlockEntityRegistration {
    companion object {
        @JvmStatic
        val ENCHANTABLE_MECHANICAL_DRILL: BlockEntityEntry<EnchantableDrillBlockEntity> =
            REGISTRATE.blockEntity<EnchantableDrillBlockEntity>("enchantable_mechanical_drill") { type, pos, state ->
                EnchantableDrillBlockEntity(type, pos, state)
            }
                .instance(renderNormally = false) {
                    ::DrillInstance
                }
                .validBlocks(BlockRegistration.ENCHANTABLE_MECHANICAL_DRILL)
                .renderer {
                    NonNullFunction(::DrillRenderer)
                }
                .register()

        @JvmStatic
        val ENCHANTABLE_MECHANICAL_SAW: BlockEntityEntry<EnchantableSawBlockEntity> =
            REGISTRATE.blockEntity<EnchantableSawBlockEntity>("enchantable_mechanical_saw") { type, pos, state ->
                EnchantableSawBlockEntity(type, pos, state)
            }
                .instance(renderNormally = false) {
                    ::SawInstance
                }
                .validBlocks(BlockRegistration.ENCHANTABLE_MECHANICAL_SAW)
                .renderer {
                    NonNullFunction(::EnchantableSawRenderer)
                }
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
