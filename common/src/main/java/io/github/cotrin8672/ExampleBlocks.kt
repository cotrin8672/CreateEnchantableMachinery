package io.github.cotrin8672

import com.tterrag.registrate.util.entry.BlockEntry
import io.github.cotrin8672.CreateEnchantableMachinery.REGISTRATE
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour

object ExampleBlocks {
    val EXAMPLE_BLOCK: BlockEntry<Block> = REGISTRATE
        .block<Block>("example_block") { properties: BlockBehaviour.Properties ->
            Block(properties)
        }
        .register()

    fun init() {
        EXAMPLE_BLOCK
    }
}
