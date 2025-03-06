package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.kinetics.saw.SawBlock;
import io.github.cotrin8672.cem.registry.BlockRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SawBlock.class)
public class SawBlockMixin {
    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void createenchantablemachinery$use(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult,
            CallbackInfoReturnable<ItemInteractionResult> cir
    ) {
        if (player.getItemInHand(hand).isEnchanted()) {
            ItemInteractionResult result = BlockRegistration.getENCHANTABLE_MECHANICAL_DRILL().get()
                    .useItemOn(stack, state, level, pos, player, hand, hitResult);
            cir.setReturnValue(result);
            cir.cancel();
        }
    }
}
