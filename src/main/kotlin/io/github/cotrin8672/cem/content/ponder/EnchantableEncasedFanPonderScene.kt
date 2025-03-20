package io.github.cotrin8672.cem.content.ponder

import com.simibubi.create.foundation.ponder.CreateSceneBuilder
import net.createmod.ponder.api.scene.SceneBuilder
import net.createmod.ponder.api.scene.SceneBuildingUtil
import net.minecraft.core.Direction
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

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
            val clay = ItemStack(Items.SAND)
            world().createItemEntity(
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

            idle(15)

            world().modifyEntities(ItemEntity::class.java) {
                it.item = clay
            }

            idle(15)

            world().modifyEntity(normalItem) {
                (it as ItemEntity).item = clay
            }

            overlay().showText(50)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().centerOf(1, 1, 1))
                .text("Encased Fans can be given an efficiency-enhancing enchantment.")
            idle(60)
        }
    }
}
