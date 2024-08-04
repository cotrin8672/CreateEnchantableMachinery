package io.github.cotrin8672.entity

import com.mojang.authlib.GameProfile
import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity
import io.github.cotrin8672.blockentity.EnchantableBlockEntity
import io.github.cotrin8672.util.EnchantedItemFactory
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.util.FakePlayer
import java.util.*

class BlockBreaker(
    level: ServerLevel,
    blockEntity: BlockBreakingKineticBlockEntity,
) : FakePlayer(level, GameProfile(UUID.randomUUID(), "block_breaker")) {
    private val facing = level.getBlockState(blockEntity.blockPos).getValue(BlockStateProperties.FACING)
    private val enchantedItemStack = if (blockEntity is EnchantableBlockEntity) {
        EnchantedItemFactory.getPickaxeItemStack(*blockEntity.getEnchantments().toTypedArray())
    } else ItemStack(Items.NETHERITE_PICKAXE)

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
