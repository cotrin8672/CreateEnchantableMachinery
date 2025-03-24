package io.github.cotrin8672.cem.content.ponder

import com.simibubi.create.AllFluids
import com.simibubi.create.AllItems
import com.simibubi.create.content.fluids.FluidFX
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity
import com.simibubi.create.foundation.ponder.CreateSceneBuilder
import io.github.cotrin8672.cem.content.block.spout.EnchantableSpoutBlockEntity
import net.createmod.catnip.math.Pointing
import net.createmod.catnip.math.VecHelper
import net.createmod.ponder.api.PonderPalette
import net.createmod.ponder.api.scene.SceneBuilder
import net.createmod.ponder.api.scene.SceneBuildingUtil
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.fluids.FluidStack

object EnchantableSpoutPonderScene {
    fun enchanting(builder: SceneBuilder, util: SceneBuildingUtil) {
        val largeCogwheel = util.grid().at(1, 0, 5)
        val cogwheel = util.grid().at(0, 1, 5)
        val shaft1 = util.grid().at(0, 1, 4)
        val shaft2 = util.grid().at(0, 1, 2)
        val normalBeltStart = util.grid().at(0, 1, 3)
        val enchantedBeltStart = util.grid().at(0, 1, 1)
        val normalSpout = util.grid().at(2, 3, 3)
        val enchantedSpout = util.grid().at(2, 3, 1)

        with(CreateSceneBuilder(builder)) {
            title("spout", "Enchanting to Spout")
            configureBasePlate(0, 0, 5)

            world().setKineticSpeed(util.select().position(largeCogwheel), 8f)
            world().setKineticSpeed(util.select().layer(1), -16f)

            world().showSection(util.select().layer(0), Direction.UP)
            idle(5)
            world().showSection(util.select().fromTo(cogwheel, shaft1), Direction.DOWN)
            idle(5)
            world().showSection(util.select().fromTo(normalBeltStart, util.grid().at(4, 1, 1)), Direction.SOUTH)
            idle(5)
            world().showSection(util.select().layer(3), Direction.DOWN)
            idle(20)

            overlay().showText(50)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().topOf(enchantedSpout))
                .text("The Spout can be enchanted with Efficiency enchantments")
            idle(60)

            val bottle = ItemStack(Items.GLASS_BOTTLE)
            val honeyBottle = ItemStack(Items.HONEY_BOTTLE)
            overlay().showControls(util.vector().topOf(normalBeltStart), Pointing.DOWN, 30).withItem(bottle)
            overlay().showControls(util.vector().topOf(enchantedBeltStart), Pointing.DOWN, 30).withItem(bottle)

            idle(40)

            val normalItemEntity = world().createItemEntity(
                util.vector().topOf(normalBeltStart),
                util.vector().of(0.0, 0.2, 0.0),
                bottle
            )
            val enchantedItemEntity = world().createItemEntity(
                util.vector().topOf(enchantedBeltStart),
                util.vector().of(0.0, 0.2, 0.0),
                bottle
            )

            idle(10)

            world().modifyEntity(normalItemEntity, Entity::discard)
            world().modifyEntity(enchantedItemEntity, Entity::discard)

            val normalBeltItem = world().createItemOnBelt(normalBeltStart, Direction.UP, bottle)
            val enchantedBeltItem = world().createItemOnBelt(enchantedBeltStart, Direction.UP, bottle)

            idle(30)
            overlay().showText(50)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().topOf(enchantedSpout))
                .text("A Spout with Efficiency enchantments can fill items with liquids faster")
            idle(30)

            world().stallBeltItem(normalBeltItem, true)
            world().stallBeltItem(enchantedBeltItem, true)

            idle(10)
            world().modifyBlockEntityNBT(
                util.select().position(enchantedSpout),
                EnchantableSpoutBlockEntity::class.java
            ) {
                it.putInt("ProcessingTicks", 10)
            }

            idle(10)
            world().modifyBlockEntityNBT(util.select().position(normalSpout), SpoutBlockEntity::class.java) {
                it.putInt("ProcessingTicks", 20)
            }
            world().changeBeltItemTo(enchantedBeltItem, honeyBottle)
            val fluidParticle = FluidFX.getFluidParticle(FluidStack(AllFluids.HONEY.get(), 1000))
            for (i in 0..9) {
                effects().emitParticles(
                    util.vector().topOf(enchantedSpout.below(2)).add(0.0, (1 / 16.0), 0.0),
                    effects().simpleParticleEmitter(
                        fluidParticle,
                        VecHelper.offsetRandomly(Vec3.ZERO, RandomSource.create(), .1f)
                    ), 1f, 1
                )
            }
            world().stallBeltItem(enchantedBeltItem, false)

            idle(20)

            world().changeBeltItemTo(normalBeltItem, honeyBottle)
            for (i in 0..9) {
                effects().emitParticles(
                    util.vector().topOf(normalSpout.below(2)).add(0.0, (1 / 16.0), 0.0),
                    effects().simpleParticleEmitter(
                        fluidParticle,
                        VecHelper.offsetRandomly(Vec3.ZERO, RandomSource.create(), .1f)
                    ), 1f, 1
                )
            }
            world().stallBeltItem(normalBeltItem, false)

            idle(60)

            overlay().showControls(util.vector().topOf(enchantedSpout), Pointing.DOWN, 50)
                .withItem(AllItems.GOGGLES.asStack())
            idle(7)
            overlay().showText(50)
                .text("When wearing Engineers' Goggles, you can view the applied enchantments")
                .attachKeyFrame()
                .colored(PonderPalette.MEDIUM)
                .pointAt(util.vector().topOf(enchantedSpout))
                .placeNearTarget()

            idle(70)
        }
    }
}
