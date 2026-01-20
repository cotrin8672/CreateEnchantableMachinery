package io.github.cotrin8672.cem

import com.simibubi.create.foundation.data.CreateRegistrate

object Cem {
    const val MOD_ID = "createenchantablemachinery"
    val REGISTRATE: CreateRegistrate = CreateRegistrate.create(MOD_ID)

    fun registrate() = REGISTRATE
}
