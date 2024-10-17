package io.github.cotrin8672.forge.mixin;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.simibubi.create.foundation.data.CreateBlockEntityBuilder;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.NonNullPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.BiFunction;

@Mixin(CreateBlockEntityBuilder.class)
public interface CreateBlockEntityBuilderMixin<T extends BlockEntity> {
    @Accessor(value = "instanceFactory", remap = false)
    NonNullSupplier<BiFunction<MaterialManager, T, BlockEntityInstance<? super T>>> getInstanceFactory();

    @Accessor(value = "renderNormally", remap = false)
    NonNullPredicate<T> getRenderNormally();
}
