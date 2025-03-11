package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.contraptions.actors.roller.RollerBlock;
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

@Mixin(RollerBlock.class)
public class RollerBlockMixin {
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
            ItemInteractionResult result = BlockRegistration.getENCHANTABLE_MECHANICAL_ROLLER().get()
                    .useItemOn(stack, state, level, pos, player, hand, hitResult);
            cir.setReturnValue(result);
            cir.cancel();
        }
    }
}
