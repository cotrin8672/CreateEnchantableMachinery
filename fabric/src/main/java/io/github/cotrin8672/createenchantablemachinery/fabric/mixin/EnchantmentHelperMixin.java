package io.github.cotrin8672.createenchantablemachinery.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.cotrin8672.createenchantablemachinery.content.block.EnchantableBlock;
import io.github.cotrin8672.createenchantablemachinery.util.EnchantableBlockMapping;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = EnchantmentHelper.class, priority = 900)
public class EnchantmentHelperMixin {
    @Unique
    private static Enchantment currentEnchantment = null;

    @ModifyExpressionValue(
            method = "getAvailableEnchantmentResults",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Iterator;next()Ljava/lang/Object;"
            )
    )
    private static Object port_lib$grabEnchantment(Object o) {
        if (o instanceof Enchantment e) {
            currentEnchantment = e;
        }
        return o;
    }

    @WrapOperation(
            method = "getAvailableEnchantmentResults",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/enchantment/EnchantmentCategory;canEnchant(Lnet/minecraft/world/item/Item;)Z"
            )
    )
    private static boolean createenchantablemachinery$customEnchantability(
            EnchantmentCategory instance,
            Item arg,
            Operation<Boolean> original,
            int level,
            ItemStack stack,
            boolean allowTreasure
    ) {
        Enchantment enchantment = currentEnchantment;
        if (stack.getItem() instanceof BlockItem blockItem) {
            Block altBlock = EnchantableBlockMapping.getAlternativeBlock(blockItem.getBlock());
            if (altBlock instanceof EnchantableBlock)
                return ((EnchantableBlock) altBlock).canApply(enchantment);
        }
        return original.call(instance, arg);
    }
}

