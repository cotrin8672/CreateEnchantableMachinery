package io.github.cotrin8672.cem.mixinimpl

import com.simibubi.create.content.contraptions.Contraption
import com.simibubi.create.foundation.utility.worldWrappers.WrappedBlockAndTintGetter
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld
import dev.engine_room.flywheel.api.instance.Instancer
import dev.engine_room.flywheel.api.visualization.VisualEmbedding
import dev.engine_room.flywheel.lib.instance.InstanceTypes
import dev.engine_room.flywheel.lib.instance.TransformedInstance
import dev.engine_room.flywheel.lib.material.Materials
import dev.engine_room.flywheel.lib.model.baked.BlockModelBuilder
import io.github.cotrin8672.cem.registry.BlockRegistration
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

object ContraptionVisualMixinImpl {
    @JvmStatic
    fun setupModel(
        contraption: Contraption,
        virtualRenderWorld: VirtualRenderWorld?,
        embedding: VisualEmbedding,
    ): Instancer<TransformedInstance> {
        val blocks = contraption.renderedBlocks
        val modelWorld = object : WrappedBlockAndTintGetter(virtualRenderWorld) {
            override fun getBlockState(pos: BlockPos): BlockState {
                return blocks.lookup.apply(pos)
            }
        }
        val enchantedBlocks = blocks.positions.filter {
            enchantableBlocks.contains(modelWorld.getBlockState(it).block)
        }.toList()

        val enchantedModel = BlockModelBuilder(modelWorld, enchantedBlocks)
            .materialFunc { _, _ -> Materials.GLINT }
            .build()
        return embedding.instancerProvider().instancer(InstanceTypes.TRANSFORMED, enchantedModel)
    }

    private val enchantableBlocks = setOf(
        BlockRegistration.ENCHANTABLE_MECHANICAL_DRILL.get()
    )
}
