package io.github.cotrin8672.cem

import io.github.cotrin8672.cem.registry.PartialModelRegistration
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.common.Mod

@Mod(value = CreateEnchantableMachinery.MOD_ID, dist = [Dist.CLIENT])
class CreateEnchantableMachineryClient {
    init {
        PartialModelRegistration.register()
    }
}
