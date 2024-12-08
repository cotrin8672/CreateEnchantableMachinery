package io.github.cotrin8672.createenchantablemachinery.forge.mixin;

import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.function.Supplier;

@Mixin(BlockBuilder.class)
public interface BlockBuilderMixin {
    @Accessor(value = "renderLayers", remap = false)
    List<Supplier<Supplier<RenderType>>> getRenderLayers();

    @Accessor(value = "colorHandler", remap = false)
    NonNullSupplier<Supplier<BlockColor>> getColorHandler();
}
