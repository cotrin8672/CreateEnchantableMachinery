package io.github.cotrin8672.createenchantablemachinery.fabric

import io.github.cotrin8672.createenchantablemachinery.CreateEnchantableMachinery
import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator

class CreateEnchantableMachineryDatagen : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val helper = ExistingFileHelper.withResourcesFromArg()
        CreateEnchantableMachinery.REGISTRATE.setupDatagen(fabricDataGenerator.createPack(), helper)
    }
}
