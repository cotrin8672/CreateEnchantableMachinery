package io.github.cotrin8672.cem.content.block.mixer

import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity
import com.simibubi.create.content.processing.recipe.ProcessingRecipe
import com.simibubi.create.foundation.utility.CreateLang
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntityDelegate
import io.github.cotrin8672.cem.util.basinOperatingBlockEntityTick
import io.github.cotrin8672.cem.util.holderLookup
import io.github.cotrin8672.cem.util.nonNullLevel
import joptsimple.internal.Strings
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.world.item.enchantment.Enchantment.getFullname
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.log2

class EnchantableMechanicalMixerBlockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState,
) : MechanicalMixerBlockEntity(type, pos, state), EnchantableBlockEntity by EnchantableBlockEntityDelegate() {
    override fun tick() {
        basinOperatingBlockEntityTick()

        if (runningTicks >= 40) {
            running = false
            runningTicks = 0
            basinChecker.scheduleUpdate()
            return
        }

        val speed = abs(speed)
        if (running && level != null) {
            if (nonNullLevel.isClientSide && runningTicks == 20) renderParticles()

            if ((!nonNullLevel.isClientSide || isVirtual) && runningTicks == 20) {
                if (processingTicks < 0) {
                    var recipeSpeed = 1f
                    if (currentRecipe is ProcessingRecipe) {
                        val t = (currentRecipe as ProcessingRecipe<*>).processingDuration
                        if (t != 0) recipeSpeed = t / 100f
                    }

                    val efficiency = holderLookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.EFFICIENCY)
                    val efficiencyModifier = 1 + 0.2 * getEnchantmentLevel(efficiency)
                    processingTicks =
                        Mth.clamp((log2(512 / speed) * ceil(recipeSpeed * 15) / efficiencyModifier).toInt(), 1, 512)

                    if (basin.isPresent) {
                        val tanks = basin.get().tanks
                        if (!tanks.first.isEmpty || !tanks.second.isEmpty) {
                            nonNullLevel.playSound(
                                null, worldPosition,
                                SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT,
                                SoundSource.BLOCKS,
                                0.75f,
                                if (speed < 65) 0.75f else 1.5f
                            )
                        }
                    }
                } else {
                    processingTicks--
                    if (processingTicks == 0) {
                        runningTicks++
                        processingTicks = -1
                        applyBasinRecipe()
                        sendData()
                    }
                }
            }
            if (runningTicks != 20) {
                runningTicks++
            }
        }
    }

    override fun addToGoggleTooltip(tooltip: MutableList<Component>, isPlayerSneaking: Boolean): Boolean {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking)

        if (getEnchantments().entrySet().isEmpty()) return false
        for (instance in getEnchantments().entrySet()) {
            CreateLang.text(Strings.repeat(' ', 0))
                .add(getFullname(instance.key, instance.intValue))
                .forGoggles(tooltip)
        }
        return true
    }

    override fun read(compound: CompoundTag, registries: HolderLookup.Provider, clientPacket: Boolean) {
        readEnchantments(compound, registries)
        super.read(compound, registries, clientPacket)
    }

    override fun write(compound: CompoundTag, registries: HolderLookup.Provider, clientPacket: Boolean) {
        writeEnchantments(compound, registries)
        super.write(compound, registries, clientPacket)
    }
}
