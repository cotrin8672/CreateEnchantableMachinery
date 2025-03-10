package io.github.cotrin8672.cem.content.block.press

import com.mojang.math.Axis
import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.kinetics.press.MechanicalPressBlock
import dev.engine_room.flywheel.api.instance.Instance
import dev.engine_room.flywheel.api.visual.DynamicVisual
import dev.engine_room.flywheel.api.visualization.VisualizationContext
import dev.engine_room.flywheel.lib.instance.InstanceTypes
import dev.engine_room.flywheel.lib.material.Materials
import dev.engine_room.flywheel.lib.model.Models
import dev.engine_room.flywheel.lib.model.baked.BakedModelBuilder
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual
import io.github.cotrin8672.cem.client.visual.EnchantedShaftVisual
import net.createmod.catnip.math.AngleHelper
import java.util.function.Consumer

class EnchantablePressVisual(
    context: VisualizationContext,
    blockEntity: EnchantableMechanicalPressBlockEntity,
    partialTick: Float,
) :
    EnchantedShaftVisual<EnchantableMechanicalPressBlockEntity>(context, blockEntity, partialTick),
    SimpleDynamicVisual {
    private val pressHead = instancerProvider().instancer(
        InstanceTypes.ORIENTED,
        Models.partial(AllPartialModels.MECHANICAL_PRESS_HEAD)
    ).createInstance()

    private val enchantedPressHead = instancerProvider().instancer(
        InstanceTypes.ORIENTED,
        BakedModelBuilder(AllPartialModels.MECHANICAL_PRESS_HEAD.get())
            .materialFunc { _, _ -> Materials.GLINT }
            .build()
    ).createInstance()

    init {
        val q = Axis.YP
            .rotationDegrees(AngleHelper.horizontalAngle(blockState.getValue(MechanicalPressBlock.HORIZONTAL_FACING)))

        pressHead.rotation(q)
        enchantedPressHead.rotation(q)

        transformModels(partialTick)
    }

    override fun beginFrame(ctx: DynamicVisual.Context) {
        transformModels(ctx.partialTick())
    }

    private fun transformModels(pt: Float) {
        val renderedHeadOffset = getRenderedHeadOffset(pt)

        pressHead.position(visualPosition)
            .translatePosition(0f, -renderedHeadOffset, 0f)
            .setChanged()

        enchantedPressHead.position(visualPosition)
            .translatePosition(0f, -renderedHeadOffset, 0f)
            .setChanged()
    }

    private fun getRenderedHeadOffset(pt: Float): Float {
        val pressingBehaviour = blockEntity!!.getPressingBehaviour()
        return (pressingBehaviour.getRenderedHeadOffset(pt) * pressingBehaviour.mode.headOffset)
    }

    override fun updateLight(partialTick: Float) {
        super.updateLight(partialTick)
        relight(pressHead)
        relight(enchantedPressHead)
    }

    override fun _delete() {
        super._delete()
        pressHead.delete()
        enchantedPressHead.delete()
    }

    override fun collectCrumblingInstances(consumer: Consumer<Instance?>) {
        super.collectCrumblingInstances(consumer)
        consumer.accept(pressHead)
        consumer.accept(enchantedPressHead)
    }
}
