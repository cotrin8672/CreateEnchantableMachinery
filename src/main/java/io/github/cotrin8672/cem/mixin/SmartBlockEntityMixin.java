package io.github.cotrin8672.cem.mixin;

import com.mojang.serialization.Codec;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SmartBlockEntity.class, remap = false)
public abstract class SmartBlockEntityMixin {
    @Unique
    private static final Codec<DataComponentMap> CEM_COMPONENTS_CODEC =
            DataComponentMap.CODEC.optionalFieldOf("components", DataComponentMap.EMPTY).codec();

    @Inject(method = "write", at = @At("TAIL"), remap = false)
    private void cem$writeComponents(
            CompoundTag tag,
            HolderLookup.Provider registries,
            boolean clientPacket,
            CallbackInfo ci
    ) {
        if (!clientPacket) return;

        CEM_COMPONENTS_CODEC.encodeStart(
                registries.createSerializationContext(NbtOps.INSTANCE),
                ((BlockEntity) (Object) this).components()
        ).resultOrPartial(error -> {
        }).ifPresent(encoded -> tag.merge((CompoundTag) encoded));
    }

    @Inject(method = "read", at = @At("HEAD"), remap = false)
    private void cem$readComponents(
            CompoundTag tag,
            HolderLookup.Provider registries,
            boolean clientPacket,
            CallbackInfo ci
    ) {
        if (!clientPacket) return;

        BlockEntity blockEntity = (BlockEntity) (Object) this;
        ItemEnchantments before = blockEntity.components().get(DataComponents.ENCHANTMENTS);

        CEM_COMPONENTS_CODEC.parse(
                        registries.createSerializationContext(NbtOps.INSTANCE), tag
                ).resultOrPartial(error -> {
                })
                .ifPresent(components -> {
                    blockEntity.setComponents(components);
                    ItemEnchantments after = blockEntity.components().get(DataComponents.ENCHANTMENTS);
                    boolean wasEmpty = before == null || before.isEmpty();
                    boolean isPresent = after != null && !after.isEmpty();
                    if (wasEmpty && isPresent) {
                        VisualizationManager manager = VisualizationManager.get(blockEntity.getLevel());
                        if (manager != null) {
                            manager.blockEntities().queueRemove(blockEntity);
                            manager.blockEntities().queueAdd(blockEntity);
                        }
                    }
                });
    }
}
