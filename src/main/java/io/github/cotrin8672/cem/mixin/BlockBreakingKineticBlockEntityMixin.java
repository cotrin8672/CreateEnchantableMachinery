package io.github.cotrin8672.cem.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import com.simibubi.create.foundation.utility.BlockHelper;
import io.github.cotrin8672.cem.util.EnchantedItemFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;

@Mixin(value = BlockBreakingKineticBlockEntity.class, remap = false)
public class BlockBreakingKineticBlockEntityMixin {
    @WrapOperation(
            method = "onBlockBroken",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/foundation/utility/BlockHelper;destroyBlock("
                            + "Lnet/minecraft/world/level/Level;"
                            + "Lnet/minecraft/core/BlockPos;"
                            + "FLjava/util/function/Consumer;)V",
                    remap = false
            ),
            remap = false
    )
    private void cem$destroyWithEnchantments(Level world, BlockPos pos, float effectChance, Consumer<ItemStack> droppedItemCallback, Operation<Void> original) {
        var be = (BlockBreakingKineticBlockEntity) (Object) this;

        ItemEnchantments enchantments = be.components().getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

        if (enchantments.isEmpty()) {
            original.call(world, pos, effectChance, droppedItemCallback);
        }

        ItemStack tool = EnchantedItemFactory.INSTANCE.getToolItemStack(Items.NETHERITE_PICKAXE, enchantments);

        BlockHelper.destroyBlockAs(world, pos, null, tool, effectChance, droppedItemCallback);
    }
}
