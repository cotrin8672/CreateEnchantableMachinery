package io.github.cotrin8672.cem.content.block.press

import com.simibubi.create.AllBlocks
import com.simibubi.create.AllSoundEvents
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour
import com.simibubi.create.content.kinetics.press.PressingBehaviour
import com.simibubi.create.content.kinetics.press.PressingBehaviour.PressingBehaviourSpecifics
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity
import io.github.cotrin8672.cem.mixin.BlockEntityBehaviourMixin
import io.github.cotrin8672.cem.util.holderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.util.Mth
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.phys.AABB
import kotlin.math.abs
import kotlin.math.pow

class EnchantablePressBehaviour<T>(
    private val be: T,
) : PressingBehaviour(be) where T : PressingBehaviourSpecifics, T : SmartBlockEntity, T : EnchantableBlockEntity {
    private var entityScanCooldown = ENTITY_SCAN
    private var lazyTickCounter: Int
        get() = (this as BlockEntityBehaviourMixin).lazyTickCounter
        set(value) {
            (this as BlockEntityBehaviourMixin).lazyTickCounter = value
        }
    private val lazyTickRate: Int
        get() = (this as BlockEntityBehaviourMixin).lazyTickRate

    override fun getRenderedHeadOffset(partialTicks: Float): Float {
        if (!running) return 0f
        val runningTicks = abs(runningTicks)
        val ticks = Mth.lerp(partialTicks, prevRunningTicks.toFloat(), runningTicks.toFloat())
        if (runningTicks < (CYCLE * 2) / 3)
            return Mth.clamp((ticks / CYCLE * 2).pow(3f), 0f, 1f)
        return Mth.clamp((CYCLE - ticks) / CYCLE * 3f, 0f, 1f)
    }

    override fun tick() {
        if (lazyTickCounter-- <= 0) {
            lazyTickCounter = lazyTickRate
            lazyTick()
        }

        val level = world
        val worldPosition = pos

        if (!running || level == null) {
            if (level != null && !level.isClientSide) {
                if (specifics.kineticSpeed == 0f) return
                if (entityScanCooldown > 0) entityScanCooldown--
                if (entityScanCooldown <= 0) {
                    entityScanCooldown = ENTITY_SCAN

                    if (get(level, worldPosition.below(2), TransportedItemStackHandlerBehaviour.TYPE) != null) return
                    if (AllBlocks.BASIN.has(level.getBlockState(worldPosition.below(2)))) return

                    for (itemEntity in level.getEntitiesOfClass(
                        ItemEntity::class.java,
                        AABB(worldPosition.below()).deflate(0.125)
                    )) {
                        if (!itemEntity.isAlive || !itemEntity.onGround()) continue
                        if (!specifics.tryProcessInWorld(itemEntity, true)) continue
                        start(Mode.WORLD)
                        return
                    }
                }
            }
            return
        }

        if (level.isClientSide && runningTicks == -CYCLE / 2) {
            prevRunningTicks = CYCLE / 2
            return
        }

        if (runningTicks == CYCLE / 2 && specifics.kineticSpeed != 0f) {
            if (inWorld()) applyInWorld()
            if (onBasin()) applyOnBasin()

            if (level.getBlockState(worldPosition.below(2)).soundType === SoundType.WOOL)
                AllSoundEvents.MECHANICAL_PRESS_ACTIVATION_ON_BELT.playOnServer(level, worldPosition)
            else
                AllSoundEvents.MECHANICAL_PRESS_ACTIVATION.playOnServer(
                    level,
                    worldPosition,
                    0.5f,
                    0.75f + (abs(specifics.kineticSpeed) / 1024f)
                )

            if (!level.isClientSide) blockEntity.sendData()
        }

        if (!level.isClientSide && runningTicks > CYCLE) {
            finished = true
            running = false
            particleItems.clear()
            specifics.onPressingCompleted()
            blockEntity.sendData()
            return
        }

        prevRunningTicks = runningTicks
        val efficiency = be.holderLookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.EFFICIENCY)
        val efficiencyModifier = 1 + 0.2f * be.getEnchantmentLevel(efficiency)
        runningTicks += (runningTickSpeed * efficiencyModifier).toInt()
        if (CYCLE / 2 in (prevRunningTicks + 1)..runningTicks) {
            runningTicks = CYCLE / 2
            if (level.isClientSide && !blockEntity.isVirtual) runningTicks = -(CYCLE / 2)
        }
    }
}
