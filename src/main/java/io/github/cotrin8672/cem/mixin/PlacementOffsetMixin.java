package io.github.cotrin8672.cem.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.create.catnip.placement.PlacementOffset;
import com.zurrtum.create.foundation.blockEntity.SyncedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Placement helpers (e.g. extending a row of drills) bypass {@code BlockItem.place}, which is what normally copies
 * the item's components onto the new block entity.
 */
@Mixin(PlacementOffset.class)
public abstract class PlacementOffsetMixin {
    @WrapOperation(
        method = "placeInWorld",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/Block;setPlacedBy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V"
        )
    )
    private void cem$applyItemComponents(
        Block block,
        Level level,
        BlockPos pos,
        BlockState state,
        LivingEntity placer,
        ItemStack stack,
        Operation<Void> original
    ) {
        BlockEntity blockEntity = stack.has(DataComponents.ENCHANTMENTS) ? level.getBlockEntity(pos) : null;
        if (blockEntity != null) {
            blockEntity.applyComponentsFromItemStack(stack);
            blockEntity.setChanged();
        }
        original.call(block, level, pos, state, placer, stack);
        if (blockEntity instanceof SyncedBlockEntity synced) {
            synced.sendData();
        }
    }
}
