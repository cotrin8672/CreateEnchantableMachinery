package io.github.cotrin8672.cem.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.cotrin8672.cem.registry.CemMachines;
import io.github.cotrin8672.cem.util.CemEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Picking an enchanted machine copies its enchantments even without Ctrl, as earlier versions of the mod did.
 * Vanilla only copies block entity components when Ctrl is held.
 */
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
    @ModifyExpressionValue(
        method = "handlePickItemFromBlock",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;getCloneItemStack(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Z)Lnet/minecraft/world/item/ItemStack;"
        )
    )
    private ItemStack cem$keepEnchantments(ItemStack stack, @Local BlockState state, @Local BlockPos pos, @Local ServerLevel level) {
        if (stack.isEmpty() || stack.has(DataComponents.ENCHANTMENTS) || !CemMachines.INSTANCE.getBlocks().contains(state.getBlock())) {
            return stack;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity != null) {
            ItemEnchantments enchantments = CemEnchantments.of(blockEntity);
            if (!enchantments.isEmpty()) {
                stack.set(DataComponents.ENCHANTMENTS, enchantments);
            }
        }
        return stack;
    }
}
