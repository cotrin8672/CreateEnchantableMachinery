package io.github.cotrin8672.cem.mixin;

import net.createmod.ponder.foundation.registration.PonderTagBuilder;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = PonderTagBuilder.class, remap = false)
public interface PonderTagBuilderMixin {
    @Accessor(value = "itemIcon", remap = false)
    void setItemIcon(ItemStack value);
}
