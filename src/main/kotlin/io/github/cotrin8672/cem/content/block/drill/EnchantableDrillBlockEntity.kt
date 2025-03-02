package io.github.cotrin8672.cem.content.block.drill

import com.simibubi.create.content.kinetics.drill.DrillBlockEntity
import com.simibubi.create.foundation.utility.BlockHelper
import io.github.cotrin8672.cem.content.entity.BlockBreaker
import io.github.cotrin8672.cem.util.holderLookup
import net.createmod.catnip.math.VecHelper
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3

class EnchantableDrillBlockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState,
) : DrillBlockEntity(type, pos, state) {
    private val fakePlayer by lazy {
        if (this.level is ServerLevel)
            BlockBreaker(this.level as ServerLevel, this@EnchantableDrillBlockEntity) else null
    }

    override fun getBreakSpeed(): Float {
        val efficiency = holderLookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.EFFICIENCY)
        val enchantmentComponent = this.components().get(DataComponents.ENCHANTMENTS)
        val efficiencyLevel = enchantmentComponent?.getLevel(efficiency) ?: 0
        return super.getBreakSpeed() * (efficiencyLevel + 1)
    }

    override fun canBreak(stateToBreak: BlockState, blockHardness: Float): Boolean {
        return isBreakable(stateToBreak, blockHardness)
    }

    override fun onBlockBroken(stateToBreak: BlockState?) {
        val nonNullLevel = checkNotNull(level)
        val vec = VecHelper.offsetRandomly(VecHelper.getCenterOf(breakingPos), nonNullLevel.random, .125f)
        BlockHelper.destroyBlockAs(
            nonNullLevel,
            breakingPos,
            fakePlayer,
            fakePlayer?.mainHandItem,
            1f
        ) { stack: ItemStack ->
            if (stack.isEmpty) return@destroyBlockAs
            if (!nonNullLevel.gameRules.getBoolean(GameRules.RULE_DOBLOCKDROPS)) return@destroyBlockAs
            // if (nonNullLevel.restoringBlockSnapshots) return@destroyBlockAs TODO: Forgeのみの処理

            val entity = ItemEntity(nonNullLevel, vec.x, vec.y, vec.z, stack)
            entity.setDefaultPickUpDelay()
            entity.deltaMovement = Vec3.ZERO
            nonNullLevel.addFreshEntity(entity)
        }
    }

    override fun addToGoggleTooltip(tooltip: MutableList<Component>, isPlayerSneaking: Boolean): Boolean {
        return super.addToGoggleTooltip(tooltip, isPlayerSneaking)
//        val enchantments = components().get(DataComponents.ENCHANTMENTS) ?: return result
//        for (instance in enchantments.entrySet()) {
//            CreateLang.text(Strings.repeat(' ', 0))
//                .add(getFullname(instance.key, instance.intValue))
//                .forGoggles(tooltip)
//        }
//        return true
    }
}
