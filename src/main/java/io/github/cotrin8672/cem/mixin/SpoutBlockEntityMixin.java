package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SpoutBlockEntity.class)
public interface SpoutBlockEntityMixin {
    @Accessor("tank")
    SmartFluidTankBehaviour getTank();

    @Accessor("createdSweetRoll")
    boolean getCreatedSweetRoll();

    @Accessor("createdSweetRoll")
    void setCreatedSweetRoll(boolean value);

    @Accessor("createdHoneyApple")
    boolean getCreatedHoneyApple();

    @Accessor("createdHoneyApple")
    void setCreatedHoneyApple(boolean value);

    @Accessor("createdChocolateBerries")
    boolean getCreatedChocolateBerries();

    @Accessor("createdChocolateBerries")
    void setCreatedChocolateBerries(boolean value);
}
