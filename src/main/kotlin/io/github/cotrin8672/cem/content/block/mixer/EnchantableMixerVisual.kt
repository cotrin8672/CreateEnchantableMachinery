package io.github.cotrin8672.cem.content.block.mixer

import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.kinetics.base.RotatingInstance
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual
import com.simibubi.create.foundation.render.AllInstanceTypes
import dev.engine_room.flywheel.api.instance.Instance
import dev.engine_room.flywheel.api.visual.DynamicVisual
import dev.engine_room.flywheel.api.visualization.VisualizationContext
import dev.engine_room.flywheel.lib.instance.InstanceTypes
import dev.engine_room.flywheel.lib.material.Materials
import dev.engine_room.flywheel.lib.model.Models
import dev.engine_room.flywheel.lib.model.baked.BakedModelBuilder
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual
import net.minecraft.core.Direction
import java.util.function.Consumer

class EnchantableMixerVisual(
    context: VisualizationContext,
    blockEntity: EnchantableMechanicalMixerBlockEntity,
    partialTick: Float,
) : SingleAxisRotatingVisual<EnchantableMechanicalMixerBlockEntity>(
    context,
    blockEntity,
    partialTick,
    Models.partial(AllPartialModels.SHAFTLESS_COGWHEEL)
), SimpleDynamicVisual {
    private val enchantedCogwheel = instancerProvider().instancer(
        AllInstanceTypes.ROTATING,
        BakedModelBuilder(AllPartialModels.SHAFTLESS_COGWHEEL.get())
            .materialFunc { _, _ -> Materials.GLINT }
            .build()
    ).createInstance()
        .rotateToFace(Direction.UP, rotationAxis())
        .setup(blockEntity)
        .setPosition(visualPosition)

    private val mixerHead = instancerProvider().instancer(
        AllInstanceTypes.ROTATING,
        Models.partial(AllPartialModels.MECHANICAL_MIXER_HEAD)
    ).createInstance()

    private val mixerPole = instancerProvider().instancer(
        InstanceTypes.ORIENTED,
        Models.partial(AllPartialModels.MECHANICAL_MIXER_POLE)
    ).createInstance()

    private val enchantedMixerPole = instancerProvider().instancer(
        InstanceTypes.ORIENTED,
        BakedModelBuilder(AllPartialModels.MECHANICAL_MIXER_POLE.get())
            .materialFunc { _, _ -> Materials.GLINT }
            .build()
    ).createInstance()

    init {
        mixerHead.setRotationAxis(Direction.Axis.Y)

        enchantedCogwheel.setChanged()
        animate(partialTick)
    }

    override fun beginFrame(ctx: DynamicVisual.Context) {
        animate(ctx.partialTick())
    }

    override fun update(pt: Float) {
        super.update(pt)
        enchantedCogwheel.setup(blockEntity).setChanged()
    }

    private fun animate(pt: Float) {
        val renderedHeadOffset: Float = blockEntity.getRenderedHeadOffset(pt)

        transformPole(renderedHeadOffset)
        transformHead(renderedHeadOffset, pt)
    }

    private fun transformHead(renderedHeadOffset: Float, pt: Float) {
        val speed: Float = blockEntity.getRenderedHeadRotationSpeed(pt)

        mixerHead.setPosition(visualPosition)
            .nudge(0f, -renderedHeadOffset, 0f)
            .setRotationalSpeed(speed * 2 * RotatingInstance.SPEED_MULTIPLIER)
            .setChanged()
    }

    private fun transformPole(renderedHeadOffset: Float) {
        mixerPole
            .position(visualPosition)
            .translatePosition(0f, -renderedHeadOffset, 0f)
            .setChanged()

        enchantedMixerPole
            .position(visualPosition)
            .translatePosition(0f, -renderedHeadOffset, 0f)
            .setChanged()
    }

    override fun updateLight(partialTick: Float) {
        super.updateLight(partialTick)
        relight(enchantedCogwheel)

        relight(pos.below(), mixerHead)
        relight(mixerPole)
        relight(enchantedMixerPole)
    }

    override fun _delete() {
        super._delete()
        enchantedCogwheel.delete()
        mixerHead.delete()
        mixerPole.delete()
        enchantedMixerPole.delete()
    }

    override fun collectCrumblingInstances(consumer: Consumer<Instance?>) {
        super.collectCrumblingInstances(consumer)
        consumer.accept(enchantedCogwheel)
        consumer.accept(mixerHead)
        consumer.accept(mixerPole)
        consumer.accept(enchantedMixerPole)
    }
}
