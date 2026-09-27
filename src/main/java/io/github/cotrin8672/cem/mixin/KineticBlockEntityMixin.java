package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import io.github.cotrin8672.cem.mixinimpl.KineticBlockEntityMixinImpl;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = KineticBlockEntity.class, remap = false)
public abstract class KineticBlockEntityMixin {
    @Inject(method = "addToGoggleTooltip", at = @At("TAIL"), remap = false)
    private void cem$addToGoogleTooltip(
            List<Component> tooltip,
            boolean isPlayerSneaking,
            CallbackInfoReturnable<Boolean> cir
    ) {
        KineticBlockEntityMixinImpl.addToGoogleTooltip(
                (KineticBlockEntity) (Object) this,
                tooltip,
                isPlayerSneaking,
                cir
        );
    }
}
