package io.github.cotrin8672.cem.config

import net.neoforged.neoforge.common.ModConfigSpec

class CemConfig private constructor(builder: ModConfigSpec.Builder) {
    companion object {
        private val pair = ModConfigSpec.Builder().configure(::CemConfig)
        val CONFIG: CemConfig = pair.left
        val CONFIG_SPEC: ModConfigSpec = pair.right
    }

    val renderGlint: ModConfigSpec.ConfigValue<Boolean> = builder
        .translation("config.renderGlint")
        .define("renderGlint", true)

    init {
        builder.build()
    }
}
