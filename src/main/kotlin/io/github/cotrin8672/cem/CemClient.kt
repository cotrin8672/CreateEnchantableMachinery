package io.github.cotrin8672.cem

import io.github.cotrin8672.cem.content.ponder.CemPonderPlugin
import io.github.cotrin8672.cem.registry.PartialModelRegistration
import net.createmod.ponder.foundation.PonderIndex
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(value = Cem.MOD_ID, dist = [Dist.CLIENT])
class CemClient {
    init {
        MOD_BUS.addListener<FMLClientSetupEvent> {
            PartialModelRegistration.register()
            PonderIndex.addPlugin(CemPonderPlugin)
        }
    }
}
