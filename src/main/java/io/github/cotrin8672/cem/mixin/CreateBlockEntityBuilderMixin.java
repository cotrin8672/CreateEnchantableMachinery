package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.foundation.data.CreateBlockEntityBuilder;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.NonNullPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = CreateBlockEntityBuilder.class, remap = false)
public interface CreateBlockEntityBuilderMixin<T extends BlockEntity> {
    @Accessor(value = "visualFactory", remap = false)
    NonNullSupplier<SimpleBlockEntityVisualizer.Factory<T>> getVisualFactory();

    @Accessor(value = "renderNormally", remap = false)
    NonNullPredicate<T> getRenderNormally();
}
