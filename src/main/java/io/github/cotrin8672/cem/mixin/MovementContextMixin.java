package io.github.cotrin8672.cem.mixin;

import com.zurrtum.create.content.contraptions.behaviour.MovementContext;
import io.github.cotrin8672.cem.util.CemEnchantments;
import io.github.cotrin8672.cem.util.MovementContextExtension;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MovementContext.class)
public abstract class MovementContextMixin implements MovementContextExtension {
    @Shadow
    public Level world;

    @Shadow
    public @Nullable CompoundTag blockEntityData;

    @Unique
    private @Nullable ItemEnchantments cem$enchantments;

    @Override
    public ItemEnchantments cem_getEnchantments() {
        if (cem$enchantments == null) {
            cem$enchantments = CemEnchantments.fromBlockEntityData(blockEntityData, world.registryAccess());
        }
        return cem$enchantments;
    }
}
