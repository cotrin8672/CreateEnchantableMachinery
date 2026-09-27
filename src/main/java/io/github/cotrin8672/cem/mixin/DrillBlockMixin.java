package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.kinetics.drill.DrillBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = DrillBlock.class, remap = false)
public class DrillBlockMixin {
//    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
//    private void createenchantablemachinery$use(
//            ItemStack stack,
//            BlockState state,
//            Level level,
//            BlockPos pos,
//            Player player,
//            InteractionHand hand,
//            BlockHitResult hitResult,
//            CallbackInfoReturnable<ItemInteractionResult> cir
//    ) {
//        ItemStack heldItem = player.getItemInHand(hand);
//        if (heldItem.isEnchanted()
//                && heldItem.getItem() instanceof BlockItem blockItem
//                && blockItem.getBlock() == state.getBlock()) {
//            ItemInteractionResult result = BlockRegistration.getENCHANTABLE_MECHANICAL_DRILL().get()
//                    .useItemOn(stack, state, level, pos, player, hand, hitResult);
//            cir.setReturnValue(result);
//        }
//    }
}
