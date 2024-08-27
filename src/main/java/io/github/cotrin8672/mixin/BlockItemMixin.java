package io.github.cotrin8672.mixin;

import io.github.cotrin8672.block.EnchantableBlock;
import io.github.cotrin8672.util.EnchantableBlockMapping;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin extends Item {
    public BlockItemMixin(Properties properties) {
        super(properties);
    }

    @Shadow
    public abstract Block getBlock();

    @Shadow
    protected abstract boolean canPlace(BlockPlaceContext context, BlockState state);

    @Inject(
            method = "getPlacementState(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/level/block/state/BlockState;",
            at = @At("HEAD"),
            cancellable = true
    )
    public void createenchantablemachinery$getPlacementState(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        Block alternativeBlock = EnchantableBlockMapping.getAlternativeBlock(this.getBlock());
        if (alternativeBlock != null && context.getItemInHand().isEnchanted()) {
            BlockState blockState = alternativeBlock.getStateForPlacement(context);
            BlockState state = blockState != null && this.canPlace(context, blockState) ? blockState : null;
            cir.setReturnValue(state);
        }
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        Block alternateBlock = EnchantableBlockMapping.getAlternativeBlock(getBlock());
        if (alternateBlock instanceof EnchantableBlock) {
            return ((EnchantableBlock) alternateBlock).canApply(enchantment);
        } else {
            return super.canApplyAtEnchantingTable(stack, enchantment);
        }
    }
}
