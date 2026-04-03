package io.github.cotrin8672.cem.mixin;

import io.github.cotrin8672.cem.util.EnchantableBlockMapping;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin extends Item {
    private static final TagKey<Item> CEM_ENCHANTABLE_BLOCKS_TAG =
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("createenchantablemachinery", "enchantable_blocks"));

    public BlockItemMixin(Properties properties) {
        super(properties);
    }

    @Shadow
    public abstract Block getBlock();

    @Shadow
    protected abstract boolean canPlace(BlockPlaceContext context, BlockState state);

    @Inject(method = "getPlacementState", at = @At("HEAD"), cancellable = true)
    public void cem$getPlacementState(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        if (!context.getItemInHand().isEnchanted()) return;
        if (!context.getItemInHand().is(CEM_ENCHANTABLE_BLOCKS_TAG)) return;

        Block alternativeBlock = EnchantableBlockMapping.getAlternativeBlock(getBlock());
        if (alternativeBlock == null) return;

        BlockState blockState = alternativeBlock.getStateForPlacement(context);
        BlockState state = blockState != null && this.canPlace(context, blockState) ? blockState : null;
        cir.setReturnValue(state);
    }
}
