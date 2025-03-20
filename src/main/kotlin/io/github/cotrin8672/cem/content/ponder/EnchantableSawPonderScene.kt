package io.github.cotrin8672.cem.content.ponder

import com.simibubi.create.AllItems
import com.simibubi.create.content.kinetics.saw.SawBlockEntity
import com.simibubi.create.foundation.ponder.CreateSceneBuilder
import io.github.cotrin8672.cem.content.block.saw.EnchantableSawBlockEntity
import io.github.cotrin8672.cem.registry.BlockRegistration
import net.createmod.catnip.math.Pointing
import net.createmod.ponder.api.PonderPalette
import net.createmod.ponder.api.scene.SceneBuilder
import net.createmod.ponder.api.scene.SceneBuildingUtil
import net.minecraft.core.Direction
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks

object EnchantableSawPonderScene {
    fun enchanting(builder: SceneBuilder, util: SceneBuildingUtil) {
        val sawPos = util.grid().at(2, 1, 3)
        val enchantedSawPos = util.grid().at(2, 1, 2)
        val cogWheelPos = util.grid().at(2, 1, 5)

        val inputBeltSection = util.select().fromTo(0, 1, 4, 1, 1, 2)
        val outputBeltSection = util.select().fromTo(3, 1, 4, 4, 1, 2)

        val normalSawInputStart = util.grid().at(0, 1, 3)
        val enchantedSawInputStart = util.grid().at(0, 1, 2)

        val normalItemSpawnPoint = util.vector().centerOf(normalSawInputStart.above())
        val enchantedItemSpawnPoint = util.vector().centerOf(enchantedSawInputStart.above())

        val processingSection = util.select().fromTo(0, 1, 4, 4, 1, 2)

        val shaftPos = util.grid().at(2, 1, 4)
        val verticalEnchantedSawPos = util.grid().at(2, 1, 3)

        val leavesPos = listOf(
            util.grid().at(3, 5, 0),
            util.grid().at(0, 4, 1),
            util.grid().at(2, 6, 1),
            util.grid().at(1, 4, 0),
            util.grid().at(1, 6, 2),
            util.grid().at(1, 5, 3),
            util.grid().at(0, 4, 3),
        )

        val breakLeavesPos = listOf(
            util.grid().at(1, 4, 4),
            util.grid().at(2, 4, 4),
            util.grid().at(3, 4, 4),
            util.grid().at(0, 4, 3),
            util.grid().at(1, 4, 3),
            util.grid().at(2, 4, 3),
            util.grid().at(3, 4, 3),
            util.grid().at(4, 4, 3),
        )

        with(CreateSceneBuilder(builder)) {
            title("mechanical_saw", "Enchanting to Mechanical Saw")
            configureBasePlate(0, 0, 5)

            world().setKineticSpeed(util.select().layer(0), -8f)
            world().setKineticSpeed(util.select().fromTo(cogWheelPos, enchantedSawPos), 16f)
            world().setKineticSpeed(inputBeltSection, -16f)
            world().setKineticSpeed(outputBeltSection, -16f)
            world().showSection(util.select().layer(0), Direction.UP)
            idle(5)
            world().showSection(util.select().fromTo(cogWheelPos, shaftPos), Direction.DOWN)
            idle(10)
            world().showSection(util.select().fromTo(sawPos, enchantedSawPos), Direction.DOWN)
            idle(10)
            world().showSection(inputBeltSection, Direction.EAST)
            world().showSection(outputBeltSection, Direction.WEST)

            idle(20)

            val sawSelect = util.select().position(sawPos)
            val enchantedSawSelect = util.select().position(enchantedSawPos)
            world().modifyBlockEntityNBT(sawSelect, SawBlockEntity::class.java) { it.putInt("RecipeIndex", 0) }
            world().modifyBlockEntityNBT(enchantedSawSelect, EnchantableSawBlockEntity::class.java) {
                it.putInt("RecipeIndex", 0)
            }

            overlay().showText(50)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().topOf(enchantedSawPos))
                .text("Like other tool, Mechanical Saw can be enchanted by enchanting table or anvil.")
            idle(60)

            val log = ItemStack(Items.OAK_LOG, 3)
            val normalLogEntity =
                world().createItemEntity(normalItemSpawnPoint, util.vector().of(0.0, 0.2, 0.0), log.copyWithCount(2))
            val enchantedLogEntity =
                world().createItemEntity(enchantedItemSpawnPoint, util.vector().of(0.0, 0.2, 0.0), log)

            idle(12)

            world().modifyEntity(normalLogEntity, Entity::discard)
            world().createItemOnBelt(normalSawInputStart, Direction.WEST, log.copyWithCount(2))
            world().modifyEntity(enchantedLogEntity, Entity::discard)
            world().createItemOnBelt(enchantedSawInputStart, Direction.WEST, log)

            idle(60)
            overlay().showText(50)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().topOf(enchantedSawPos))
                .text("Efficiency enchantments can increase the processing efficiency of items.")

