package io.github.cotrin8672.blockentity.`fun`

import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour.TransportedResult
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack
import com.simibubi.create.content.kinetics.fan.AirCurrent
import com.simibubi.create.content.kinetics.fan.AirCurrentSound
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes
import com.simibubi.create.foundation.advancement.AllAdvancements
import com.simibubi.create.foundation.utility.VecHelper
import io.github.cotrin8672.mixin.ServerGamePacketListenerImplMixin
import net.minecraft.client.Minecraft
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.fml.DistExecutor
import kotlin.math.abs

class EnchantableAirCurrent(source: IAirCurrentSource, efficiencyLevel: Int) : AirCurrent(source) {
    class AirCurrentSoundKt(event: SoundEvent, pitch: Float) : AirCurrentSound(event, pitch)

    private val fanProcessing = EnchantableFanProcessing(efficiencyLevel)

    companion object {
        private var isClientPlayerInAirCurrent = false

        @OnlyIn(Dist.CLIENT)
        private var flyingSound: AirCurrentSound? = null

        @OnlyIn(Dist.CLIENT)
        private fun enableClientPlayerSound(entity: Entity, maxVolume: Float) {
            if (entity !== Minecraft.getInstance().getCameraEntity()) return

            isClientPlayerInAirCurrent = true

            val pitch = Mth.clamp(entity.deltaMovement.length() * .5f, .5, 2.0).toFloat()

            if (flyingSound == null || flyingSound!!.isStopped) {
                flyingSound = AirCurrentSoundKt(SoundEvents.ELYTRA_FLYING, pitch)
                Minecraft.getInstance().soundManager.play(flyingSound!!)
            }
            flyingSound?.pitch = pitch
            flyingSound?.fadeIn(maxVolume)
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
            val maxAcceleration = 5f

            val xIn = Mth.clamp(
                flow.x * acceleration - previousMotion.x,
                -maxAcceleration.toDouble(),
                maxAcceleration.toDouble()
            )
            val yIn = Mth.clamp(
                flow.y * acceleration - previousMotion.y,
                -maxAcceleration.toDouble(),
                maxAcceleration.toDouble()
            )
            val zIn = Mth.clamp(
                flow.z * acceleration - previousMotion.z,
                -maxAcceleration.toDouble(),
                maxAcceleration.toDouble()
            )

            entity.deltaMovement = previousMotion.add(Vec3(xIn, yIn, zIn).scale((1 / 8f).toDouble()))
            entity.fallDistance = 0f
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT) {
                Runnable {
                    enableClientPlayerSound(
                        entity,
                        Mth.clamp(speed / 128f * .4f, 0.01f, .4f)
                    )
                }
            }

            if (entity is ServerPlayer) {
                val connection = entity.connection as ServerGamePacketListenerImplMixin
                connection.setAboveGroundTickCount(0)
            }

            val processingType = getTypeAt(entityDistance.toFloat())

            if (processingType === AllFanProcessingTypes.NONE) continue

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
            val processingType = pair.right

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
