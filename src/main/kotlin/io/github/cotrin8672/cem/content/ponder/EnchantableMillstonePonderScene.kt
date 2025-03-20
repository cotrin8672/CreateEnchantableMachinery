package io.github.cotrin8672.cem.content.ponder

import com.simibubi.create.AllItems
import com.simibubi.create.content.kinetics.millstone.MillstoneBlockEntity
import com.simibubi.create.foundation.ponder.CreateSceneBuilder
import io.github.cotrin8672.cem.content.block.millstone.EnchantableMillstoneBlockEntity
import net.createmod.catnip.math.Pointing
import net.createmod.ponder.api.PonderPalette
import net.createmod.ponder.api.scene.SceneBuilder
import net.createmod.ponder.api.scene.SceneBuildingUtil
import net.minecraft.core.Direction
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

object EnchantableMillstonePonderScene {
    fun enchanting(builder: SceneBuilder, util: SceneBuildingUtil) {
        val millstonePos = util.grid().at(3, 2, 3)
        val enchantedMillstonePos = util.grid().at(1, 2, 3)
        val verticalLargeCogwheelPos = util.grid().at(2, 0, 5)
        val horizontalLargeCogwheelPos = util.grid().at(2, 1, 4)
        val rightCogwheelPos = util.grid().at(1, 1, 3)
        val leftCogwheelPos = util.grid().at(3, 1, 3)
        val casingSection = util.select().fromTo(rightCogwheelPos, leftCogwheelPos)
        val rightFunnel = util.grid().at(1, 2, 2)
        val leftFunnel = util.grid().at(3, 2, 2)
        val funnelSection = util.select().fromTo(rightFunnel, leftFunnel)

        with(CreateSceneBuilder(builder)) {
            title("millstone", "Enchanting to Mechanical Harvester")
            configureBasePlate(0, 0, 5)

            world().setKineticSpeed(util.select().position(verticalLargeCogwheelPos), -8f)
            world().setKineticSpeed(util.select().position(horizontalLargeCogwheelPos), 8f)
            world().setKineticSpeed(util.select().fromTo(rightCogwheelPos, millstonePos), -16f)

            world().showSection(util.select().layer(0), Direction.UP)
            idle(5)
            world().showSection(util.select().position(horizontalLargeCogwheelPos), Direction.DOWN)
            idle(10)
            world().showSection(casingSection, Direction.UP)
            idle(10)
            world().showSection(util.select().position(millstonePos), Direction.DOWN)
            world().showSection(util.select().position(enchantedMillstonePos), Direction.DOWN)

            idle(20)

            overlay().showText(50)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().topOf(enchantedMillstonePos))
                .text("Like other tool, Mechanical Harvester can be enchanted by enchanting table or anvil.")
            idle(60)

            world().showSection(funnelSection, Direction.SOUTH)

            idle(20)

            val stack = ItemStack(Items.WHEAT)
            val normalItem = world().createItemEntity(
                util.vector().topOf(millstonePos),
                util.vector().of(0.0, 0.25, 0.0),
                stack
            )
            val enchantedItem = world().createItemEntity(
                util.vector().topOf(enchantedMillstonePos),
                util.vector().of(0.0, 0.25, 0.0),
                stack
            )

            idle(10)

            world().modifyEntity(normalItem, Entity::discard)
            world().modifyEntity(enchantedItem, Entity::discard)

            world().modifyBlockEntity(millstonePos, MillstoneBlockEntity::class.java) {
                it.inputInv.setStackInSlot(0, stack)
            }
            world().modifyBlockEntity(enchantedMillstonePos, EnchantableMillstoneBlockEntity::class.java) {
                it.inputInv.setStackInSlot(0, stack)
            }

            overlay().showText(50)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().topOf(enchantedMillstonePos))
                .text("Like other tool, Mechanical Harvester can be enchanted by enchanting table or anvil.")
            idle(50)

            val flour = AllItems.WHEAT_FLOUR.asStack()
            // Enchantable Millstone processing end
            idle(10)
            world().modifyBlockEntity(enchantedMillstonePos, EnchantableMillstoneBlockEntity::class.java) {
                it.inputInv.setStackInSlot(0, ItemStack.EMPTY)
            }
            world().flapFunnel(rightFunnel, true)
            world().createItemEntity(rightFunnel.center, util.vector().of(0.0, 0.0, 0.0), flour)

            // Millstone processing end
            idle(40)
            world().modifyBlockEntity(millstonePos, MillstoneBlockEntity::class.java) {
                it.inputInv.setStackInSlot(0, ItemStack.EMPTY)
            }
            world().flapFunnel(leftFunnel, true)
            world().createItemEntity(leftFunnel.center, util.vector().of(0.0, 0.0, 0.0), flour)

            idle(20)

            overlay().showControls(util.vector().topOf(enchantedMillstonePos), Pointing.DOWN, 50)
                .withItem(AllItems.GOGGLES.asStack())
            idle(7)
            overlay().showText(50)
                .text("When wearing Engineers' Goggles, the player can get saw1's enchantments.")
                .attachKeyFrame()
                .colored(PonderPalette.MEDIUM)
                .pointAt(util.vector().topOf(enchantedMillstonePos))
                .placeNearTarget()

            idle(70)
        }
    }
}
