package io.github.cotrin8672.cem.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.zurrtum.create.content.contraptions.behaviour.MovementContext;
import com.zurrtum.create.content.kinetics.base.BlockBreakingMovementBehaviour;
import com.zurrtum.create.foundation.utility.BlockHelper;
import io.github.cotrin8672.cem.util.CemEnchantments;
import io.github.cotrin8672.cem.util.CemSpeeds;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;

/**
 * Drills, saws, ploughs and rollers on contraptions.
 */
@Mixin(BlockBreakingMovementBehaviour.class)
public abstract class BlockBreakingMovementBehaviourMixin {
    @WrapOperation(
        method = "tickBreaker",
        at = @At(
            value = "INVOKE",
            target = "Lcom/zurrtum/create/content/kinetics/base/BlockBreakingMovementBehaviour;getBlockBreakingSpeed(Lcom/zurrtum/create/content/contraptions/behaviour/MovementContext;)F"
        )
    )
    private float cem$applyEfficiency(BlockBreakingMovementBehaviour behaviour, MovementContext context, Operation<Float> original) {
        return CemSpeeds.breakSpeed(original.call(behaviour, context), CemEnchantments.efficiency(context));
    }

    @WrapOperation(
        method = "destroyBlock",
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
        Operation<Void> original,
        @Local(argsOnly = true) MovementContext context
    ) {
        ItemEnchantments enchantments = CemEnchantments.of(context);
        if (enchantments.isEmpty()) {
            original.call(level, pos, effectChance, droppedItemCallback);
            return;
        }
        BlockHelper.destroyBlockAs(level, pos, null, CemEnchantments.createTool(enchantments), effectChance, droppedItemCallback);
    }
}
