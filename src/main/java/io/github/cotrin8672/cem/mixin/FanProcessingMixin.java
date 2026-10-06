package io.github.cotrin8672.cem.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.zurrtum.create.content.kinetics.fan.processing.FanProcessing;
import io.github.cotrin8672.cem.util.CemFanContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Both processing paths read {@code fanProcessingTime} from the config; the {@code intValue()} unboxing is the hook.
 */
@Mixin(FanProcessing.class)
public abstract class FanProcessingMixin {
    @ModifyExpressionValue(
        method = "applyProcessing(Lcom/zurrtum/create/content/kinetics/belt/transport/TransportedItemStack;Lnet/minecraft/world/level/Level;Lcom/zurrtum/create/content/kinetics/fan/processing/FanProcessingType;)Lcom/zurrtum/create/content/kinetics/belt/behaviour/TransportedItemStackHandlerBehaviour$TransportedResult;",
        at = @At(value = "INVOKE", target = "Ljava/lang/Integer;intValue()I")
    )
    private static int cem$shortenBeltProcessing(int time) {
        return CemFanContext.processingTime(time);
    }

    @ModifyExpressionValue(
        method = "decrementProcessingTime",
        at = @At(value = "INVOKE", target = "Ljava/lang/Integer;intValue()I", ordinal = 0)
    )
    private static int cem$shortenEntityProcessing(int time) {
        return CemFanContext.processingTime(time);
    }
}
