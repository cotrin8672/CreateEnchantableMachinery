package io.github.cotrin8672.mixin;

import com.mlib.enchantments.CustomEnchantment;
import io.github.cotrin8672.block.EnchantableBlock;
import io.github.cotrin8672.util.EnchantableBlockMapping;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CustomEnchantment.class)
public class CustomEnchantmentMixin {
    @Inject(
            method = "canEnchant",
            at = @At("HEAD"),
            cancellable = true
    )
    private void createenchantablemachinery$canEnchant(
            ItemStack itemStack,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (itemStack.getItem() instanceof BlockItem blockItem) {
            Block altBlock = EnchantableBlockMapping.getAlternativeBlock(blockItem.getBlock());
            if (altBlock != null) {
                cir.setReturnValue(((EnchantableBlock) altBlock).canApply((Enchantment) (Object) this));
                cir.cancel();
            }
        }
    }
}
