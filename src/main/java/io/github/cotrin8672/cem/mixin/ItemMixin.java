package io.github.cotrin8672.cem.mixin;

import io.github.cotrin8672.cem.util.EnchantableBlockMapping;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "getEnchantmentValue", at = @At("HEAD"), cancellable = true)
    private void cem$getEnchantmentValue(CallbackInfoReturnable<Integer> cir) {
        Item item = (Item) (Object) this;
        if (item instanceof BlockItem blockItem && EnchantableBlockMapping.getOriginalBlocks().contains(blockItem.getBlock())) {
            cir.setReturnValue(14);
            cir.cancel();
        }
    }

    @Inject(method = "isEnchantable", at = @At("HEAD"), cancellable = true)
    private void cem$isEnchantable(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem() instanceof BlockItem blockItem && EnchantableBlockMapping.getOriginalBlocks().contains(blockItem.getBlock())) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
