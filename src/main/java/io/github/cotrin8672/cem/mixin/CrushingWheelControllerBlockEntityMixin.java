package io.github.cotrin8672.cem.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.content.kinetics.crusher.CrushingWheelBlockEntity;
import com.zurrtum.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity;
import io.github.cotrin8672.cem.util.CemEnchantments;
import io.github.cotrin8672.cem.util.CemSpeeds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Item crushing speeds up by the Efficiency of both adjacent wheels. {@code crushingspeed} itself is left alone
 * because it also drives entity pull.
 */
@Mixin(CrushingWheelControllerBlockEntity.class)
public abstract class CrushingWheelControllerBlockEntityMixin {
    @ModifyExpressionValue(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F"))
    private float cem$speedUpCrushing(float processingSpeed) {
        return CemSpeeds.crushingSpeed(processingSpeed, cem$wheelEfficiency());
    }

    /** Mirrors how {@code CrushingWheelControllerBlock.updateSpeed} finds the wheels, but sums both. */
    @Unique
    private int cem$wheelEfficiency() {
        CrushingWheelControllerBlockEntity controller = (CrushingWheelControllerBlockEntity) (Object) this;
        Level level = controller.getLevel();
        if (level == null) {
            return 0;
        }
        int efficiency = 0;
        for (Direction direction : Direction.values()) {
            BlockPos wheelPos = controller.getBlockPos().relative(direction);
            BlockState wheel = level.getBlockState(wheelPos);
            if (!wheel.is(AllBlocks.CRUSHING_WHEEL) || wheel.getValue(BlockStateProperties.AXIS) == direction.getAxis()) {
                continue;
            }
            if (level.getBlockEntity(wheelPos) instanceof CrushingWheelBlockEntity wheelEntity) {
                efficiency += CemEnchantments.efficiency(wheelEntity);
            }
        }
        return efficiency;
    }
}
