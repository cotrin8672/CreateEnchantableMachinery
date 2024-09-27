package io.github.cotrin8672.mixin;

import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = BasinOperatingBlockEntity.class, remap = false)
public interface BasinOperatingBlockEntityMixin {
    @Invoker("onBasinRemoved")
    void onBasinRemoved();
}
