package io.github.cotrin8672.cem.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import com.zurrtum.create.foundation.utility.BlockHelper;
import io.github.cotrin8672.cem.util.CemEnchantments;
import io.github.cotrin8672.cem.util.CemSpeeds;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;

/**
 * Stationary drills and saws: Efficiency speeds up breaking, and blocks are broken with a tool carrying the
 * machine's enchantments.
 */
@Mixin(BlockBreakingKineticBlockEntity.class)
public abstract class BlockBreakingKineticBlockEntityMixin {
    @ModifyReturnValue(method = "getBreakSpeed", at = @At("RETURN"))
    private float cem$applyEfficiency(float speed) {
        return CemSpeeds.breakSpeed(speed, CemEnchantments.efficiency((BlockEntity) (Object) this));
    }

    @WrapOperation(
        method = "onBlockBroken",
        at = @At(
            value = "INVOKE",
            target = "Lcom/zurrtum/create/foundation/utility/BlockHelper;destroyBlock(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;FLjava/util/function/Consumer;)V"
        )
    )
    private void cem$destroyWithEnchantedTool(
        Level level,
        BlockPos pos,
        float effectChance,
        Consumer<ItemStack> droppedItemCallback,
        Operation<Void> original
    ) {
        ItemEnchantments enchantments = CemEnchantments.of((BlockEntity) (Object) this);
        if (enchantments.isEmpty()) {
            original.call(level, pos, effectChance, droppedItemCallback);
            return;
        }
        BlockHelper.destroyBlockAs(level, pos, null, CemEnchantments.createTool(enchantments), effectChance, droppedItemCallback);
    }
}
