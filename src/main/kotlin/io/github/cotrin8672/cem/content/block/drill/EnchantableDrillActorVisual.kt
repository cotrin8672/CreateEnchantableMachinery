package io.github.cotrin8672.cem.content.block.drill

import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.render.ActorVisual
import com.simibubi.create.content.kinetics.drill.DrillBlock
import dev.engine_room.flywheel.api.visualization.VisualizationContext
import dev.engine_room.flywheel.lib.instance.InstanceTypes
import dev.engine_room.flywheel.lib.material.Materials
import dev.engine_room.flywheel.lib.model.Models
import dev.engine_room.flywheel.lib.model.baked.BakedModelBuilder
import io.github.cotrin8672.cem.registry.PartialModelRegistration
import net.createmod.catnip.animation.AnimationTickHolder
import net.createmod.catnip.math.AngleHelper
import net.createmod.catnip.math.VecHelper
import net.minecraft.world.level.BlockAndTintGetter

class EnchantableDrillActorVisual(
    visualizationContext: VisualizationContext,
    world: BlockAndTintGetter,
    context: MovementContext,
) : ActorVisual(visualizationContext, world, context) {
    private val drillHead =
        instancerProvider.instancer(InstanceTypes.TRANSFORMED, Models.partial(AllPartialModels.DRILL_HEAD))
            .createInstance()
    private val enchantedDrillHead =
        instancerProvider.instancer(
            InstanceTypes.TRANSFORMED,
            BakedModelBuilder(PartialModelRegistration.ENCHANTABLE_HARVESTER_BLADE.get()).materialFunc { _, _ -> Materials.GLINT }
                .build()
        ).createInstance()
    private val facing = context.state.getValue(DrillBlock.FACING)

    private var rotation = 0.0
    private var previousRotation = 0.0

    override fun tick() {
        previousRotation = rotation

        if (context.disabled || VecHelper.isVecPointingTowards(context.relativeMotion, facing.opposite)) return

        val deg = context.animationSpeed

        rotation += deg / 20.0

        rotation %= 360.0
    }

    override fun beginFrame() {
        drillHead.setIdentityTransform()
            .translate(context.localPos)
            .center()
            .rotateToFace(facing.opposite)
            .rotateZDegrees(getRotation().toFloat())
            .uncenter()
            .setChanged()

        enchantedDrillHead.setIdentityTransform()
            .translate(context.localPos)
            .center()
            .rotateToFace(facing.opposite)
            .rotateZDegrees(getRotation().toFloat())
            .uncenter()
            .setChanged()
    }

    private fun getRotation(): Double {
        return AngleHelper.angleLerp(AnimationTickHolder.getPartialTicks().toDouble(), previousRotation, rotation)
            .toDouble()
    }

    override fun _delete() {
        drillHead.delete()
        enchantedDrillHead.delete()
    }
}
