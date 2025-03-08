package io.github.cotrin8672.cem.content.block.fan

import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour.TransportedResult
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack
import com.simibubi.create.content.kinetics.fan.AirCurrent
import com.simibubi.create.content.kinetics.fan.AirCurrentSound
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource
import com.simibubi.create.foundation.advancement.AllAdvancements
import io.github.cotrin8672.cem.mixin.ServerGamePacketListenerImplMixin
import net.createmod.catnip.math.VecHelper
import net.createmod.catnip.platform.CatnipServices
import net.minecraft.client.Minecraft
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import kotlin.math.abs

class EnchantableAirCurrent(source: IAirCurrentSource, efficiencyLevel: Int) : AirCurrent(source) {
    class AirCurrentSoundKt(event: SoundEvent, pitch: Float) : AirCurrentSound(event, pitch)

    private val fanProcessing = EnchantableFanProcessing(efficiencyLevel)

    object Client {
        private var isClientPlayerInAirCurrent = false

        private var flyingSound: AirCurrentSound? = null

        fun enableClientPlayerSound(e: Entity, maxVolume: Float) {
            if (e !== Minecraft.getInstance().getCameraEntity()) return

            isClientPlayerInAirCurrent = true

            val pitch = Mth.clamp(e.deltaMovement.length() * .5f, .5, 2.0).toFloat()

            if (flyingSound == null || flyingSound!!.isStopped) {
                flyingSound = AirCurrentSoundKt(SoundEvents.ELYTRA_FLYING, pitch)
                flyingSound?.let { Minecraft.getInstance().soundManager.play(it) }
            }
            flyingSound?.pitch = pitch
            flyingSound?.fadeIn(maxVolume)
        }

        fun tickClientPlayerSounds() {
            if (!isClientPlayerInAirCurrent && flyingSound != null)
                if (flyingSound?.isFaded == true)
                    flyingSound?.stopSound()
                else flyingSound?.fadeOut()
            isClientPlayerInAirCurrent = false
        }
    }

    override fun tickAffectedEntities(world: Level?) {
        val iterator = caughtEntities.iterator()
        while (iterator.hasNext()) {
            val entity = iterator.next()
            if (!entity.isAlive || !entity.boundingBox.intersects(bounds) || isPlayerCreativeFlying(entity)) {
                iterator.remove()
                continue
            }

            val flow = (if (pushing) direction else direction.opposite).normal
            val speed = abs(source.speed.toDouble()).toFloat()
            val sneakModifier = if (entity.isShiftKeyDown) 4096f else 512f
            val entityDistance = VecHelper.alignedDistanceToFace(entity.position(), source.airCurrentPos, direction)
            // entityDistanceOld should be removed eventually. Remember that entityDistanceOld cannot be 0 while entityDistance can,
            // so division by 0 must be avoided.
            val entityDistanceOld = entity.position().distanceTo(VecHelper.getCenterOf(source.airCurrentPos))
            val acceleration = (speed / sneakModifier / (entityDistanceOld / maxDistance)).toFloat()
            val previousMotion = entity.deltaMovement
            val maxAcceleration = 5.0

            val xIn = Mth.clamp(flow.x * acceleration - previousMotion.x, -maxAcceleration, maxAcceleration)
            val yIn = Mth.clamp(flow.y * acceleration - previousMotion.y, -maxAcceleration, maxAcceleration)
            val zIn = Mth.clamp(flow.z * acceleration - previousMotion.z, -maxAcceleration, maxAcceleration)

            entity.deltaMovement = previousMotion.add(Vec3(xIn, yIn, zIn).scale((1.0 / 8.0)))
            entity.fallDistance = 0f
            if (CatnipServices.PLATFORM.env.isClient) {
                Client.enableClientPlayerSound(entity, Mth.clamp(speed / 128f * .4f, 0.01f, .4f))
            }

            if (entity is ServerPlayer) {
                val connection = entity.connection as ServerGamePacketListenerImplMixin
                connection.setAboveGroundTickCount(0)
            }

            val processingType = getTypeAt(entityDistance.toFloat())

            if (processingType === null) continue

            if (entity is ItemEntity) {
                if (world != null && world.isClientSide) {
                    processingType.spawnProcessingParticles(world, entity.position())
                    continue
                }
                if (fanProcessing.canProcess(entity, processingType))
                    if (fanProcessing.applyProcessing(
                            entity,
                            processingType
                        ) && source is EnchantableEncasedFanBlockEntity
                    )
                        (source as EnchantableEncasedFanBlockEntity).award(AllAdvancements.FAN_PROCESSING)
                continue
            }

            if (world != null) processingType.affectEntity(entity, world)
        }
    }

    override fun tickAffectedHandlers() {
        for (pair in affectedItemHandlers) {
            val handler = pair.key
            val world = handler.world
            val processingType = pair.right ?: continue

            handler.handleProcessingOnAllItems { transported: TransportedItemStack ->
                if (world.isClientSide) {
                    processingType.spawnProcessingParticles(world, handler.getWorldPositionOf(transported))
                    return@handleProcessingOnAllItems TransportedResult.doNothing()
                }
                val applyProcessing = fanProcessing.applyProcessing(transported, world, processingType)
                if (!applyProcessing.doesNothing() && source is EnchantableEncasedFanBlockEntity)
                    (source as EnchantableEncasedFanBlockEntity).award(AllAdvancements.FAN_PROCESSING)
                applyProcessing
            }
        }
    }
}
