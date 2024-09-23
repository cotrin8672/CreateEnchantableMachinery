package io.github.cotrin8672.mixin;

import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockEntityBehaviour.class)
public interface BlockEntityBehaviourMixin {
    @Accessor(value = "lazyTickCounter", remap = false)
    int getLazyTickCounter();

    @Accessor(value = "lazyTickCounter", remap = false)
    void setLazyTickCounter(int value);

    @Accessor(value = "lazyTickRate", remap = false)
    int getLazyTickRate();
}
