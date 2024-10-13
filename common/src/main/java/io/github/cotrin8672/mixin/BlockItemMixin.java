package io.github.cotrin8672.mixin;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    @Inject(
            method = "getPlacementState",
            at = @At("HEAD")
    )
    public void createenchantablemachinery$getPlacementState(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        System.out.println("test");
    }
}
