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

object EnchantableDrillPonderScene {
    fun enchanting(builder: SceneBuilder, util: SceneBuildingUtil) {
        val scene = CreateSceneBuilder(builder)
        scene.title("mechanical_drill", "Enchanting to Mechanical Drill.")
        scene.configureBasePlate(0, 0, 5)

        scene.world().setKineticSpeed(util.select().layer(0), 8f)
        scene.world().setKineticSpeed(util.select().fromTo(1, 1, 5, 1, 1, 3), -16f)
        scene.world().setKineticSpeed(util.select().fromTo(3, 1, 5, 3, 1, 3), -16f)
        scene.world().showSection(util.select().layer(0), Direction.UP)
        scene.idle(5)
        scene.world().showSection(util.select().fromTo(1, 1, 5, 3, 1, 4), Direction.DOWN)
        scene.idle(10)
        scene.world().showSection(util.select().fromTo(1, 1, 3, 3, 1, 3), Direction.SOUTH)

        scene.idle(20)

        scene.overlay().showText(80)
            .attachKeyFrame()
            .placeNearTarget()
            .pointAt(util.vector().topOf(1, 1, 3))
            .text("Like other tool, Mechanical Drill can be enchanted by enchanting table or anvil.")
        scene.idle(80)

        scene.world().showSection(util.select().fromTo(1, 1, 2, 3, 1, 2), Direction.DOWN)
        scene.idle(5)

        val normalDrillStone = util.grid().at(3, 1, 2)
        val enchantedDrillStone = util.grid().at(1, 1, 2)

        for (i in 0..9) {
            scene.idle(5)
            scene.world().incrementBlockBreakingProgress(enchantedDrillStone)
            scene.idle(5)
            scene.world().incrementBlockBreakingProgress(enchantedDrillStone)
            scene.world().incrementBlockBreakingProgress(normalDrillStone)

            when (i) {
                1 -> {
                    scene.overlay().showText(80)
                        .attachKeyFrame()
                        .placeNearTarget()
                        .pointAt(util.vector().topOf(1, 1, 3))
                        .text("Efficiency enchantments can increase the mining speed of mechanical drills...")
                }

                4 -> {
                    scene.world().createItemEntity(
                        util.vector().centerOf(enchantedDrillStone),
                        util.vector().of(0.0, .1, 0.0), ItemStack(Items.STONE)
                    )
                }

                9 -> {
                    scene.world().createItemEntity(
                        util.vector().centerOf(normalDrillStone),
                        util.vector().of(0.0, .1, 0.0), ItemStack(Items.COBBLESTONE)
                    )
                }
            }
        }

        scene.idle(20)

        scene.overlay().showText(80)
            .attachKeyFrame()
            .placeNearTarget()
            .pointAt(util.vector().centerOf(enchantedDrillStone))
            .text("Silk touch mining can also be used")

        scene.idle(100)

        scene.overlay().showText(80)
            .attachKeyFrame()
            .placeNearTarget()
            .pointAt(util.vector().topOf(1, 1, 3))
            .text("Mechanical Drill can grant any enchantment for mining tools")

        scene.idle(100)

        val enchantedDrill = util.vector().topOf(1, 1, 3)

        scene.overlay().showControls(enchantedDrill, Pointing.DOWN, 80).withItem(AllItems.GOGGLES.asStack())
        scene.idle(7)
        scene.overlay().showText(80)
            .text("When wearing Engineers' Goggles, the player can get drill's enchantments.")
            .attachKeyFrame()
            .colored(PonderPalette.MEDIUM)
            .pointAt(enchantedDrill)
            .placeNearTarget()
        scene.idle(100)
    }
}
