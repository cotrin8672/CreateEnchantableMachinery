package io.github.cotrin8672.cem.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.create.content.kinetics.saw.SawBlockEntity;
import com.zurrtum.create.content.kinetics.saw.TreeCutter;
import com.zurrtum.create.content.processing.recipe.ProcessingInventory;
import io.github.cotrin8672.cem.util.CemEnchantments;
import io.github.cotrin8672.cem.util.CemSpeeds;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.BiConsumer;

/**
 * Felling a whole tree uses the enchanted tool too, not just the first block, and Efficiency shortens cutting.
 */
@Mixin(SawBlockEntity.class)
public abstract class SawBlockEntityMixin {
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
        Operation<Void> original
    ) {
        ItemEnchantments enchantments = CemEnchantments.of((SawBlockEntity) (Object) this);
        if (enchantments.isEmpty()) {
            original.call(tree, level, entity, drop);
            return;
        }
        tree.destroyBlocks(level, CemEnchantments.createTool(enchantments), null, drop);
    }

    /** The second write of {@code remainingTime} in {@code start} sets the recipe's duration. */
    @WrapOperation(
        method = "start(Lnet/minecraft/world/item/ItemStack;)V",
        at = @At(
            value = "FIELD",
            target = "Lcom/zurrtum/create/content/processing/recipe/ProcessingInventory;remainingTime:F",
            opcode = Opcodes.PUTFIELD,
            ordinal = 1
        )
    )
    private void cem$shortenCutting(ProcessingInventory inventory, float time, Operation<Void> original) {
        original.call(inventory, CemSpeeds.sawProcessingTime(time, CemEnchantments.efficiency((SawBlockEntity) (Object) this)));
    }
}
