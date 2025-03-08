package io.github.cotrin8672.cem.content.block.fan

import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual
import com.simibubi.create.foundation.render.AllInstanceTypes
import dev.engine_room.flywheel.api.instance.Instance
import dev.engine_room.flywheel.api.material.CardinalLightingMode
import dev.engine_room.flywheel.api.visualization.VisualizationContext
import dev.engine_room.flywheel.lib.instance.InstanceTypes
import dev.engine_room.flywheel.lib.material.Materials
import dev.engine_room.flywheel.lib.material.SimpleMaterial
import dev.engine_room.flywheel.lib.model.Models
import dev.engine_room.flywheel.lib.model.baked.BakedModelBuilder
import dev.engine_room.flywheel.lib.model.baked.BlockModelBuilder
import io.github.cotrin8672.cem.util.nonNullLevel
import net.minecraft.core.Direction
import net.minecraft.util.Mth
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import java.util.function.Consumer

class EnchantableFanVisual(
    context: VisualizationContext,
    blockEntity: EnchantableEncasedFanBlockEntity,
    partialTick: Float,
) : KineticBlockEntityVisual<EnchantableEncasedFanBlockEntity>(context, blockEntity, partialTick) {
    private val shaft = instancerProvider().instancer(
        AllInstanceTypes.ROTATING,
        Models.partial(AllPartialModels.SHAFT_HALF)
    )
        .createInstance()
    private val enchantedShaft = instancerProvider().instancer(
        AllInstanceTypes.ROTATING,
        BakedModelBuilder(AllPartialModels.SHAFT_HALF.get()).materialFunc { _, _ -> Materials.GLINT }.build()
    )
        .createInstance()
    private val fan = instancerProvider().instancer(
        AllInstanceTypes.ROTATING,
        Models.partial(AllPartialModels.ENCASED_FAN_INNER)
    )
        .createInstance()

    private val blockModel = context.createEmbedding(renderOrigin()).instancerProvider().instancer(
        InstanceTypes.POSED,
        BlockModelBuilder(blockEntity.nonNullLevel, setOf(blockEntity.blockPos))
            .materialFunc { _, _ ->
                SimpleMaterial.builderOf(Materials.GLINT)
                    .cardinalLightingMode(CardinalLightingMode.CHUNK)
                    .polygonOffset(true)
            }
            .build()
    )
        .createInstance()
    val direction: Direction = blockState.getValue(BlockStateProperties.FACING)
    private val opposite = direction.opposite

    init {
        shaft.setup(blockEntity)
            .setPosition(visualPosition)
            .rotateToFace(Direction.SOUTH, opposite)
            .setChanged()

        enchantedShaft.setup(blockEntity)
            .setPosition(visualPosition)
            .rotateToFace(Direction.SOUTH, opposite)
            .setChanged()

        fan.setup(blockEntity, getFanSpeed())
            .setPosition(visualPosition)
            .rotateToFace(Direction.SOUTH, opposite)
            .setChanged()

        blockModel
            .setChanged()
    }

    private fun getFanSpeed(): Float {
        val speed = blockEntity.speed * 5
        return when {
            speed > 0f -> Mth.clamp(speed, 80f, (64 * 20f))
            speed < 0f -> Mth.clamp(speed, (-64 * 20f), -80f)
            else -> 0f
        }
    }

    override fun update(pt: Float) {
        shaft.setup(blockEntity).setChanged()
        enchantedShaft.setup(blockEntity).setChanged()
        fan.setup(blockEntity, getFanSpeed()).setChanged()
        blockModel.setChanged()
    }

    override fun updateLight(partialTick: Float) {
        val behind = pos.relative(opposite)
        relight(behind, shaft)
        relight(behind, enchantedShaft)

        val inFront = pos.relative(direction)
        relight(inFront, fan)
        relight(blockModel)
    }

    override fun _delete() {
        shaft.delete()
        enchantedShaft.delete()
        fan.delete()
        blockModel.delete()
    }

    override fun collectCrumblingInstances(consumer: Consumer<Instance?>) {
        consumer.accept(shaft)
        consumer.accept(enchantedShaft)
        consumer.accept(fan)
        consumer.accept(blockModel)
    }
}
