package io.github.cotrin8672.cem.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = BlockBreakingKineticBlockEntity.class, remap = false)
public class DrillBlockEntityMixin {
    @ModifyReturnValue(method = "getBreakSpeed", at = @At("RETURN"), remap = false)
    public float cem$getBreakSpeed(float original) {
        BlockBreakingKineticBlockEntity blockEntity = (BlockBreakingKineticBlockEntity) (Object) this;
        ItemEnchantments enchantments = blockEntity.components().getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        Holder.Reference<Enchantment> efficienty = blockEntity.getLevel().holderLookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.EFFICIENCY);

        return original * (1 + enchantments.getLevel(efficienty));
    }
}
