package io.github.cotrin8672.cem

import net.minecraftforge.fml.common.Mod

@Mod(Cem.MOD_ID)
object CemForge {

    init {
        Cem.registrate()
    }
}
