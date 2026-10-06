package io.github.cotrin8672.cem.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.zurrtum.create.content.kinetics.millstone.MillstoneBlockEntity;
import io.github.cotrin8672.cem.util.CemEnchantments;
import io.github.cotrin8672.cem.util.CemSpeeds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Shortens the recipe time rather than raising the int processing speed, which would round the bonus away at low RPM.
 */
@Mixin(MillstoneBlockEntity.class)
public abstract class MillstoneBlockEntityMixin {
    @ModifyExpressionValue(
        method = "tick()V",
        at = @At(value = "INVOKE", target = "Lcom/zurrtum/create/content/kinetics/millstone/MillingRecipe;time()I")
    )
    private int cem$shortenMilling(int time) {
        return CemSpeeds.millingTime(time, CemEnchantments.efficiency((MillstoneBlockEntity) (Object) this));
    }
}
