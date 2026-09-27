package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = SpoutBlockEntity.class, remap = false)
public interface SpoutBlockEntityMixin {
    @Accessor(value = "tank", remap = false)
    SmartFluidTankBehaviour getTank();

    @Accessor(value = "createdSweetRoll", remap = false)
    boolean getCreatedSweetRoll();

    @Accessor(value = "createdSweetRoll", remap = false)
    void setCreatedSweetRoll(boolean value);

    @Accessor(value = "createdHoneyApple", remap = false)
    boolean getCreatedHoneyApple();

    @Accessor(value = "createdHoneyApple", remap = false)
    void setCreatedHoneyApple(boolean value);

    @Accessor(value = "createdChocolateBerries", remap = false)
    boolean getCreatedChocolateBerries();

    @Accessor(value = "createdChocolateBerries", remap = false)
    void setCreatedChocolateBerries(boolean value);
}
