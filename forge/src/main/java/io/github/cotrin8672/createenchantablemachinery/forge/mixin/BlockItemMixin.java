package io.github.cotrin8672.createenchantablemachinery.forge.mixin;

import io.github.cotrin8672.createenchantablemachinery.content.block.EnchantableBlock;
import io.github.cotrin8672.createenchantablemachinery.util.EnchantableBlockMapping;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin extends Item {
    public BlockItemMixin(Properties settings) {
        super(settings);
    }

    @Shadow
    public abstract Block getBlock();

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
