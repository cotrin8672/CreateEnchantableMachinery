package io.github.cotrin8672.mixin;

import io.github.cotrin8672.util.EnchantableBlockMapping;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow
    public abstract Item getItem();

    @Inject(method = "setRepairCost(I)V", at = @At("HEAD"), cancellable = true)
    public void createenchantablemachinery$setRepairCost(int value, CallbackInfo callbackInfo) {
        if (EnchantableBlockMapping.getOriginBlockList().stream().map(Block::asItem).toList().contains(getItem())) {
            callbackInfo.cancel();
        }
    }
}
