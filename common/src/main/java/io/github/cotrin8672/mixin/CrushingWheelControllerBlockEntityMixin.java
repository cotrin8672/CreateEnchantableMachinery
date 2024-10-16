package io.github.cotrin8672.mixin;

import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Mixin(CrushingWheelControllerBlockEntity.class)
public interface CrushingWheelControllerBlockEntityMixin {
    @Accessor(value = "entityUUID", remap = false)
    UUID getEntityUUID();

    @Accessor(value = "entityUUID", remap = false)
    void setEntityUUID(UUID entityUUID);
}
