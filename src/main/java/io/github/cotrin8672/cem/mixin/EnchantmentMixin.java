package io.github.cotrin8672.cem.mixin;

import io.github.cotrin8672.cem.util.AnvilCompatibilityContext;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
    @Inject(method = "areCompatible", at = @At("HEAD"), cancellable = true)
    private static void cem$allowIncompatibleInCemAnvil(
            Holder<Enchantment> first,
            Holder<Enchantment> second,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (!AnvilCompatibilityContext.isActive()) return;
        if (first.equals(second)) return;
        if (!isFortuneSilkPair(first, second)) return;
        cir.setReturnValue(true);
    }

    private static boolean isFortuneSilkPair(Holder<Enchantment> first, Holder<Enchantment> second) {
        boolean firstFortune = first.is(Enchantments.FORTUNE);
        boolean firstSilk = first.is(Enchantments.SILK_TOUCH);
        boolean secondFortune = second.is(Enchantments.FORTUNE);
        boolean secondSilk = second.is(Enchantments.SILK_TOUCH);
        return (firstFortune && secondSilk) || (firstSilk && secondFortune);
    }
}
