package io.github.cotrin8672.cem.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.zurrtum.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.zurrtum.create.content.contraptions.behaviour.MovementContext;
import io.github.cotrin8672.cem.util.CemEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;

/**
 * Harvests with an enchanted tool, so e.g. Fortune increases crop drops. Leaves keep Create's shears.
 */
@Mixin(HarvesterMovementBehaviour.class)
public abstract class HarvesterMovementBehaviourMixin {
    @WrapOperation(
        method = "visitNewPosition",
        at = @At(
            value = "INVOKE",
            target = "Lcom/zurrtum/create/foundation/utility/BlockHelper;destroyBlockAs(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;FLjava/util/function/Consumer;)V"
        )
    )
    private void cem$harvestWithEnchantedTool(
        Level level,
        BlockPos pos,
        Player player,
        ItemStack tool,
        float effectChance,
        Consumer<ItemStack> droppedItemCallback,
        Operation<Void> original,
        @Local(argsOnly = true) MovementContext context
    ) {
        ItemEnchantments enchantments = CemEnchantments.of(context);
        if (!enchantments.isEmpty()) {
            if (tool.isEmpty()) {
                tool = CemEnchantments.createTool(enchantments, Items.NETHERITE_HOE);
            } else {
                tool = tool.copy();
                tool.set(DataComponents.ENCHANTMENTS, enchantments);
            }
        }
        original.call(level, pos, player, tool, effectChance, droppedItemCallback);
    }
}
