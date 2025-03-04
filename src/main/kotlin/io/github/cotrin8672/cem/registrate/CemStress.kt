package io.github.cotrin8672.cem.registrate

import com.tterrag.registrate.builders.BlockBuilder
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator
import io.github.cotrin8672.cem.CreateEnchantableMachinery
import it.unimi.dsi.fastutil.objects.Object2DoubleMap
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap
import net.createmod.catnip.config.ConfigBase
import net.createmod.catnip.registry.RegisteredObjectsHelper
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.common.ModConfigSpec
import java.util.function.DoubleSupplier

class CemStress : ConfigBase() {
    private val capacities: MutableMap<ResourceLocation, ModConfigSpec.ConfigValue<Double>> = HashMap()
    private val impacts: MutableMap<ResourceLocation, ModConfigSpec.ConfigValue<Double>> = HashMap()

    override fun registerAll(builder: ModConfigSpec.Builder) {
        builder.comment(".", Comments.su, Comments.impact).push("impact")
        DEFAULT_IMPACTS.forEach { (id: ResourceLocation, value: Double) ->
            impacts[id] = builder.define(id.path, value)
        }
        builder.pop()

        builder.comment(".", Comments.su, Comments.capacity).push("capacity")
        DEFAULT_CAPACITIES.forEach { (id: ResourceLocation, value: Double) ->
            capacities[id] = builder.define(id.path, value)
        }
        builder.pop()
    }

    override fun getName(): String {
        return "stressValues.v$VERSION"
    }

    fun getImpact(block: Block): DoubleSupplier? {
        val id = RegisteredObjectsHelper.getKeyOrThrow(block)
        val value = impacts[id]
        return if (value == null) null else DoubleSupplier { value.get() }
    }

    fun getCapacity(block: Block): DoubleSupplier? {
        val id = RegisteredObjectsHelper.getKeyOrThrow(block)
        val value = capacities[id]
        return if (value == null) null else DoubleSupplier { value.get() }
    }

    private object Comments {
        var su: String = "[in Stress Units]"
        var impact: String =
            "Configure the individual stress impact of mechanical blocks. Note that this cost is doubled for every speed increase it receives."
        var capacity: String = "Configure how much stress a source can accommodate for."
    }

    companion object {
        // bump this version to reset configured values.
        private const val VERSION = 2

        // IDs need to be used since configs load before registration
        private val DEFAULT_IMPACTS: Object2DoubleMap<ResourceLocation> = Object2DoubleOpenHashMap()
        private val DEFAULT_CAPACITIES: Object2DoubleMap<ResourceLocation> = Object2DoubleOpenHashMap()

        fun <B : Block, P> setNoImpact(): NonNullUnaryOperator<BlockBuilder<B, P>> {
            return setImpact(0.0)
        }

        fun <B : Block, P> setImpact(value: Double): NonNullUnaryOperator<BlockBuilder<B, P>> {
            return NonNullUnaryOperator { builder: BlockBuilder<B, P> ->
                assertFromCreateEnchantableMachinery(builder)
                val id = CreateEnchantableMachinery.asResource(builder.name)
                DEFAULT_IMPACTS.put(id, value)
                builder
            }
        }

        fun <B : Block, P> setCapacity(value: Double): NonNullUnaryOperator<BlockBuilder<B, P>> {
            return NonNullUnaryOperator<BlockBuilder<B, P>> { builder: BlockBuilder<B, P> ->
                assertFromCreateEnchantableMachinery(builder)
                val id: ResourceLocation = CreateEnchantableMachinery.asResource(builder.name)
                DEFAULT_CAPACITIES.put(id, value)
                builder
            }
        }

        private fun assertFromCreateEnchantableMachinery(builder: BlockBuilder<*, *>) {
            check(builder.owner.modid == CreateEnchantableMachinery.MOD_ID) {
                "Non-Create: Enchantable Machinery blocks cannot be added to Create: Enchantable Machinery's config."
            }
        }
    }
}
