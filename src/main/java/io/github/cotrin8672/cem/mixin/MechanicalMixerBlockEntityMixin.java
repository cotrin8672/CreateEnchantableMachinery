package io.github.cotrin8672.cem.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import io.github.cotrin8672.cem.util.CemEnchantments;
import io.github.cotrin8672.cem.util.CemSpeeds;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MechanicalMixerBlockEntity.class)
public abstract class MechanicalMixerBlockEntityMixin {
    /** The first write of {@code processingTicks} in {@code tick} is where the recipe duration is computed. */
    @WrapOperation(
        method = "tick()V",
        at = @At(
            value = "FIELD",
            target = "Lcom/zurrtum/create/content/kinetics/mixer/MechanicalMixerBlockEntity;processingTicks:I",
            opcode = Opcodes.PUTFIELD,
            ordinal = 0
        )
    )
    private void cem$shortenMixing(MechanicalMixerBlockEntity mixer, int ticks, Operation<Void> original) {
        original.call(mixer, CemSpeeds.mixingTicks(ticks, CemEnchantments.efficiency(mixer)));
    }
}
