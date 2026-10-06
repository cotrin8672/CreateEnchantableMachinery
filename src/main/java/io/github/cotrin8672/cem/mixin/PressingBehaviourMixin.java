package io.github.cotrin8672.cem.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.content.kinetics.press.PressingBehaviour;
import io.github.cotrin8672.cem.util.CemEnchantments;
import io.github.cotrin8672.cem.util.CemSpeeds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Runs on both sides: the client animates the press head from the same tick speed, which is why the enchantments
 * are synced to the client.
 */
@Mixin(PressingBehaviour.class)
public abstract class PressingBehaviourMixin {
    @ModifyReturnValue(method = "getRunningTickSpeed()I", at = @At("RETURN"))
    private int cem$speedUpPressing(int speed) {
        BlockEntityBehaviour<?> behaviour = (BlockEntityBehaviour<?>) (Object) this;
        return CemSpeeds.pressRunningSpeed(speed, CemEnchantments.efficiency(behaviour.blockEntity));
    }
}
