package io.github.cotrin8672.cem.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.zurrtum.create.content.contraptions.behaviour.MovementContext;
import com.zurrtum.create.content.kinetics.saw.SawMovementBehaviour;
import com.zurrtum.create.content.kinetics.saw.TreeCutter;
import io.github.cotrin8672.cem.util.CemEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.BiConsumer;

@Mixin(SawMovementBehaviour.class)
public abstract class SawMovementBehaviourMixin {
    @WrapOperation(
        method = "onBlockBroken",
        at = @At(
            value = "INVOKE",
            target = "Lcom/zurrtum/create/content/kinetics/saw/TreeCutter$Tree;destroyBlocks(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/BiConsumer;)V"
        )
    )
    private void cem$cutTreeWithEnchantedTool(
        TreeCutter.Tree tree,
        Level level,
        LivingEntity entity,
        BiConsumer<BlockPos, ItemStack> drop,
        Operation<Void> original,
        @Local(argsOnly = true) MovementContext context
    ) {
        ItemEnchantments enchantments = CemEnchantments.of(context);
        if (enchantments.isEmpty()) {
            original.call(tree, level, entity, drop);
            return;
        }
        tree.destroyBlocks(level, CemEnchantments.createTool(enchantments), null, drop);
    }
}
