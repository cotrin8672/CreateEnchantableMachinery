package io.github.cotrin8672.cem.util

import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
import com.simibubi.create.infrastructure.config.AllConfigs
import io.github.cotrin8672.cem.mixin.KineticBlockEntityMixin
import io.github.cotrin8672.cem.mixin.SmartBlockEntityMixin
import net.createmod.catnip.platform.CatnipServices
import net.minecraft.core.HolderLookup
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity


val BlockEntity.nonNullLevel: Level
    get() = checkNotNull(level)

fun <T> BlockEntity.holderLookup(registryKey: ResourceKey<out Registry<out T>>): HolderLookup<T> =
    checkNotNull(level).holderLookup(registryKey)

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
        CatnipServices.PLATFORM.executeOnClientOnly { Runnable(this::tickAudio) }
        return
    }

    if (this.validationCountdown-- <= 0) {
        validationCountdown = AllConfigs.server().kinetics.kineticValidationFrequency.get()
        validateKinetics()
    }

    if (flickerScore > 0) {
        flickerTally = flickerScore - 1
    }

    if (networkDirty) {
        if (hasNetwork()) orCreateNetwork.updateNetwork()
        networkDirty = false
    }
}

fun KineticBlockEntity.validateKinetics() {
    if (hasSource()) {
        if (!hasNetwork()) {
            removeSource()
            return
        }

        if (source == null) return
        if (!nonNullLevel.isLoaded(source)) return

        val blockEntity = nonNullLevel.getBlockEntity(source)
        val sourceBE = blockEntity as? KineticBlockEntity

        if (sourceBE == null || sourceBE.speed == 0f) {
            removeSource()
            detachKinetics()
            return
        }

        return
    }

    if (speed != 0f) {
        if (generatedSpeed == 0f)
            speed = 0f
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
