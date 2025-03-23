package io.github.cotrin8672.cem.content.ponder

import com.simibubi.create.AllItems
import com.simibubi.create.content.contraptions.actors.roller.RollerBlockEntity
import com.simibubi.create.foundation.ponder.CreateSceneBuilder
import io.github.cotrin8672.cem.content.block.roller.EnchantableRollerBlockEntity
import net.createmod.catnip.math.Pointing
import net.createmod.ponder.api.PonderPalette
import net.createmod.ponder.api.scene.SceneBuilder
import net.createmod.ponder.api.scene.SceneBuildingUtil
import net.minecraft.core.Direction

object EnchantableRollerPonderScene {
    fun enchanting(builder: SceneBuilder, util: SceneBuildingUtil) {
        val largeCogwheel = util.grid().at(3, 0, 5)
        val underCogwheel = util.grid().at(4, 1, 5)
        val topCogwheel = util.grid().at(4, 2, 5)
        val gantryShaftSection = util.select().fromTo(4, 2, 4, 4, 2, 2)
        val gantryCarriage = util.grid().at(3, 2, 4)
        val normalRoller = util.grid().at(2, 2, 3)
        val enchantedRoller = util.grid().at(1, 2, 3)
        val rightStone = util.grid().at(1, 1, 1)
        val leftStone = util.grid().at(2, 1, 1)

        with(CreateSceneBuilder(builder)) {
            title("mechanical_roller", "Enchanting to Mechanical Roller")
            configureBasePlate(0, 0, 5)

            val setForward = {
                world().setKineticSpeed(util.select().position(largeCogwheel), -8f)
                world().setKineticSpeed(util.select().position(underCogwheel), 16f)
                world().setKineticSpeed(util.select().position(topCogwheel), -16f)
                world().setKineticSpeed(gantryShaftSection, -16f)
            }
            val setBackward = {
                world().setKineticSpeed(util.select().position(largeCogwheel), 8f)
                world().setKineticSpeed(util.select().position(underCogwheel), -16f)
                world().setKineticSpeed(util.select().position(topCogwheel), 16f)
                world().setKineticSpeed(gantryShaftSection, 16f)
            }

            val rotateForward = {
                world().modifyBlockEntity(normalRoller, RollerBlockEntity::class.java) {
                    it.animatedSpeed = -150f
                }
                world().modifyBlockEntity(enchantedRoller, EnchantableRollerBlockEntity::class.java) {
                    it.animatedSpeed = -150f
                }
            }

            val stopRotate = {
                world().modifyBlockEntity(normalRoller, RollerBlockEntity::class.java) {
                    it.animatedSpeed = 0f
                }
                world().modifyBlockEntity(enchantedRoller, EnchantableRollerBlockEntity::class.java) {
                    it.animatedSpeed = 0f
                }
            }

            setBackward()

            world().showSection(util.select().layer(0), Direction.UP)
            idle(5)
            world().showSection(util.select().position(underCogwheel), Direction.DOWN)
            idle(5)
            world().showSection(util.select().position(topCogwheel), Direction.DOWN)
            idle(5)
            world().showSection(gantryShaftSection, Direction.SOUTH)
            idle(5)
            val gantrySection =
                world().showIndependentSection(util.select().fromTo(gantryCarriage, enchantedRoller), Direction.EAST)

            idle(20)

            overlay().showText(50)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().topOf(enchantedRoller))
                .text("Like other tool, Mechanical Harvester can be enchanted by enchanting table or anvil.")
            idle(60)

            world().showSection(util.select().fromTo(rightStone, leftStone), Direction.UP)

            idle(20)

            setForward()
            world().moveSection(gantrySection, util.vector().of(0.0, 0.0, -1.0), 20)
            rotateForward()

            idle(20)
            stopRotate()

            for (i in 0..9) {
                idle(5)
                world().incrementBlockBreakingProgress(rightStone)
                idle(5)
                world().incrementBlockBreakingProgress(rightStone)
                world().incrementBlockBreakingProgress(leftStone)

                if (i == 1) {
                    overlay().showText(80)
                        .attachKeyFrame()
                        .placeNearTarget()
                        .pointAt(util.vector().topOf(rightStone))
                        .text("Like other tool, Mechanical Harvester can be enchanted by enchanting table or anvil.")
                }
            }

            rotateForward()
            world().moveSection(gantrySection, util.vector().of(0.0, 0.0, -1.0), 20)

            idle(20)
            stopRotate()
            idle(20)

            setBackward()
            world().moveSection(gantrySection, util.vector().of(0.0, 0.0, 2.0), 40)

            idle(60)

            overlay().showControls(util.vector().topOf(enchantedRoller), Pointing.DOWN, 50)
                .withItem(AllItems.GOGGLES.asStack())
            idle(7)
            overlay().showText(50)
                .text("When wearing Engineers' Goggles, the player can get saw1's enchantments.")
                .attachKeyFrame()
                .colored(PonderPalette.MEDIUM)
                .pointAt(util.vector().topOf(enchantedRoller))
                .placeNearTarget()

            idle(70)
        }
    }
}
