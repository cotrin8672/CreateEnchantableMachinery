package io.github.cotrin8672.cem.util

import net.minecraft.util.Mth
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * How Efficiency speeds up each machine. `eff` is the Efficiency level; level 0 always returns the input unchanged.
 */
object CemSpeeds {
    /** Processing machines (millstone, mixer, press, crushing wheels) gain 20% per level. */
    private fun processingFactor(eff: Int) = 1 + 0.2f * eff

    /** Stationary and contraption drills, saws, ploughs and rollers break blocks (1 + eff) times as fast. */
    @JvmStatic
    fun breakSpeed(speed: Float, eff: Int): Float = speed * (1 + eff)

    /** Millstone recipe duration in ticks. */
    @JvmStatic
    fun millingTime(time: Int, eff: Int): Int =
        if (eff <= 0) time else max(1, (time / processingFactor(eff)).roundToInt())

    /** Mixer processing duration, clamped like Create's NeoForge version does. */
    @JvmStatic
    fun mixingTicks(ticks: Int, eff: Int): Int =
        if (eff <= 0) ticks else Mth.clamp((ticks / processingFactor(eff)).toInt(), 1, 512)

    /**
     * Press head speed per tick. Always at least one faster than the base, so the bonus is not lost to int rounding
     * at low RPM; a stopped press stays stopped.
     */
    @JvmStatic
    fun pressRunningSpeed(speed: Int, eff: Int): Int =
        if (eff <= 0 || speed == 0) speed else max(speed + 1, ceil(speed * processingFactor(eff)).toInt())

    /** Crushing progress per tick; `eff` is the sum of both wheels. */
    @JvmStatic
    fun crushingSpeed(speed: Float, eff: Int): Float = if (eff <= 0) speed else speed * processingFactor(eff)

    /**
     * Spout filling countdown. The spout acts when the countdown reaches exactly 5, so it must start above that.
     */
    @JvmStatic
    fun spoutFillingTime(time: Int, eff: Int): Int = if (eff <= 0) time else max(6, time - 2 * eff)

    /** Fan processing time loses 10% per level, but never reaches zero. */
    @JvmStatic
    fun fanProcessingTime(time: Int, eff: Int): Int =
        if (eff <= 0 || time <= 0) time else max(1, (time * (1 - 0.1f * eff)).toInt())

    /** Saw cutting time loses 10% per level, down to 10% of the original. */
    @JvmStatic
    fun sawProcessingTime(time: Float, eff: Int): Float =
        if (eff <= 0) time else max(1f, time * max(0.1f, 1 - 0.1f * eff))
}
