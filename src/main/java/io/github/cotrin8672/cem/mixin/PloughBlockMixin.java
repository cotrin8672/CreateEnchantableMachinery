package io.github.cotrin8672.cem.mixin;

import com.zurrtum.create.content.contraptions.actors.plough.PloughBlock;
import io.github.cotrin8672.cem.content.plough.PloughEnchantmentBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Gives Create's plough a block entity so it has somewhere to keep its enchantments, in the world and on contraptions.
 */
@Mixin(PloughBlock.class)
public abstract class PloughBlockMixin implements EntityBlock {
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PloughEnchantmentBlockEntity(pos, state);
    }
}
