package io.github.cotrin8672.util

import io.github.cotrin8672.block.EnchantmentProperties
import net.minecraft.core.Direction
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.client.model.generators.ConfiguredModel
import net.minecraftforge.client.model.generators.ModelFile

fun BlockStateProvider.enchantableDirectionalBLock(block: Block, modelFunc: (BlockState) -> ModelFile) {
    getVariantBuilder(block)
        .forAllStates { state ->
            val dir = state.getValue(BlockStateProperties.FACING)
            val efficiency = state.getValue(EnchantmentProperties.EFFICIENCY_LEVEL)
            val fortune = state.getValue(EnchantmentProperties.FORTUNE_LEVEL)
            val silkTouch = state.getValue(EnchantmentProperties.SILK_TOUCH_LEVEL)
            val model = when {
                efficiency > 0 -> modelFunc(state)
                fortune > 0 -> modelFunc(state)
                silkTouch > 0 -> modelFunc(state)
                else -> modelFunc(state)
            }
            ConfiguredModel.builder()
                .modelFile(model)
                .rotationX(if (dir == Direction.DOWN) 180 else if (dir.axis.isHorizontal) 90 else 0)
                .rotationY(if (dir.axis.isVertical) 0 else (dir.toYRot().toInt() + 180) % 360)
                .build()
        }
}
