package io.github.cotrin8672.cem.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.zurrtum.create.content.contraptions.actors.plough.PloughMovementBehaviour;
import com.zurrtum.create.content.contraptions.behaviour.MovementContext;
import io.github.cotrin8672.cem.util.CemEnchantments;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Snow layers are dropped with a shovel; give it the plough's enchantments so Silk Touch yields snow layers.
 */
@Mixin(PloughMovementBehaviour.class)
public abstract class PloughMovementBehaviourMixin {
    @ModifyExpressionValue(method = "onBlockBroken", at = @At(value = "NEW", target = "net/minecraft/world/item/ItemStack"))
    private ItemStack cem$enchantShovel(ItemStack shovel, @Local(argsOnly = true) MovementContext context) {
        ItemEnchantments enchantments = CemEnchantments.of(context);
        if (!enchantments.isEmpty()) {
            shovel.set(DataComponents.ENCHANTMENTS, enchantments);
        }
        return shovel;
    }
}
