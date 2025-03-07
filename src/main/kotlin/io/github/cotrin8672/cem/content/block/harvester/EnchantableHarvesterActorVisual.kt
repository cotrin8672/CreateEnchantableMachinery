package io.github.cotrin8672.cem.content.block.harvester

import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.render.ActorVisual
import dev.engine_room.flywheel.api.visualization.VisualizationContext
import dev.engine_room.flywheel.lib.instance.InstanceTypes
import dev.engine_room.flywheel.lib.material.Materials
import dev.engine_room.flywheel.lib.model.Models
import dev.engine_room.flywheel.lib.model.baked.BakedModelBuilder
import dev.engine_room.flywheel.lib.model.baked.PartialModel
import net.createmod.catnip.animation.AnimationTickHolder
import net.createmod.catnip.math.AngleHelper
import net.createmod.catnip.math.VecHelper
import net.minecraft.core.Direction
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.Vec3

class EnchantableHarvesterActorVisual(
    visualizationContext: VisualizationContext,
    world: BlockAndTintGetter,
    context: MovementContext,
) : ActorVisual(visualizationContext, world, context) {
    companion object {
        private const val ORIGIN_OFFSET = 1 / 16f
        private val rotOffset = Vec3(0.5, (-2 * ORIGIN_OFFSET + 0.5), (ORIGIN_OFFSET + 0.5))
    }

    private val state = context.state
    private val facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING)

    private val harvester = instancerProvider.instancer(
        InstanceTypes.TRANSFORMED,
        Models.partial(getRollingPartial())
    ).createInstance()
    private val enchantedHarvester = instancerProvider.instancer(
        InstanceTypes.TRANSFORMED,
        BakedModelBuilder(AllPartialModels.DRILL_HEAD.get()).materialFunc { _, _ -> Materials.GLINT }.build()
    ).createInstance()


    private val horizontalAngle = facing.toYRot() + (if (facing.axis === Direction.Axis.X) 180 else 0)
    private var rotation = 0.0
    private var previousRotation = 0.0

    init {
        harvester.light(localBlockLight(), 0).setChanged()
        enchantedHarvester.light(localBlockLight(), 0).setChanged()
    }

    private fun getRollingPartial(): PartialModel {
        return AllPartialModels.HARVESTER_BLADE
    }

    private fun getRotationOffset(): Vec3 {
        return rotOffset
    }

    private fun getRadius(): Double {
        return 6.5
    }

    override fun tick() {
        super.tick()

        previousRotation = rotation

        if (context.contraption.stalled || context.disabled
            || VecHelper.isVecPointingTowards(context.relativeMotion, facing.opposite)
        ) return

        val arcLength = context.motion.length()

        val radians = arcLength * 16 / getRadius()

        var deg = AngleHelper.deg(radians)

        deg = ((deg * 3000) / 3000)

        rotation += deg * 1.25

        rotation %= 360.0
    }

    override fun beginFrame() {
        enchantedHarvester.setIdentityTransform()
            .translate(context.localPos)
            .center()
            .rotateYDegrees(horizontalAngle)
            .uncenter()
            .translate(getRotationOffset())
            .rotateXDegrees(getRotation().toFloat())
            .translateBack(getRotationOffset())
            .setChanged()

        harvester.setIdentityTransform()
            .translate(context.localPos)
            .center()
            .rotateYDegrees(horizontalAngle)
            .uncenter()
            .translate(getRotationOffset())
            .rotateXDegrees(getRotation().toFloat())
            .translateBack(getRotationOffset())
            .setChanged()
    }

    override fun _delete() {
        harvester.delete()
        enchantedHarvester.delete()
    }

    private fun getRotation(): Double {
        return AngleHelper.angleLerp(AnimationTickHolder.getPartialTicks().toDouble(), previousRotation, rotation)
            .toDouble()
    }
}
