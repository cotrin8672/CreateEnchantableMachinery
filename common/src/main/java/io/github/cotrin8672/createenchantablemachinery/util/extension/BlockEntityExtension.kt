package io.github.cotrin8672.createenchantablemachinery.util.extension

import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
import com.simibubi.create.infrastructure.config.AllConfigs
import io.github.cotrin8672.createenchantablemachinery.mixin.KineticBlockEntityMixin
import io.github.cotrin8672.createenchantablemachinery.mixin.SmartBlockEntityMixin
import io.github.cotrin8672.createenchantablemachinery.util.Side
import io.github.cotrin8672.createenchantablemachinery.util.interfaces.SideExecutor
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity

val BlockEntity.nonNullLevel: Level
    get() = checkNotNull(level)

fun SmartBlockEntity.smartBlockEntityTick() {
    if (!(this as SmartBlockEntityMixin).initialized && hasLevel()) {
        initialize()
        initialized = true
    }
    if (lazyTickCounter-- <= 0) {
        lazyTickCounter = lazyTickRate
        lazyTick()
    }

    forEachBehaviour(BlockEntityBehaviour::tick)
}

fun KineticBlockEntity.kineticBlockEntityTick() {
    if (!nonNullLevel.isClientSide && needsSpeedUpdate()) attachKinetics()
    smartBlockEntityTick()
    (this as KineticBlockEntityMixin).effects.tick()

    preventSpeedUpdate = 0
    if (nonNullLevel.isClientSide) {
        SideExecutor().runWhenOn(Side.CLIENT) { Runnable(this::tickAudio) }
        return
    }

    if (this.validationCountdown-- <= 0) {
        validationCountdown = AllConfigs.server().kinetics.kineticValidationFrequency.get()
    }

    if (flickerScore > 0) {
        flickerTally = flickerScore - 1
    }

    if (networkDirty) {
        if (hasNetwork()) orCreateNetwork.updateNetwork()
        networkDirty = false
    }
}

fun MechanicalMixerBlockEntity.mechanicalMixerBlockEntityTick() {
    if (basinRemoved) {
        basinRemoved = false
        if (!running) return
        runningTicks = 40
        running = false
        sendData()
        return
    }
    kineticBlockEntityTick()
}
