package io.github.cotrin8672.mixin;

import com.simibubi.create.content.contraptions.actors.roller.RollerBlock;
import io.github.cotrin8672.registrate.BlockRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RollerBlock.class)
public class RollerBlockMixin {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void createenchantablemachinery$use(
            BlockState state,
            Level world,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult ray,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        if (player.getItemInHand(hand).isEnchanted()) {
            InteractionResult result =
                    BlockRegistration.getENCHANTABLE_MECHANICAL_ROLLER().get().use(state, world, pos, player, hand, ray);
            cir.setReturnValue(result);
            cir.cancel();
        }
    }
}
