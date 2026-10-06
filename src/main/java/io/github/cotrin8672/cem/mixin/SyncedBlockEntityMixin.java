package io.github.cotrin8672.cem.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.create.foundation.blockEntity.SyncedBlockEntity;
import io.github.cotrin8672.cem.util.CemEnchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Block entity components are not part of Create's client sync, so the enchantments are sent along with it.
 */
@Mixin(SyncedBlockEntity.class)
public abstract class SyncedBlockEntityMixin {
    @WrapOperation(
        method = "getUpdateTag",
        at = @At(
            value = "INVOKE",
            target = "Lcom/zurrtum/create/foundation/blockEntity/SyncedBlockEntity;writeClient(Lnet/minecraft/world/level/storage/ValueOutput;)V"
        )
    )
    private void cem$writeEnchantments(SyncedBlockEntity blockEntity, ValueOutput view, Operation<Void> original) {
        original.call(blockEntity, view);
        ItemEnchantments enchantments = CemEnchantments.of(blockEntity);
        if (!enchantments.isEmpty()) {
            view.store(CemEnchantments.SYNC_KEY, ItemEnchantments.CODEC, enchantments);
        }
    }

    @Inject(method = {"handleUpdateTag", "onDataPacket"}, at = @At("HEAD"))
    private void cem$readEnchantments(ValueInput view, CallbackInfo ci) {
        ItemEnchantments enchantments = view.read(CemEnchantments.SYNC_KEY, ItemEnchantments.CODEC).orElse(ItemEnchantments.EMPTY);
        CemEnchantments.applySynced((BlockEntity) (Object) this, enchantments);
    }
}
