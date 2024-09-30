package io.github.cotrin8672.blockentity

import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity
import com.simibubi.create.content.processing.recipe.ProcessingRecipe
import com.simibubi.create.foundation.utility.Lang
import io.github.cotrin8672.util.mechanicalMixerBlockEntityTick
import io.github.cotrin8672.util.nonNullLevel
import joptsimple.internal.Strings
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.log2
import kotlin.math.pow

class EnchantableMechanicalMixerBlockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState,
    private val delegate: EnchantableBlockEntityDelegate = EnchantableBlockEntityDelegate(),
) : MechanicalMixerBlockEntity(type, pos, state), EnchantableBlockEntity by delegate {
    override fun tick() {
        mechanicalMixerBlockEntityTick()

        if (runningTicks >= 40) {
            running = false;
            runningTicks = 0;
            basinChecker.scheduleUpdate();
            return;
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

                    val efficiencyModifier = 1.3.pow(delegate.getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY))
                    processingTicks =
                        (Mth.clamp(log2(512 / speed) * ceil(recipeSpeed * 15f), 1f, 512f) / efficiencyModifier).toInt()

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
        for (instance in delegate.enchantmentInstances) {
            val level = instance.level
            Lang.text(Strings.repeat(' ', 0))
                .add(instance.enchantment.getFullname(level).copy())
                .forGoggles(tooltip)
        }
        return true
    }

    override fun read(compound: CompoundTag, clientPacket: Boolean) {
        delegate.enchantmentsTag = compound.getList(ItemStack.TAG_ENCH, Tag.TAG_COMPOUND.toInt())
        super.read(compound, clientPacket)
    }

    override fun write(compound: CompoundTag, clientPacket: Boolean) {
        compound.remove(ItemStack.TAG_ENCH)
        delegate.enchantmentsTag?.let { compound.put(ItemStack.TAG_ENCH, it) }
        super.write(compound, clientPacket)
    }
}
