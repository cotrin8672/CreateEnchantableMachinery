package io.github.cotrin8672.cem.content.ponder

import com.simibubi.create.AllItems
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity
import com.simibubi.create.content.kinetics.press.PressingBehaviour
import com.simibubi.create.foundation.ponder.CreateSceneBuilder
import io.github.cotrin8672.cem.content.block.press.EnchantableMechanicalPressBlockEntity
import net.createmod.catnip.math.Pointing
import net.createmod.ponder.api.PonderPalette
import net.createmod.ponder.api.scene.SceneBuilder
import net.createmod.ponder.api.scene.SceneBuildingUtil
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

object EnchantablePressPonderScene {
    fun enchanting(builder: SceneBuilder, util: SceneBuildingUtil) {
        val largeCogwheel = util.grid().at(1, 0, 5)
        val cogwheel = util.grid().at(2, 1, 5)
        val verticalShaftSection = util.select().fromTo(2, 2, 3, 2, 3, 3)
        val rightBasin = util.grid().at(1, 2, 3)
        val leftBasin = util.grid().at(3, 2, 3)
        val underGearbox = util.grid().at(2, 1, 3)
        val topGearbox = util.grid().at(2, 4, 3)
        val enchantedPress = util.grid().at(1, 4, 3)
        val normalPress = util.grid().at(3, 4, 3)
        val beltStart = util.grid().at(0, 1, 2)
        val beltSection = util.select().fromTo(beltStart, util.grid().at(4, 1, 2))

        with(CreateSceneBuilder(builder)) {
            title("mechanical_press", "Enchanting to Mechanical Press")
            configureBasePlate(0, 0, 5)

            world().setKineticSpeed(util.select().position(largeCogwheel), -8f)
            world().setKineticSpeed(util.select().position(cogwheel), 16f)
            world().setKineticSpeed(util.select().position(underGearbox), 16f)
            world().setKineticSpeed(verticalShaftSection, -16f)
            world().setKineticSpeed(util.select().fromTo(enchantedPress, normalPress), 16f)
            world().setKineticSpeed(beltSection, -16f)

            world().showSection(util.select().layer(0), Direction.UP)
            idle(5)
            world().showSection(util.select().layer(1), Direction.DOWN)
            idle(5)
            world().showSection(util.select().fromTo(rightBasin, leftBasin), Direction.DOWN)
            world().showSection(util.select().fromTo(topGearbox.below(1), topGearbox), Direction.DOWN)
            idle(5)
            world().showSection(util.select().position(enchantedPress), Direction.EAST)
            world().showSection(util.select().position(normalPress), Direction.WEST)
            idle(20)

            overlay().showText(50)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().topOf(enchantedPress))
                .text("Like other tool, Mechanical Harvester can be enchanted by enchanting table or anvil.")
            idle(60)

            val ironIngot = ItemStack(Items.IRON_INGOT)
            overlay().showControls(util.vector().topOf(rightBasin), Pointing.DOWN, 30).withItem(ironIngot)
            overlay().showControls(util.vector().topOf(leftBasin), Pointing.DOWN, 30).withItem(ironIngot)

            idle(40)

            world().modifyBlockEntity(enchantedPress, EnchantableMechanicalPressBlockEntity::class.java) {
                it.pressingBehaviour.start(PressingBehaviour.Mode.BASIN)
            }
            world().modifyBlockEntity(normalPress, MechanicalPressBlockEntity::class.java) {
                it.pressingBehaviour.start(PressingBehaviour.Mode.BASIN)
            }

            overlay().showText(50)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().topOf(enchantedPress))
                .text("Like other tool, Mechanical Harvester can be enchanted by enchanting table or anvil.")

            val ironBlock = ItemStack(Items.IRON_BLOCK)
            idle(35)
            world().createItemOnBelt(beltStart.east(), Direction.UP, ironBlock)
            idle(30)
            world().createItemOnBelt(beltStart.east(3), Direction.UP, ironBlock)
            world().modifyBlockEntity(enchantedPress, EnchantableMechanicalPressBlockEntity::class.java) {
                it.pressingBehaviour.start(PressingBehaviour.Mode.BASIN)
            }
            idle(35)
            world().createItemOnBelt(beltStart.east(), Direction.UP, ironBlock)

            idle(60)

            overlay().showControls(util.vector().centerOf(enchantedPress), Pointing.RIGHT, 50)
                .withItem(AllItems.GOGGLES.asStack())
            idle(7)
            overlay().showText(50)
                .text("When wearing Engineers' Goggles, the player can get saw1's enchantments.")
                .attachKeyFrame()
                .colored(PonderPalette.MEDIUM)
                .pointAt(util.vector().topOf(enchantedPress))
                .placeNearTarget()

            idle(70)
        }
    }
}
