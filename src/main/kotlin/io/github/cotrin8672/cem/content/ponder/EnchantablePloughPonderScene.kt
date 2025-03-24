package io.github.cotrin8672.cem.content.ponder

import com.simibubi.create.AllItems
import com.simibubi.create.foundation.ponder.CreateSceneBuilder
import net.createmod.catnip.math.Pointing
import net.createmod.ponder.api.PonderPalette
import net.createmod.ponder.api.scene.SceneBuilder
import net.createmod.ponder.api.scene.SceneBuildingUtil
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

object EnchantablePloughPonderScene {
    fun enchanting(builder: SceneBuilder, util: SceneBuildingUtil) {
        val largeCogWheelPos = util.grid().at(3, 0, 5)
        val cogWheelPos = util.grid().at(4, 1, 5)
        val gantryShaftSection = util.select().fromTo(4, 1, 4, 4, 1, 3)
        val gantryCarriagePos = util.grid().at(3, 1, 4)
        val ploughPos = util.grid().at(2, 1, 3)
        val enchantedPloughPos = util.grid().at(1, 1, 3)
        val rightRail = util.grid().at(1, 1, 2)
        val leftRail = util.grid().at(2, 1, 2)
        val railSection = util.select().fromTo(rightRail, leftRail)

        with(CreateSceneBuilder(builder)) {
            title("mechanical_plough", "Enchanting to Mechanical Plough")
            configureBasePlate(0, 0, 5)

            world().showSection(util.select().layer(0), Direction.UP)
            idle(5)
            world().showSection(util.select().position(cogWheelPos), Direction.DOWN)
            idle(10)
            world().showSection(gantryShaftSection, Direction.SOUTH)
            idle(10)
            val gantrySection = world().showIndependentSection(
                util.select().fromTo(gantryCarriagePos, enchantedPloughPos),
                Direction.EAST
            )
            idle(20)

            overlay().showText(50)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().topOf(enchantedPloughPos))
                .text("Like other tools, the Mechanical Plough can be enchanted")
            idle(60)

            world().showSection(railSection, Direction.DOWN)

            idle(20)

            world().setKineticSpeed(util.select().position(largeCogWheelPos), 8f)
            world().setKineticSpeed(util.select().position(cogWheelPos), -16f)
            world().setKineticSpeed(gantryShaftSection, -16f)
            world().moveSection(gantrySection, util.vector().of(0.0, 0.0, -0.1), 2)

            val stack = ItemStack(Items.RAIL)
            for (i in 0..9) {
                idle(5)
                world().incrementBlockBreakingProgress(rightRail)
                idle(5)
                world().incrementBlockBreakingProgress(rightRail)
                world().incrementBlockBreakingProgress(leftRail)

                when (i) {
                    1 -> {
                        overlay().showText(80)
                            .attachKeyFrame()
                            .placeNearTarget()
                            .pointAt(util.vector().topOf(enchantedPloughPos))
                            .text("Efficiency enchantments can increase the mining speed of the Mechanical Plough")
                    }

                    4 -> {
                        world().createItemEntity(
                            util.vector().centerOf(rightRail),
                            util.vector().of(0.0, 0.0, 0.0),
                            stack
                        )
                    }
                }
            }
            world().createItemEntity(util.vector().centerOf(leftRail), util.vector().of(0.0, 0.0, 0.0), stack)
            world().moveSection(gantrySection, util.vector().of(0.0, 0.0, -0.9), 18)

            idle(20)

            world().setKineticSpeed(util.select().position(largeCogWheelPos), -8f)
            world().setKineticSpeed(util.select().position(cogWheelPos), 16f)
            world().setKineticSpeed(gantryShaftSection, 16f)
            world().moveSection(gantrySection, util.vector().of(0.0, 0.0, 1.0), 20)

            idle(40)

            overlay().showText(50)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().blockSurface(enchantedPloughPos, Direction.WEST))
                .text("Any enchantment for mining tools can be applied to the Mechanical Plough")

            idle(70)

            overlay().showControls(util.vector().topOf(enchantedPloughPos), Pointing.DOWN, 50)
                .withItem(AllItems.GOGGLES.asStack())
            idle(7)
            overlay().showText(50)
                .text("When wearing Engineers' Goggles, you can view the applied enchantments")
                .attachKeyFrame()
                .colored(PonderPalette.MEDIUM)
                .pointAt(util.vector().topOf(enchantedPloughPos))
                .placeNearTarget()

            idle(70)
        }
    }
}
