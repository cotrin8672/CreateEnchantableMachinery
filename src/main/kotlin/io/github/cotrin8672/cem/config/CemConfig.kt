package io.github.cotrin8672.cem.config

import net.minecraftforge.common.ForgeConfigSpec

class CemConfig private constructor(builder: ForgeConfigSpec.Builder) {
    companion object {
        private val pair = ForgeConfigSpec.Builder().configure(::CemConfig)
        val CONFIG: CemConfig = pair.left
        val CONFIG_SPEC: ForgeConfigSpec = pair.right
    }

    val renderGlint: ForgeConfigSpec.ConfigValue<Boolean> = builder
        .translation("config.renderGlint")
        .define("renderGlint", true)

    init {
        builder.build()
    }
}
