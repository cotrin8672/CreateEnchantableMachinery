package io.github.cotrin8672.mixin;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticEffectHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = KineticBlockEntity.class, remap = false)
public interface KineticBlockEntityMixin {
    @Accessor("validationCountdown")
    int getValidationCountdown();

    @Accessor("validationCountdown")
    void setValidationCountdown(int value);

    @Accessor("flickerTally")
    int getFlickerTally();

    @Accessor("flickerTally")
    void setFlickerTally(int value);

    @Accessor("effects")
    KineticEffectHandler getEffects();
}
