package io.github.cotrin8672.createenchantablemachinery.config

import net.minecraftforge.common.ForgeConfigSpec

object Config {
    private val builder = ForgeConfigSpec.Builder()

    lateinit var renderGlint: ForgeConfigSpec.BooleanValue

    val clientSpec: ForgeConfigSpec by lazy {
        renderGlint = builder
            .comment("Render enchantment glint")
            .define("renderGlint", true)
        builder.build()
    }
}
