package io.github.cotrin8672.fabric

import io.github.cotrin8672.CreateEnchantableMachinery
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.minecraftforge.common.data.ExistingFileHelper

class CreateEnchantableMachineryDatagen : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val helper = ExistingFileHelper.withResourcesFromArg()
        CreateEnchantableMachinery.REGISTRATE.setupDatagen(fabricDataGenerator, helper)
    }
}
