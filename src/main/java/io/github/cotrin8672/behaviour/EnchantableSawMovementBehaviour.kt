package io.github.cotrin8672.behaviour

import com.mojang.authlib.GameProfile
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.kinetics.saw.SawMovementBehaviour
import com.simibubi.create.foundation.utility.BlockHelper
import com.simibubi.create.foundation.utility.TreeCutter
import io.github.cotrin8672.util.EnchantedItemFactory
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.BlockTags
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.EnchantmentInstance
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.util.FakePlayer
import java.util.*

class EnchantableSawMovementBehaviour : SawMovementBehaviour() {
    override fun destroyBlock(context: MovementContext, breakingPos: BlockPos) {
        val enchantments = EnchantmentHelper.getEnchantments(ItemStack.EMPTY.apply {
            tag = context.blockEntityData
        }).map { EnchantmentInstance(it.key, it.value) }
        val enchantedItem = EnchantedItemFactory.getPickaxeItemStack(*enchantments.toTypedArray())
        val fakePlayer = if (context.world is ServerLevel) {
            object : FakePlayer(context.world as ServerLevel, GameProfile(UUID.randomUUID(), "fake_player")) {
                override fun isSpectator(): Boolean {
                    return false
                }

                override fun isCreative(): Boolean {
                    return false
                }

                override fun getLookAngle(): Vec3 {
                    return context.relativeMotion
                }

                override fun getMainHandItem(): ItemStack {
                    return enchantedItem
                }

                override fun tick() {
                    super.tick()
                    this.setPos(context.position)
                }
            }
        } else null
        BlockHelper.destroyBlockAs(context.world, breakingPos, fakePlayer, enchantedItem, 1f) {
            this.dropItem(context, it)
        }
    }

    override fun onBlockBroken(context: MovementContext, pos: BlockPos?, brokenState: BlockState) {
        if (brokenState.`is`(BlockTags.LEAVES)) return

        val enchantments = EnchantmentHelper.getEnchantments(ItemStack.EMPTY.apply {
            tag = context.blockEntityData
        }).map { EnchantmentInstance(it.key, it.value) }
        val enchantedItem = EnchantedItemFactory.getPickaxeItemStack(*enchantments.toTypedArray())
        val fakePlayer = if (context.world is ServerLevel) {
            object : FakePlayer(context.world as ServerLevel, GameProfile(UUID.randomUUID(), "fake_player")) {
                override fun isSpectator(): Boolean {
                    return false
                }

                override fun isCreative(): Boolean {
                    return false
                }

                override fun getLookAngle(): Vec3 {
                    return context.relativeMotion
                }

                override fun getMainHandItem(): ItemStack {
                    return enchantedItem
                }

                override fun tick() {
                    super.tick()
                    this.setPos(context.position)
                }
            }
        } else null

        val dynamicTree = TreeCutter.findDynamicTree(brokenState.block, pos)
        if (dynamicTree.isPresent) {
            dynamicTree.get().destroyBlocks(context.world, fakePlayer) { stack, dropPos ->
                dropItemFromCutTree(context, stack, dropPos)
            }
            return
        }

        TreeCutter.findTree(context.world, pos).destroyBlocks(context.world, fakePlayer) { stack, dropPos ->
            dropItemFromCutTree(context, stack, dropPos)
        }
    }

    override fun getBlockBreakingSpeed(context: MovementContext): Float {
        val enchantments = EnchantmentHelper.getEnchantments(ItemStack.EMPTY.apply {
            tag = context.blockEntityData
        })
        return super.getBlockBreakingSpeed(context) * ((enchantments[Enchantments.BLOCK_EFFICIENCY] ?: 0) + 1)
    }
}
