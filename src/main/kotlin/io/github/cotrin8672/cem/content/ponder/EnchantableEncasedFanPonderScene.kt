package io.github.cotrin8672.cem.content.ponder

import com.simibubi.create.AllItems
import com.simibubi.create.foundation.ponder.CreateSceneBuilder
import net.createmod.catnip.math.Pointing
import net.createmod.ponder.api.PonderPalette
import net.createmod.ponder.api.scene.SceneBuilder
import net.createmod.ponder.api.scene.SceneBuildingUtil
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.phys.Vec3

object EnchantableEncasedFanPonderScene {
    fun enchanting(builder: SceneBuilder, util: SceneBuildingUtil) {
        val largeCogWheelPos = util.grid().at(2, 0, 5)
        val rightCogWheelPos = util.grid().at(1, 1, 5)
        val leftCogWheelPos = util.grid().at(3, 1, 5)
        val enchantedFanPos = util.grid().at(1, 1, 4)
        val normalFanPos = util.grid().at(3, 1, 4)
        val rightWaterPos = util.grid().at(1, 1, 3)
        val leftWaterPos = util.grid().at(3, 1, 3)

        with(CreateSceneBuilder(builder)) {
            title("encased_fan", "Enchanting to Encased Fan")
            configureBasePlate(0, 0, 5)

            world().setKineticSpeed(util.select().position(largeCogWheelPos), 8f)
            world().setKineticSpeed(util.select().fromTo(rightCogWheelPos, normalFanPos), -16f)
            world().setKineticSpeed(util.select().fromTo(leftCogWheelPos, enchantedFanPos), -16f)

            world().showSection(util.select().layer(0), Direction.UP)
            idle(5)
            world().showSection(util.select().fromTo(enchantedFanPos, leftCogWheelPos), Direction.DOWN)
            idle(10)
            world().showSection(util.select().position(rightWaterPos), Direction.UP)
            world().showSection(util.select().position(leftWaterPos), Direction.UP)
            idle(20)

            overlay().showText(50)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().topOf(enchantedFanPos))
                .text("Encased Fans can be given an efficiency-enhancing enchantment.")
            idle(60)

            val sand = ItemStack(Items.SAND)
            val clay = ItemStack(Items.CLAY)
            val enchantedItem = world().createItemEntity(
                util.vector().topOf(rightWaterPos.north(2)),
                util.vector().of(0.0, 0.2, 0.0),
                sand
            )
            val normalItem = world().createItemEntity(
                util.vector().topOf(leftWaterPos.north(2)),
                util.vector().of(0.0, 0.2, 0.0),
                sand
            )

            idle(10)

            effects().emitParticles(
                util.vector().centerOf(rightWaterPos.north(2)),
                effects().simpleParticleEmitter(ParticleTypes.SPIT, Vec3.ZERO),
                1f,
                40
            )

            effects().emitParticles(
                util.vector().centerOf(leftWaterPos.north(2)),
                effects().simpleParticleEmitter(ParticleTypes.SPIT, Vec3.ZERO),
                1f,
                80
            )

            idle(40)

            world().modifyEntity(enchantedItem) {
                (it as ItemEntity).item = clay
            }

            idle(40)

            world().modifyEntity(normalItem) {
                (it as ItemEntity).item = clay
            }

            overlay().showText(50)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().centerOf(1, 1, 1))
                .text("Encased Fans can be given an efficiency-enhancing enchantment.")
            idle(70)

            overlay().showControls(util.vector().topOf(enchantedFanPos), Pointing.DOWN, 50)
                .withItem(AllItems.GOGGLES.asStack())
            idle(7)
            overlay().showText(50)
                .text("When wearing Engineers' Goggles, the player can get drill's enchantments")
                .attachKeyFrame()
                .colored(PonderPalette.MEDIUM)
                .pointAt(util.vector().topOf(enchantedFanPos))
                .placeNearTarget()

            idle(70)
        }
    }
}
