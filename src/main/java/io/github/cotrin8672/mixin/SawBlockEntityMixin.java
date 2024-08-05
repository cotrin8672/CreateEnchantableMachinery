package io.github.cotrin8672.mixin;

import com.simibubi.create.content.kinetics.saw.SawBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SawBlockEntity.class)
public interface SawBlockEntityMixin {
    @Accessor(value = "filtering", remap = false)
    FilteringBehaviour getFiltering();

    @Accessor(value = "recipeIndex", remap = false)
    int getRecipeIndex();

    @Accessor(value = "recipeIndex", remap = false)
    void setRecipeIndex(int value);
}
