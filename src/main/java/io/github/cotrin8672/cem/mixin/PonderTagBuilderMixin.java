package io.github.cotrin8672.cem.mixin;

import net.createmod.ponder.foundation.registration.PonderTagBuilder;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PonderTagBuilder.class)
public interface PonderTagBuilderMixin {
    @Accessor("itemIcon")
    void setItemIcon(ItemStack value);
}