            idle(200)

            world().hideSection(processingSection, Direction.DOWN)

            idle(20)

            world().setBlock(verticalEnchantedSawPos, BlockRegistration.ENCHANTABLE_MECHANICAL_SAW.defaultState, false)
            world().showSection(util.select().fromTo(shaftPos, verticalEnchantedSawPos), Direction.DOWN)
            world().setKineticSpeed(util.select().position(verticalEnchantedSawPos), 16f)

            idle(10)

            overlay().showText(50)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().topOf(sawPos))
                .text("Of course, enchantments can also be applied to logging.")

            idle(60)

            world().setBlocks(util.select().position(enchantedSawPos), Blocks.OAK_LOG.defaultBlockState(), false)

            world().showSection(util.select().position(enchantedSawPos), Direction.SOUTH)
            world().showSection(util.select().layersFrom(2), Direction.SOUTH)

            idle(5)

            for (i in 0..9) {
                idle(10)
                world().incrementBlockBreakingProgress(enchantedSawPos)
                if (i == 1) {
                    overlay().showText(40)
                        .attachKeyFrame()
                        .placeNearTarget()
                        .pointAt(util.vector().blockSurface(enchantedSawPos, Direction.WEST))
                        .text("When you cut it down using Mechanical Saw with a silk touch...")
                }
            }

            leavesPos.forEach(world()::destroyBlock)
            world().replaceBlocks(util.select().layersFrom(2), Blocks.AIR.defaultBlockState(), false)

            for (i in 0..4) {
                val dropPos = util.vector().centerOf(enchantedSawPos.above(i))
                val distance = dropPos.distanceTo(util.vector().centerOf(enchantedSawPos)).toFloat()
                world().createItemEntity(
                    dropPos,
                    util.vector().of(0.0, 0.0, -distance / 20.0),
                    ItemStack(Items.OAK_LOG)
                )
            }

            for (leaf in breakLeavesPos) {
                val dropPos = util.vector().centerOf(leaf)
                val distance = dropPos.distanceTo(enchantedSawPos.center)
                world().createItemEntity(
                    dropPos,
                    util.vector().of(0.0, 0.0, -distance / 20.0),
                    ItemStack(Items.OAK_LEAVES)
                )
            }

            idle(30)

            overlay().showText(50)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().centerOf(1, 1, 1))
                .text("The effect of enchantment allows you to cut down trees.")

            idle(70)

            overlay().showText(50)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().blockSurface(sawPos, Direction.WEST))
                .text("Mechanical Saw can grant any enchantment for mining tools.")

            idle(70)

            overlay().showControls(util.vector().topOf(sawPos), Pointing.DOWN, 50).withItem(AllItems.GOGGLES.asStack())
            idle(7)
            overlay().showText(50)
                .text("When wearing Engineers' Goggles, the player can get saw1's enchantments.")
                .attachKeyFrame()
                .colored(PonderPalette.MEDIUM)
                .pointAt(util.vector().topOf(sawPos))
                .placeNearTarget()

            idle(70)
        }
    }
}
