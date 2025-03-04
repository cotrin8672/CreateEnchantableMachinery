package io.github.cotrin8672.cem.config

import net.createmod.catnip.config.ConfigBase

class CemCommonConfig : ConfigBase() {
    val kinetics: CemKinetics = nested(0, ::CemKinetics, "Kinetics")

    override fun getName(): String {
        return "common"
    }
}
