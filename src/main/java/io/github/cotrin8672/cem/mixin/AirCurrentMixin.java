package io.github.cotrin8672.cem.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.zurrtum.create.content.kinetics.fan.AirCurrent;
import io.github.cotrin8672.cem.util.CemFanContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Fan processing times are computed in static helpers with no reference to the fan, so the fan is remembered for
 * the duration of the two methods that end up calling them.
 */
@Mixin(AirCurrent.class)
public abstract class AirCurrentMixin {
    @WrapMethod(method = "tickAffectedEntities")
    private void cem$trackEntitySource(@Nullable Level level, Operation<Void> original) {
        CemFanContext.run(((AirCurrent) (Object) this).source, () -> original.call(level));
    }

    @WrapMethod(method = "tickAffectedHandlers")
    private void cem$trackHandlerSource(Operation<Void> original) {
        CemFanContext.run(((AirCurrent) (Object) this).source, original::call);
    }
}
