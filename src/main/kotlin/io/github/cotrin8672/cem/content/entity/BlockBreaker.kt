package io.github.cotrin8672.cem.content.entity

import com.mojang.authlib.GameProfile
import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity
import io.github.cotrin8672.cem.util.EnchantedItemFactory
import net.minecraft.core.component.DataComponents
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.common.util.FakePlayer
import java.util.*

class BlockBreaker(
    level: ServerLevel,
    blockEntity: BlockBreakingKineticBlockEntity,
) : FakePlayer(level, GameProfile(UUID.randomUUID(), "block_breaker")) {
    companion object {
        private val _blockBreakPlayerList = mutableListOf<BlockBreaker>()

        fun unload(level: ServerLevel) {
            _blockBreakPlayerList.removeIf { it.level() == level }
        }
    }

    init {
        _blockBreakPlayerList.add(this)
    }

    private val facing = level.getBlockState(blockEntity.blockPos).getValue(BlockStateProperties.FACING)
    private val enchantedItemStack = EnchantedItemFactory.getPickaxeItemStack(
        blockEntity.components().get(DataComponents.ENCHANTMENTS)?.entrySet() ?: setOf()
    )

    init {
        setPos(blockEntity.blockPos.center)
    }

    override fun isSpectator(): Boolean {
        return false
    }

    override fun isCreative(): Boolean {
        return false
    }

    override fun getLookAngle(): Vec3 {
        return Vec3.atCenterOf(facing.normal)
    }

    override fun getMainHandItem(): ItemStack {
        return enchantedItemStack
    }
}
