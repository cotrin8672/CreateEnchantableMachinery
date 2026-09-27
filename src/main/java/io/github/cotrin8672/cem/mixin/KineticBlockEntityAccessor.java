package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticEffectHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = KineticBlockEntity.class, remap = false)
public interface KineticBlockEntityAccessor {
    @Accessor(value = "validationCountdown", remap = false)
    int getValidationCountdown();

    @Accessor(value = "validationCountdown", remap = false)
    void setValidationCountdown(int value);

    @Accessor(value = "flickerTally", remap = false)
    int getFlickerTally();

    @Accessor(value = "flickerTally", remap = false)
    void setFlickerTally(int value);

    @Accessor(value = "effects", remap = false)
    KineticEffectHandler getEffects();

    @Invoker(value = "validateKinetics", remap = false)
    void invokeValidateKinetics();
}
