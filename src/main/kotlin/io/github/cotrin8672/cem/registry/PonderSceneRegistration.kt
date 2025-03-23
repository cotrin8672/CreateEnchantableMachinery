package io.github.cotrin8672.cem.registry

import com.simibubi.create.AllBlocks
import com.tterrag.registrate.util.entry.ItemProviderEntry
import io.github.cotrin8672.cem.content.ponder.*
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper
import net.minecraft.resources.ResourceLocation

object PonderSceneRegistration {
    fun register(helper: PonderSceneRegistrationHelper<ResourceLocation>) {
        val registry = helper.withKeyFunction { obj: ItemProviderEntry<*, *> -> obj.id }

        registry.forComponents(AllBlocks.MECHANICAL_DRILL)
            .addStoryBoard(
                "mechanical_drill/enchanting",
                EnchantableDrillPonderScene::enchanting,
            )

        registry.forComponents(AllBlocks.MECHANICAL_SAW)
            .addStoryBoard(
                "mechanical_saw/enchanting",
                EnchantableSawPonderScene::enchanting
            )

        registry.forComponents(AllBlocks.MECHANICAL_HARVESTER)
            .addStoryBoard(
                "mechanical_harvester/enchanting",
                EnchantableHarvesterPonderScene::enchanting
            )

        registry.forComponents(AllBlocks.MECHANICAL_PLOUGH)
            .addStoryBoard(
                "mechanical_plough/enchanting",
                EnchantablePloughPonderScene::enchanting
            )

        registry.forComponents(AllBlocks.ENCASED_FAN)
            .addStoryBoard(
                "encased_fan/enchanting",
                EnchantableEncasedFanPonderScene::enchanting
            )

        registry.forComponents(AllBlocks.MILLSTONE)
            .addStoryBoard(
                "millstone/enchanting",
                EnchantableMillstonePonderScene::enchanting
            )

        registry.forComponents(AllBlocks.CRUSHING_WHEEL)
            .addStoryBoard(
                "crushing_wheel/enchanting",
                EnchantableCrushingWheelPonderScene::enchanting
            )

        registry.forComponents(AllBlocks.MECHANICAL_PRESS)
            .addStoryBoard(
                "mechanical_press/enchanting",
                EnchantablePressPonderScene::enchanting
            )

        registry.forComponents(AllBlocks.MECHANICAL_MIXER)
            .addStoryBoard(
                "mechanical_mixer/enchanting",
                EnchantableMixerPonderScene::enchanting
            )

        registry.forComponents(AllBlocks.MECHANICAL_ROLLER)
            .addStoryBoard(
                "mechanical_roller/enchanting",
                EnchantableRollerPonderScene::enchanting
            )
    }
}
