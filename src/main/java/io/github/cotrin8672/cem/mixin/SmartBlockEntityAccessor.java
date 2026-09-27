package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = SmartBlockEntity.class, remap = false)
public interface SmartBlockEntityAccessor {
    @Accessor(value = "initialized", remap = false)
    boolean getInitialized();

    @Accessor(value = "initialized", remap = false)
    void setInitialized(boolean value);

    @Accessor(value = "lazyTickCounter", remap = false)
    int getLazyTickCounter();

    @Accessor(value = "lazyTickCounter", remap = false)
    void setLazyTickCounter(int value);

    @Accessor(value = "lazyTickRate", remap = false)
    int getLazyTickRate();

    @Accessor(value = "lazyTickRate", remap = false)
    void setLazyTickRate(int value);
}
