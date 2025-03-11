package io.github.cotrin8672.cem.content.block.roller

import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterActorVisual
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld
import dev.engine_room.flywheel.api.visualization.VisualizationContext
import dev.engine_room.flywheel.lib.instance.InstanceTypes
import dev.engine_room.flywheel.lib.material.Materials
import dev.engine_room.flywheel.lib.model.Models
import dev.engine_room.flywheel.lib.model.baked.BakedModelBuilder
import dev.engine_room.flywheel.lib.model.baked.PartialModel
import net.minecraft.world.phys.Vec3

class EnchantableRollerActorVisual(
    visualizationContext: VisualizationContext,
    simulationWorld: VirtualRenderWorld,
    movementContext: MovementContext,
) : HarvesterActorVisual(visualizationContext, simulationWorld, movementContext) {
    private val enchantedWheel = instancerProvider.instancer(
        InstanceTypes.TRANSFORMED,
        BakedModelBuilder(rollingPartial.get())
            .materialFunc { _, _ -> Materials.GLINT }
            .build()
    ).createInstance().apply {
        light(localBlockLight(), 0)
        setChanged()
    }

    private val frame = instancerProvider.instancer(
        InstanceTypes.TRANSFORMED,
        Models.partial(AllPartialModels.ROLLER_FRAME)
    ).createInstance().apply {
        light(localBlockLight(), 0)
    }

    private val enchantedFrame = instancerProvider.instancer(
        InstanceTypes.TRANSFORMED,
        BakedModelBuilder(AllPartialModels.ROLLER_FRAME.get())
            .materialFunc { _, _ -> Materials.GLINT }
            .build()
    ).createInstance().apply {
        light(localBlockLight(), 0)
    }

    override fun beginFrame() {
        harvester.setIdentityTransform()
            .translate(context.localPos)
            .center()
            .rotateYDegrees(horizontalAngle)
            .uncenter()
            .translate(0.0, -.25, (17 / 16f).toDouble())
            .rotateXDegrees(rotation.toFloat())
            .translate(0.0, -.5, .5)
            .rotateYDegrees(90f)
            .setChanged()

        enchantedWheel.setIdentityTransform()
            .translate(context.localPos)
            .center()
            .rotateYDegrees(horizontalAngle)
            .uncenter()
            .translate(0.0, -.25, (17 / 16f).toDouble())
            .rotateXDegrees(rotation.toFloat())
            .translate(0.0, -.5, .5)
            .rotateYDegrees(90f)
            .setChanged()

        frame.setIdentityTransform()
            .translate(context.localPos)
            .center()
            .rotateYDegrees(horizontalAngle + 180)
            .uncenter()
            .setChanged()

        enchantedFrame.setIdentityTransform()
            .translate(context.localPos)
            .center()
            .rotateYDegrees(horizontalAngle + 180)
            .uncenter()
            .setChanged()
    }

    override fun getRollingPartial(): PartialModel {
        return AllPartialModels.ROLLER_WHEEL
    }

    override fun getRotationOffset(): Vec3 {
        return Vec3.ZERO
    }

    override fun getRadius(): Double {
        return 16.5
    }

    override fun _delete() {
        super._delete()
        enchantedWheel.delete()
        frame.delete()
        enchantedFrame.delete()
    }
}
