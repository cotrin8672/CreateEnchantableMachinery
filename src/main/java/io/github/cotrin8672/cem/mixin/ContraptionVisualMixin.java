package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.render.ContraptionVisual;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
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
    protected VirtualRenderWorld virtualRenderWorld;

    @Shadow
    @Final
    protected VisualEmbedding embedding;

    @Unique
    protected TransformedInstance cem$enchantedStructure;

    @Inject(method = "setupModel", at = @At("TAIL"))
    private void cem$setupModel(Contraption contraption, CallbackInfo ci) {
        var enchantedInstancer = ContraptionVisualMixinImpl.setupModel(contraption, virtualRenderWorld, embedding);

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
