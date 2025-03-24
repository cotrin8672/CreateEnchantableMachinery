package io.github.cotrin8672.cem.content.ponder

import com.simibubi.create.AllItems
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity
import com.simibubi.create.foundation.ponder.CreateSceneBuilder
import io.github.cotrin8672.cem.content.block.mixer.EnchantableMechanicalMixerBlockEntity
import net.createmod.catnip.math.Pointing
import net.createmod.ponder.api.PonderPalette
import net.createmod.ponder.api.scene.SceneBuilder
import net.createmod.ponder.api.scene.SceneBuildingUtil
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

object EnchantableMixerPonderScene {
    fun enchanting(builder: SceneBuilder, util: SceneBuildingUtil) {
        val largeCogwheel = util.grid().at(1, 0, 5)
        val cogwheel1 = util.grid().at(2, 1, 5)
        val cogwheel2 = util.grid().at(2, 4, 3)
        val verticalShaftSection = util.select().fromTo(2, 2, 3, 2, 3, 3)
        val rightBasin = util.grid().at(1, 2, 3)
        val leftBasin = util.grid().at(3, 2, 3)
        val underGearbox = util.grid().at(2, 1, 3)
        val enchantedMixer = util.grid().at(1, 4, 3)
        val normalMixer = util.grid().at(3, 4, 3)
        val beltStart = util.grid().at(0, 1, 2)
        val beltSection = util.select().fromTo(beltStart, util.grid().at(4, 1, 2))

        with(CreateSceneBuilder(builder)) {
            title("mechanical_mixer", "Enchanting to Mechanical Mixer")
            configureBasePlate(0, 0, 5)

            world().setKineticSpeed(util.select().position(largeCogwheel), -8f)
            world().setKineticSpeed(util.select().position(cogwheel1), -16f)
            world().setKineticSpeed(util.select().position(cogwheel2), -16f)
            world().setKineticSpeed(util.select().position(underGearbox), -16f)
            world().setKineticSpeed(verticalShaftSection, -16f)
            world().setKineticSpeed(util.select().position(enchantedMixer), 16f)
            world().setKineticSpeed(util.select().position(normalMixer), 16f)
            world().setKineticSpeed(beltSection, -16f)

            world().showSection(util.select().layer(0), Direction.UP)
            idle(5)
            world().showSection(util.select().layer(1), Direction.DOWN)
            idle(5)
            world().showSection(util.select().fromTo(rightBasin, leftBasin), Direction.DOWN)
            world().showSection(util.select().fromTo(cogwheel2.below(1), cogwheel2), Direction.DOWN)
            idle(5)
            world().showSection(util.select().position(enchantedMixer), Direction.EAST)
            world().showSection(util.select().position(normalMixer), Direction.WEST)
            idle(20)

            overlay().showText(50)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().topOf(enchantedMixer))
                .text("The Mechanical Mixer can be enchanted with Efficiency enchantments")
            idle(60)

            val redDye = ItemStack(Items.RED_DYE)
            val blueDye = ItemStack(Items.BLUE_DYE)
            val purpleDye = ItemStack(Items.PURPLE_DYE)
            overlay().showControls(util.vector().topOf(rightBasin), Pointing.DOWN, 30).withItem(redDye)
            overlay().showControls(util.vector().topOf(rightBasin), Pointing.UP, 30).withItem(blueDye)
            overlay().showControls(util.vector().topOf(leftBasin), Pointing.DOWN, 30).withItem(redDye)
            overlay().showControls(util.vector().topOf(leftBasin), Pointing.UP, 30).withItem(blueDye)

            idle(40)

            world().modifyBlockEntity(enchantedMixer, EnchantableMechanicalMixerBlockEntity::class.java) {
                it.startProcessingBasin()
            }
            world().modifyBlockEntity(normalMixer, MechanicalMixerBlockEntity::class.java) {
                it.startProcessingBasin()
            }

            overlay().showText(50)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().topOf(enchantedMixer))
                .text("A Mechanical Mixer with Efficiency enchantments can process items faster")

            idle(60)
            world().createItemOnBelt(beltStart.east(), Direction.UP, purpleDye)
            idle(40)
            world().createItemOnBelt(beltStart.east(3), Direction.UP, purpleDye)

            idle(60)

            overlay().showControls(util.vector().centerOf(enchantedMixer), Pointing.RIGHT, 50)
                .withItem(AllItems.GOGGLES.asStack())
            idle(7)
            overlay().showText(50)
                .text("When wearing Engineers' Goggles, you can view the applied enchantments")
                .attachKeyFrame()
                .colored(PonderPalette.MEDIUM)
                .pointAt(util.vector().topOf(enchantedMixer))
                .placeNearTarget()

            idle(70)
        }
    }
}
