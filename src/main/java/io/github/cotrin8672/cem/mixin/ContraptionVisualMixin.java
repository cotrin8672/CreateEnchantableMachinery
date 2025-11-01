package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.contraptions.render.ClientContraption;
import com.simibubi.create.content.contraptions.render.ContraptionVisual;
import dev.engine_room.flywheel.api.visualization.VisualEmbedding;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import io.github.cotrin8672.cem.mixinimpl.ContraptionVisualMixinImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ContraptionVisual.class)
public class ContraptionVisualMixin {
    @Shadow
    @Final
    protected VisualEmbedding embedding;

    @Unique
    protected TransformedInstance cem$enchantedStructure;

    @Inject(method = "setupStructure", at = @At("TAIL"))
    private void cem$setupStructure(ClientContraption clientContraption, CallbackInfo ci) {
        var enchantedInstancer = ContraptionVisualMixinImpl.setupStructure(clientContraption, clientContraption.getRenderLevel(), embedding);

        if (cem$enchantedStructure == null) {
            cem$enchantedStructure = enchantedInstancer.createInstance();
        } else {
            enchantedInstancer.stealInstance(cem$enchantedStructure);
        }

        cem$enchantedStructure.setChanged();
    }

    @Inject(method = "_delete", at = @At("TAIL"))
    private void cem$_delete(CallbackInfo ci) {
        if (cem$enchantedStructure != null) {
            cem$enchantedStructure.delete();
        }
    }
}
