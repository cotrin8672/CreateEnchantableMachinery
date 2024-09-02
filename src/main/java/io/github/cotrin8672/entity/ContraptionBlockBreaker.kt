package io.github.cotrin8672.entity

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

class ContraptionBlockBreaker(
    level: ServerLevel,
    private var context: MovementContext?,
    private val heldItem: ItemStack = EnchantedItemFactory.getPickaxeItemStack(
        *EnchantmentHelper.getEnchantments(ItemStack.EMPTY.apply { tag = context?.blockEntityData })
            .map { EnchantmentInstance(it.key, it.value) }
            .toTypedArray()
    ),
) : FakePlayer(level, GameProfile(UUID.randomUUID(), "contraption_block_breaker")) {
    companion object {
        private val _contraptionBlockBreakerList = mutableListOf<ContraptionBlockBreaker>()

        fun unload(level: ServerLevel) {
            _contraptionBlockBreakerList.removeIf { it.level() == level }
        }

        private lateinit var instance: ContraptionBlockBreaker

        fun getBlockBreakerForMovementContext(
            level: ServerLevel,
            context: MovementContext,
            heldItem: ItemStack = EnchantedItemFactory.getPickaxeItemStack(
                *EnchantmentHelper.getEnchantments(ItemStack.EMPTY.apply { tag = context.blockEntityData })
                    .map { EnchantmentInstance(it.key, it.value) }
                    .toTypedArray()
            ),
        ): ContraptionBlockBreaker {
            if (!::instance.isInitialized) {
                instance = ContraptionBlockBreaker(level, context, heldItem)
            } else {
                instance.setMovementContext(context)
            }

            return instance
        }
    }

    init {
        _contraptionBlockBreakerList.add(this)
        this.lookAngle
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

    private fun setMovementContext(context: MovementContext) {
        this.context = context
    }
}
