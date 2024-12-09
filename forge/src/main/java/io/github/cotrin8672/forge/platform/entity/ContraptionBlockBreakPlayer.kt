package io.github.cotrin8672.forge.platform.entity

import com.mojang.authlib.GameProfile
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import io.github.cotrin8672.util.EnchantedItemFactory
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.EnchantmentInstance
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.util.FakePlayer
import java.util.*

class ContraptionBlockBreakPlayer
private constructor(
    level: ServerLevel,
    private var context: MovementContext?,
    private val heldItem: ItemStack = EnchantedItemFactory.getPickaxeItemStack(
        *EnchantmentHelper.getEnchantments(ItemStack.EMPTY.apply { tag = context?.blockEntityData })
            .map { EnchantmentInstance(it.key, it.value) }
            .toTypedArray()
    ),
) : FakePlayer(level, GameProfile(UUID.randomUUID(), "contraption_block_breaker")) {
    companion object {
        private val _contraptionBlockBreakPlayerList = mutableListOf<ContraptionBlockBreakPlayer>()

        fun unload(level: ServerLevel) {
            _contraptionBlockBreakPlayerList.removeIf { it.level() == level }
        }

        private lateinit var instance: ContraptionBlockBreakPlayer

        fun getBlockBreakerForMovementContext(
            level: ServerLevel,
            context: MovementContext?,
            heldItem: ItemStack = EnchantedItemFactory.getPickaxeItemStack(
                *EnchantmentHelper.getEnchantments(ItemStack.EMPTY.apply { tag = context?.blockEntityData })
                    .map { EnchantmentInstance(it.key, it.value) }
                    .toTypedArray()
            ),
        ): ContraptionBlockBreakPlayer {
            if (!Companion::instance.isInitialized) {
                instance = ContraptionBlockBreakPlayer(level, context, heldItem)
            } else {
                instance.setMovementContext(context)
            }

            return instance
        }
    }

    init {
        _contraptionBlockBreakPlayerList.add(this)
    }

    override fun isSpectator(): Boolean {
        return false
    }

    override fun isCreative(): Boolean {
        return false
    }

    override fun getLookAngle(): Vec3 {
        return context?.relativeMotion ?: Vec3.ZERO
    }

    override fun getMainHandItem(): ItemStack {
        return heldItem
    }

    private fun setMovementContext(context: MovementContext?) {
        this.context = context
    }
}
