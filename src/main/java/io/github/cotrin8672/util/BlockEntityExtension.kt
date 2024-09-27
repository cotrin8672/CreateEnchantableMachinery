package io.github.cotrin8672.util

import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
import com.simibubi.create.infrastructure.config.AllConfigs
import io.github.cotrin8672.mixin.BasinOperatingBlockEntityMixin
import io.github.cotrin8672.mixin.KineticBlockEntityMixin
import io.github.cotrin8672.mixin.SmartBlockEntityMixin
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.DistExecutor

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
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT) { Runnable(this::tickAudio) }
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

fun BasinOperatingBlockEntity.basinOperatingBlockEntityTick() {
    if (basinRemoved) {
        basinRemoved = false
        (this as BasinOperatingBlockEntityMixin).onBasinRemoved()
        sendData()
        return
    }
    kineticBlockEntityTick()
}