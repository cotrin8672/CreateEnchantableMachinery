package io.github.cotrin8672.mixin;

import com.simibubi.create.content.kinetics.saw.SawBlock;
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

@Mixin(SawBlock.class)
public class SawBlockMixin {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void createenchantablemachinery$use(
            BlockState state,
            Level worldIn,
            BlockPos pos,
            Player player,
            InteractionHand handIn,
            BlockHitResult hit,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        if (player.getItemInHand(handIn).isEnchanted()) {
            InteractionResult result =
                    BlockRegistration.getENCHANTABLE_MECHANICAL_SAW().get().use(state, worldIn, pos, player, handIn, hit);
            cir.setReturnValue(result);
            cir.cancel();
        }
    }
}
