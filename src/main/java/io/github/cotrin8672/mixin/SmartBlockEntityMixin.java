package io.github.cotrin8672.mixin;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = SmartBlockEntity.class, remap = false)
public interface SmartBlockEntityMixin {
    @Accessor("initialized")
    boolean getInitialized();

    @Accessor("initialized")
    void setInitialized(boolean value);

    @Accessor("lazyTickCounter")
    int getLazyTickCounter();

    @Accessor("lazyTickCounter")
    void setLazyTickCounter(int value);

    @Accessor("lazyTickRate")
    int getLazyTickRate();

    @Accessor("lazyTickRate")
    void setLazyTickRate(int value);
}
