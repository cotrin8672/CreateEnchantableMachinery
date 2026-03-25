package io.github.cotrin8672.cem.mixin;

import io.github.cotrin8672.cem.util.EnchantableBlockMapping;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    private static final TagKey<Item> CEM_ENCHANTABLE_BLOCKS_TAG =
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("createenchantablemachinery", "enchantable_blocks"));

    @Inject(method = "getEnchantmentValue", at = @At("HEAD"), cancellable = true)
    private void cem$getEnchantmentValue(CallbackInfoReturnable<Integer> cir) {
        Item item = (Item) (Object) this;
        if (item instanceof BlockItem blockItem && isCemEnchantableBlockItem(new ItemStack(item), blockItem)) {
            cir.setReturnValue(14);
        }
    }

    @Inject(method = "isEnchantable", at = @At("HEAD"), cancellable = true)
    private void cem$isEnchantable(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem() instanceof BlockItem blockItem && isCemEnchantableBlockItem(stack, blockItem)) {
            cir.setReturnValue(true);
        }
    }

    private static boolean isCemEnchantableBlockItem(ItemStack stack, BlockItem blockItem) {
        if (stack.is(CEM_ENCHANTABLE_BLOCKS_TAG)) return true;
        return EnchantableBlockMapping.getOriginalBlocks().contains(blockItem.getBlock())
                || EnchantableBlockMapping.getEnchantableBlocks().contains(blockItem.getBlock());
    }
}
