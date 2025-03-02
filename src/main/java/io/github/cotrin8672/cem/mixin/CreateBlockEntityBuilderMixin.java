package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.foundation.data.CreateBlockEntityBuilder;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Predicate;

@Mixin(CreateBlockEntityBuilder.class)
public interface CreateBlockEntityBuilderMixin<T extends BlockEntity> {
    @Accessor(value = "visualFactory", remap = false)
    NonNullSupplier<SimpleBlockEntityVisualizer.Factory<T>> getVisualFactory();

    @Accessor(value = "renderNormally", remap = false)
    Predicate<@NotNull T> getRenderNormally();
}
