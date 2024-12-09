package io.github.cotrin8672.createenchantablemachinery.mixin;

import io.github.cotrin8672.createenchantablemachinery.content.block.EnchantableBlock;
import io.github.cotrin8672.createenchantablemachinery.util.EnchantableBlockMapping;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
    @Inject(
            method = "canEnchant",
            at = @At("HEAD"),
            cancellable = true
    )
    private void createenchantablemachinery$canEnchant(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            Block altBlock = EnchantableBlockMapping.getAlternativeBlock(blockItem.getBlock());
            if (altBlock != null) {
                if (((EnchantableBlock) altBlock).canApply((Enchantment) (Object) this)) {
                    cir.setReturnValue(true);
                    cir.cancel();
                }
            }
        }
    }
}
