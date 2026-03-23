package io.github.cotrin8672.cem.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IItemExtension.class)
public interface IItemExtensionMixin {
    @Inject(method = "supportsEnchantment", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void cem$supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment, CallbackInfoReturnable<Boolean> cir) {
        if (!isCrushingWheel(stack)) return;
        if (enchantment.is(Enchantments.FORTUNE) || enchantment.is(Enchantments.SILK_TOUCH)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isBookEnchantable", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void cem$isBookEnchantable(ItemStack stack, ItemStack book, CallbackInfoReturnable<Boolean> cir) {
        if (!isCrushingWheel(stack)) return;
        if (containsFortuneOrSilk(book)) {
            cir.setReturnValue(true);
        }
    }

    private static boolean isCrushingWheel(ItemStack stack) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (id == null) return false;
        if (id.equals(ResourceLocation.fromNamespaceAndPath("create", "crushing_wheel"))) return true;
        if (id.equals(ResourceLocation.fromNamespaceAndPath("createenchantablemachinery", "enchantable_crushing_wheel"))) return true;
        return stack.is(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "create/crushing_wheels")));
    }

    private static boolean containsFortuneOrSilk(ItemStack stack) {
        ItemEnchantments stored = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        if (getEnchantmentLevel(stored, Enchantments.FORTUNE) > 0 || getEnchantmentLevel(stored, Enchantments.SILK_TOUCH) > 0) {
            return true;
        }
        ItemEnchantments direct = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        return getEnchantmentLevel(direct, Enchantments.FORTUNE) > 0 || getEnchantmentLevel(direct, Enchantments.SILK_TOUCH) > 0;
    }

    private static int getEnchantmentLevel(ItemEnchantments enchantments, ResourceKey<Enchantment> key) {
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            if (entry.getKey().is(key)) return entry.getIntValue();
        }
        return 0;
    }
}
