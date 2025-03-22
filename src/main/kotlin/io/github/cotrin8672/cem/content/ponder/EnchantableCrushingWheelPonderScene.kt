package io.github.cotrin8672.cem.content.ponder

import com.simibubi.create.AllItems
import com.simibubi.create.foundation.ponder.CreateSceneBuilder
import net.createmod.catnip.math.Pointing
import net.createmod.ponder.api.ParticleEmitter
import net.createmod.ponder.api.PonderPalette
import net.createmod.ponder.api.scene.SceneBuilder
import net.createmod.ponder.api.scene.SceneBuildingUtil
import net.minecraft.core.Direction
import net.minecraft.core.particles.ItemParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

object EnchantableCrushingWheelPonderScene {
    fun enchanting(builder: SceneBuilder, util: SceneBuildingUtil) {
        val rightCrushingWheel = util.grid().at(1, 2, 2)
        val leftCrushingWheel = util.grid().at(3, 2, 2)
        val controller = util.grid().at(2, 2, 2)
        val rightGearbox = util.grid().at(1, 2, 3)
        val leftGearbox = util.grid().at(3, 2, 3)
        val shaft1 = util.grid().at(2, 2, 3)
        val shaft2 = util.grid().at(3, 2, 4)
        val topCogwheel = util.grid().at(3, 2, 5)
        val underCogwheel = util.grid().at(3, 1, 5)
        val largeCogwheel = util.grid().at(2, 0, 5)

        with(CreateSceneBuilder(builder)) {
            title("crushing_wheel", "Enchanting to Crushing Wheel")
            configureBasePlate(0, 0, 5)

            world().setKineticSpeed(util.select().position(largeCogwheel), -8f)
            world().setKineticSpeed(util.select().position(underCogwheel), 16f)
            world().setKineticSpeed(util.select().position(topCogwheel), -16f)
            world().setKineticSpeed(util.select().position(shaft2), -16f)
            world().setKineticSpeed(util.select().position(shaft1), -16f)
            world().setKineticSpeed(util.select().position(rightGearbox), 16f)
            world().setKineticSpeed(util.select().position(leftGearbox), -16f)
            world().setKineticSpeed(util.select().position(rightCrushingWheel), -16f)
            world().setKineticSpeed(util.select().position(leftCrushingWheel), 16f)

            world().showSection(util.select().layer(0), Direction.UP)
            idle(5)
            world().showSection(util.select().fromTo(topCogwheel, underCogwheel), Direction.DOWN)
            idle(10)
            world().showSection(util.select().fromTo(shaft2, rightCrushingWheel), Direction.SOUTH)
            idle(20)

            overlay().showText(50)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().topOf(rightCrushingWheel))
                .text("Encased Fans can be given an efficiency-enhancing enchantment.")
            idle(60)

            val input = ItemStack(Items.GOLD_ORE)
            val output = ItemStack(Items.RAW_GOLD)
            val itemEntity =
                world().createItemEntity(util.vector().topOf(controller), util.vector().of(0.0, 0.2, 0.0), input)
            idle(10)
            world().modifyEntity(itemEntity, Entity::discard)
            val blockSpace: ParticleEmitter = effects().particleEmitterWithinBlockSpace(
                ItemParticleOption(ParticleTypes.ITEM, input),
                util.vector().of(0.0, 0.0, 0.0)
            )
            effects().emitParticles(util.vector().centerOf(controller).add(0.0, -0.2, 0.0), blockSpace, 3f, 20)
            idle(30)

            world().createItemEntity(
                util.vector().topOf(controller).add(0.0, -1.4, 0.0),
                util.vector().of(0.0, 0.0, 0.0),
                output
            )

            idle(10)

            world().createItemEntity(
                util.vector().topOf(controller).add(0.0, -1.4, 0.0),
                util.vector().of(0.0, 0.0, 0.0),
                output
            )

            idle(20)

            overlay().showText(50)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().centerOf(controller.below()))
                .text("Encased Fans can be given an efficiency-enhancing enchantment.")
            idle(70)

            overlay().showText(50)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().centerOf(controller))
                .text("Encased Fans can be given an efficiency-enhancing enchantment.")
            idle(70)

            overlay().showControls(util.vector().topOf(rightCrushingWheel), Pointing.DOWN, 50)
                .withItem(AllItems.GOGGLES.asStack())
            idle(7)
            overlay().showText(50)
                .text("When wearing Engineers' Goggles, the player can get drill's enchantments")
                .attachKeyFrame()
                .colored(PonderPalette.MEDIUM)
                .pointAt(util.vector().topOf(rightCrushingWheel))
                .placeNearTarget()

            idle(70)
        }
    }
}
