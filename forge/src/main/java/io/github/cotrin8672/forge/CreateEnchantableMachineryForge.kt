package io.github.cotrin8672.forge

import io.github.cotrin8672.CreateEnchantableMachinery
import io.github.cotrin8672.CreateEnchantableMachinery.init
import io.github.cotrin8672.CreateEnchantableMachinery.registrateHandler
import net.minecraftforge.fml.common.Mod
import org.koin.core.context.startKoin

@Mod(CreateEnchantableMachinery.MOD_ID)
class CreateEnchantableMachineryForge {
    init {
        startKoin {
            modules(registrateModule)
        }

        registrateHandler.getRegistrate()
        registrateHandler.register()
        init()
    }
}
