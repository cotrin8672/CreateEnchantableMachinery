package io.github.cotrin8672.cem.mixin;

import io.github.cotrin8672.cem.util.AnvilCompatibilityContext;
import io.github.cotrin8672.cem.util.EnchantableBlockMapping;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public class AnvilMenuMixin {
    private static final TagKey<Item> CEM_ENCHANTABLE_BLOCKS_TAG =
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("createenchantablemachinery", "enchantable_blocks"));
    private static final TagKey<Item> C_CREATE_CRUSHING_WHEELS_TAG =
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "create/crushing_wheels"));

    @Unique
    private int cem$compatContextDepth = 0;

    @Inject(method = "createResult", at = @At("HEAD"))
    private void cem$enterCompatContext(CallbackInfo ci) {
        AnvilMenu self = (AnvilMenu) (Object) this;
        ItemStack left = self.getSlot(0).getItem();
        ItemStack right = self.getSlot(1).getItem();
        if (!isCrushingWheelItem(left)) return;
        if (!isEnchantmentSource(right)) return;

        AnvilCompatibilityContext.push();
        this.cem$compatContextDepth++;
    }

    @Inject(method = "createResult", at = @At("RETURN"))
    private void cem$exitCompatContext(CallbackInfo ci) {
        if (this.cem$compatContextDepth <= 0) return;
        this.cem$compatContextDepth--;
        AnvilCompatibilityContext.pop();
    }

    @Inject(method = "createResult", at = @At("TAIL"))
    private void cem$blockStackEnchantingForMachineItems(CallbackInfo ci) {
        AnvilMenu self = (AnvilMenu) (Object) this;
        Slot resultSlot = self.getSlot(2);

        ItemStack left = self.getSlot(0).getItem();
        ItemStack right = self.getSlot(1).getItem();
        boolean isCrushingWheel = isCrushingWheelItem(left);

        if (left.isEmpty()) {
            return;
        }
        if (!isMachineBlockItem(left) && !isCrushingWheel) {
            return;
        }
        if (right.isEmpty()) {
            return;
        }
        if (!isEnchantmentSource(right)) {
            return;
        }
        if (left.getCount() > 1) {
            if (!resultSlot.getItem().isEmpty()) {
                resultSlot.set(ItemStack.EMPTY);
            }
            return;
        }

        // Custom exception: allow Fortune + Silk Touch together only for machine block items.
        ItemEnchantments leftEnchantments = left.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        ItemEnchantments rightEnchantments = right.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        if (rightEnchantments.isEmpty()) {
            rightEnchantments = right.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        }
        if (rightEnchantments.isEmpty()) {
            return;
        }
        if (!isCrushingWheel) {
            return;
        }

        int leftFortune = getEnchantmentLevel(leftEnchantments, Enchantments.FORTUNE);
        int leftSilk = getEnchantmentLevel(leftEnchantments, Enchantments.SILK_TOUCH);
        int rightFortune = getEnchantmentLevel(rightEnchantments, Enchantments.FORTUNE);
        int rightSilk = getEnchantmentLevel(rightEnchantments, Enchantments.SILK_TOUCH);

        boolean incomingFortuneOrSilk = rightFortune > 0 || rightSilk > 0;
        if (!incomingFortuneOrSilk) {
            return;
        }

        boolean hasCombinedPair = (leftFortune > 0 || rightFortune > 0) && (leftSilk > 0 || rightSilk > 0);
        if (!hasCombinedPair) {
            return;
        }
        Holder<Enchantment> fortuneHolder = findEnchantmentHolder(leftEnchantments, rightEnchantments, Enchantments.FORTUNE);
        Holder<Enchantment> silkHolder = findEnchantmentHolder(leftEnchantments, rightEnchantments, Enchantments.SILK_TOUCH);
        if (fortuneHolder == null || silkHolder == null) {
            return;
        }

        ItemStack output = left.copyWithCount(1);
        ItemEnchantments.Mutable merged = new ItemEnchantments.Mutable(leftEnchantments);

        int fortuneLevel = combineAnvilLevel(leftFortune, rightFortune, fortuneHolder.value().getMaxLevel());
        int silkLevel = combineAnvilLevel(leftSilk, rightSilk, silkHolder.value().getMaxLevel());
        if (fortuneLevel > 0) merged.set(fortuneHolder, fortuneLevel);
        if (silkLevel > 0) merged.set(silkHolder, silkLevel);

        output.set(DataComponents.ENCHANTMENTS, merged.toImmutable());
        resultSlot.set(output);

        int baseCost = Math.max(1, self.getCost());
        self.setMaximumCost(baseCost * 4);
    }

    private static boolean isMachineBlockItem(ItemStack stack) {
        if (stack.is(CEM_ENCHANTABLE_BLOCKS_TAG)) return true;
        if (!(stack.getItem() instanceof BlockItem blockItem)) return false;
        return EnchantableBlockMapping.getOriginalBlocks().contains(blockItem.getBlock())
                || EnchantableBlockMapping.getEnchantableBlocks().contains(blockItem.getBlock());
    }

    private static boolean isEnchantmentSource(ItemStack stack) {
        ItemEnchantments stored = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        if (!stored.isEmpty()) return true;
        ItemEnchantments direct = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        return !direct.isEmpty();
    }

    private static boolean isCrushingWheelItem(ItemStack stack) {
        if (stack.is(C_CREATE_CRUSHING_WHEELS_TAG)) return true;
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (itemId == null) return false;
        if (itemId.equals(ResourceLocation.fromNamespaceAndPath("create", "crushing_wheel"))) return true;
        return itemId.equals(ResourceLocation.fromNamespaceAndPath("createenchantablemachinery", "enchantable_crushing_wheel"));
    }

    private static int combineAnvilLevel(int existingLevel, int incomingLevel, int maxLevel) {
        if (incomingLevel <= 0) return existingLevel;
        if (existingLevel == incomingLevel) return Math.min(maxLevel, incomingLevel + 1);
        return Math.max(existingLevel, incomingLevel);
    }

    private static int getEnchantmentLevel(ItemEnchantments enchantments, ResourceKey<Enchantment> key) {
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            if (entry.getKey().is(key)) {
                return entry.getIntValue();
            }
        }
        return 0;
    }

    private static Holder<Enchantment> findEnchantmentHolder(
            ItemEnchantments left,
            ItemEnchantments right,
            ResourceKey<Enchantment> key
    ) {
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : left.entrySet()) {
            if (entry.getKey().is(key)) return entry.getKey();
        }
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : right.entrySet()) {
            if (entry.getKey().is(key)) return entry.getKey();
        }
        return null;
    }
}
