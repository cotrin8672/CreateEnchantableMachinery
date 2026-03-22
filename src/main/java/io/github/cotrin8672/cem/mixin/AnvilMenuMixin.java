package io.github.cotrin8672.cem.mixin;

import io.github.cotrin8672.cem.util.EnchantableBlockMapping;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public class AnvilMenuMixin {
    @Inject(method = "createResult", at = @At("TAIL"))
    private void cem$blockStackEnchantingForMachineItems(CallbackInfo ci) {
        AnvilMenu self = (AnvilMenu) (Object) this;
        Slot leftSlot = self.getSlot(0);
        Slot rightSlot = self.getSlot(1);
        Slot resultSlot = self.getSlot(2);

        ItemStack left = leftSlot.getItem();
        ItemStack right = rightSlot.getItem();

        if (left.getCount() <= 1) return;
        if (!isMachineBlockItem(left)) return;
        if (right.isEmpty()) return;
        if (!isEnchantmentSource(right)) return;
        if (resultSlot.getItem().isEmpty()) return;

        resultSlot.set(ItemStack.EMPTY);
    }

    private static boolean isMachineBlockItem(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) return false;
        return EnchantableBlockMapping.getOriginalBlocks().contains(blockItem.getBlock());
    }

    private static boolean isEnchantmentSource(ItemStack stack) {
        ItemEnchantments stored = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        if (!stored.isEmpty()) return true;
        ItemEnchantments direct = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        return !direct.isEmpty();
    }
}
