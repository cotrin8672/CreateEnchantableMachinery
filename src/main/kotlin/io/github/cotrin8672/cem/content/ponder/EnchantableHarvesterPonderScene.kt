package io.github.cotrin8672.cem.content.ponder

import com.simibubi.create.AllItems
import com.simibubi.create.foundation.ponder.CreateSceneBuilder
import io.github.cotrin8672.cem.content.block.harvester.EnchantableHarvesterBlockEntity
import net.createmod.catnip.math.Pointing
import net.createmod.ponder.api.PonderPalette
import net.createmod.ponder.api.scene.SceneBuilder
import net.createmod.ponder.api.scene.SceneBuildingUtil
import net.minecraft.core.Direction
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.properties.BlockStateProperties

object EnchantableHarvesterPonderScene {
    fun enchanting(builder: SceneBuilder, util: SceneBuildingUtil) {
        val largeCogWheelPos = util.grid().at(3, 0, 5)
        val cogWheelPos = util.grid().at(4, 1, 5)
        val gantryShaftSection = util.select().fromTo(4, 1, 4, 4, 1, 2)
        val gantryCarriagePos = util.grid().at(3, 1, 4)
        val leafPos = util.grid().at(2, 1, 1)
        val enchantedHarvesterPos = util.grid().at(2, 1, 3)

        with(CreateSceneBuilder(builder)) {
            title("mechanical_harvester", "Enchanting to Mechanical Harvester")
            configureBasePlate(0, 0, 5)

            world().showSection(util.select().layer(0), Direction.UP)
            idle(5)
            world().showSection(util.select().position(cogWheelPos), Direction.DOWN)
            idle(10)
            world().showSection(gantryShaftSection, Direction.SOUTH)
            idle(10)
            val gantrySection = world().showIndependentSection(
                util.select().fromTo(gantryCarriagePos, enchantedHarvesterPos),
                Direction.EAST
            )
            idle(20)

            overlay().showText(50)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().topOf(enchantedHarvesterPos))
                .text("Like other tools, the Mechanical Harvester can be enchanted by an enchanting table or anvil")
            idle(60)

            world().showSection(util.select().position(leafPos), Direction.UP)

            idle(20)

            world().setKineticSpeed(util.select().position(largeCogWheelPos), 8f)
            world().setKineticSpeed(util.select().position(cogWheelPos), -16f)
            world().setKineticSpeed(gantryShaftSection, -16f)
            world().moveSection(gantrySection, util.vector().of(0.0, 0.0, -2.0), 40)
            world().modifyBlockEntity(enchantedHarvesterPos, EnchantableHarvesterBlockEntity::class.java) {
                it.animatedSpeed = -150f
            }

            idle(20)
            world().destroyBlock(leafPos)
            val leafStack = ItemStack(Items.OAK_LEAVES)
            val leafItemEntity = world().createItemEntity(leafPos.center, util.vector().of(0.0, 0.0, 0.0), leafStack)

            idle(20)

            world().modifyBlockEntity(enchantedHarvesterPos, EnchantableHarvesterBlockEntity::class.java) {
                it.animatedSpeed = 0f
            }

            idle(10)

            world().setKineticSpeed(util.select().position(largeCogWheelPos), -8f)
            world().setKineticSpeed(util.select().position(cogWheelPos), 16f)
            world().setKineticSpeed(gantryShaftSection, 16f)
            world().moveSection(gantrySection, util.vector().of(0.0, 0.0, 2.0), 40)
            world().modifyBlockEntity(enchantedHarvesterPos, EnchantableHarvesterBlockEntity::class.java) {
                it.animatedSpeed = 150f
            }

            idle(40)

            world().modifyBlockEntity(enchantedHarvesterPos, EnchantableHarvesterBlockEntity::class.java) {
                it.animatedSpeed = 0f
            }

            idle(20)

            overlay().showText(50)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().centerOf(leafPos))
                .text("It can be enchanted with silk touch effects, and...")
            idle(60)

            world().modifyEntity(leafItemEntity, Entity::discard)
            world().hideSection(util.select().fromTo(leafPos, leafPos.below()), Direction.DOWN)

            idle(20)

            world().setBlock(leafPos.below(), Blocks.FARMLAND.defaultBlockState(), false)
            world().setBlock(
                leafPos,
                Blocks.POTATOES.defaultBlockState().setValue(BlockStateProperties.AGE_7, 7),
                false
            )

            world().showSection(util.select().fromTo(leafPos.below(), leafPos), Direction.UP)

            idle(20)

            world().setKineticSpeed(util.select().position(largeCogWheelPos), 8f)
            world().setKineticSpeed(util.select().position(cogWheelPos), -16f)
            world().setKineticSpeed(gantryShaftSection, -16f)
            world().moveSection(gantrySection, util.vector().of(0.0, 0.0, -2.0), 40)
            world().modifyBlockEntity(enchantedHarvesterPos, EnchantableHarvesterBlockEntity::class.java) {
                it.animatedSpeed = -150f
            }

            idle(20)
            world().setBlock(leafPos, Blocks.POTATOES.defaultBlockState(), true)
            val potatoStack = ItemStack(Items.POTATO, 7)
            world().createItemEntity(leafPos.center, util.vector().of(0.0, 0.0, 0.0), potatoStack)

            idle(20)

            world().modifyBlockEntity(enchantedHarvesterPos, EnchantableHarvesterBlockEntity::class.java) {
                it.animatedSpeed = 0f
            }

            idle(10)

            world().setKineticSpeed(util.select().position(largeCogWheelPos), -8f)
            world().setKineticSpeed(util.select().position(cogWheelPos), 16f)
            world().setKineticSpeed(gantryShaftSection, 16f)
            world().moveSection(gantrySection, util.vector().of(0.0, 0.0, 2.0), 40)
            world().modifyBlockEntity(enchantedHarvesterPos, EnchantableHarvesterBlockEntity::class.java) {
                it.animatedSpeed = 150f
            }

            idle(40)

            world().modifyBlockEntity(enchantedHarvesterPos, EnchantableHarvesterBlockEntity::class.java) {
                it.animatedSpeed = 0f
            }

            idle(20)

            overlay().showText(50)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().centerOf(leafPos))
                .text("Fortune enchantments can yield more crops")

            idle(70)

            overlay().showText(50)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().blockSurface(enchantedHarvesterPos, Direction.WEST))
                .text("The Mechanical Harvester can receive any enchantment applicable to mining tools")

            idle(70)

            overlay().showControls(util.vector().topOf(enchantedHarvesterPos), Pointing.DOWN, 50)
                .withItem(AllItems.GOGGLES.asStack())
            idle(7)
            overlay().showText(50)
                .text("When wearing Engineers' Goggles, the applied enchantments can be viewed")
                .attachKeyFrame()
                .colored(PonderPalette.MEDIUM)
                .pointAt(util.vector().topOf(enchantedHarvesterPos))
                .placeNearTarget()

            idle(70)
        }
    }
}
