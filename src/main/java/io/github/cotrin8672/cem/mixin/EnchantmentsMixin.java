package io.github.cotrin8672.cem.mixin;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Enchantments.class)
public abstract class EnchantmentsMixin {
    @Inject(method = "register", at = @At("HEAD"))
    private static void cem$addEffects(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder, CallbackInfo ci) {

    }
}
