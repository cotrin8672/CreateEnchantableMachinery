package io.github.cotrin8672.cem.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
        if (isCrushingWheel(stack) && (enchantment.is(Enchantments.FORTUNE) || enchantment.is(Enchantments.SILK_TOUCH))) {
            cir.setReturnValue(true);
            return;
        }

        ItemStack baseMachine = getBaseCreateMachineStack(stack);
        if (!baseMachine.isEmpty()) {
            IItemExtension baseExtension = (IItemExtension) baseMachine.getItem();
            cir.setReturnValue(baseExtension.supportsEnchantment(baseMachine, enchantment));
        }
    }

    @Inject(method = "isBookEnchantable", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void cem$isBookEnchantable(ItemStack stack, ItemStack book, CallbackInfoReturnable<Boolean> cir) {
        if (isCrushingWheel(stack) && containsFortuneOrSilk(book)) {
            cir.setReturnValue(true);
            return;
        }

        ItemStack baseMachine = getBaseCreateMachineStack(stack);
        if (!baseMachine.isEmpty()) {
            IItemExtension baseExtension = (IItemExtension) baseMachine.getItem();
            cir.setReturnValue(baseExtension.isBookEnchantable(baseMachine, book));
        }
    }

    private static boolean isCrushingWheel(ItemStack stack) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (id == null) return false;
        if (id.equals(ResourceLocation.fromNamespaceAndPath("create", "crushing_wheel"))) return true;
        if (id.equals(ResourceLocation.fromNamespaceAndPath("createenchantablemachinery", "enchantable_crushing_wheel"))) return true;
        return stack.is(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "create/crushing_wheels")));
    }

    private static ItemStack getBaseCreateMachineStack(ItemStack stack) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        if (!(stack.getItem() instanceof BlockItem)) return ItemStack.EMPTY;

        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (id == null || !id.getNamespace().startsWith("createcasing")) return ItemStack.EMPTY;

        ResourceLocation baseItemId = null;

        if (id.getPath().endsWith("_mechanical_drill")) {
            baseItemId = ResourceLocation.fromNamespaceAndPath("create", "mechanical_drill");
        } else if (id.getPath().endsWith("_mechanical_saw")) {
            baseItemId = ResourceLocation.fromNamespaceAndPath("create", "mechanical_saw");
        } else if (id.getPath().endsWith("_mechanical_harvester")) {
            baseItemId = ResourceLocation.fromNamespaceAndPath("create", "mechanical_harvester");
        } else if (id.getPath().endsWith("_encased_fan")) {
            baseItemId = ResourceLocation.fromNamespaceAndPath("create", "encased_fan");
        } else if (id.getPath().endsWith("_millstone")) {
            baseItemId = ResourceLocation.fromNamespaceAndPath("create", "millstone");
        } else if (id.getPath().endsWith("_crushing_wheel")) {
            baseItemId = ResourceLocation.fromNamespaceAndPath("create", "crushing_wheel");
        } else if (id.getPath().endsWith("_mechanical_plough")) {
            baseItemId = ResourceLocation.fromNamespaceAndPath("create", "mechanical_plough");
        } else if (id.getPath().endsWith("_mechanical_mixer")) {
            baseItemId = ResourceLocation.fromNamespaceAndPath("create", "mechanical_mixer");
        } else if (id.getPath().endsWith("_mechanical_press")) {
            baseItemId = ResourceLocation.fromNamespaceAndPath("create", "mechanical_press");
        } else if (id.getPath().endsWith("_mechanical_roller")) {
            baseItemId = ResourceLocation.fromNamespaceAndPath("create", "mechanical_roller");
        } else if (id.getPath().endsWith("_spout")) {
            baseItemId = ResourceLocation.fromNamespaceAndPath("create", "spout");
        }

        if (baseItemId == null) return ItemStack.EMPTY;
        Item baseItem = BuiltInRegistries.ITEM.get(baseItemId);
        if (baseItem == null || baseItem == Items.AIR) return ItemStack.EMPTY;
        return new ItemStack(baseItem);
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
