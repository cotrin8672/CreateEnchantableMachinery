package io.github.cotrin8672.cem.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.zurrtum.create.content.fluids.spout.SpoutBlockEntity;
import io.github.cotrin8672.cem.util.CemEnchantments;
import io.github.cotrin8672.cem.util.CemSpeeds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * {@code SpoutBlockEntity.FILLING_TIME} (20) is inlined by javac, so the constant is targeted in both places that
 * start a filling: items held on a belt or depot, and blocks below the spout.
 */
@Mixin(SpoutBlockEntity.class)
public abstract class SpoutBlockEntityMixin {
    @ModifyExpressionValue(
        method = {"whenItemHeld", "tick()V"},
        at = @At(value = "CONSTANT", args = "intValue=20")
    )
    private int cem$shortenFilling(int fillingTime) {
        return CemSpeeds.spoutFillingTime(fillingTime, CemEnchantments.efficiency((SpoutBlockEntity) (Object) this));
    }
}
