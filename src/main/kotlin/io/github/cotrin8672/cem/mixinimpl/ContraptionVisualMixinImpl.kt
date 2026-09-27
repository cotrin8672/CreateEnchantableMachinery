package io.github.cotrin8672.cem.mixinimpl

import com.simibubi.create.content.contraptions.render.ClientContraption
import com.simibubi.create.foundation.utility.worldWrappers.WrappedBlockAndTintGetter
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld
import dev.engine_room.flywheel.api.instance.Instancer
import dev.engine_room.flywheel.api.visualization.VisualEmbedding
import dev.engine_room.flywheel.lib.instance.InstanceTypes
import dev.engine_room.flywheel.lib.instance.TransformedInstance
import dev.engine_room.flywheel.lib.material.Materials
import dev.engine_room.flywheel.lib.model.baked.BlockModelBuilder
import net.minecraft.core.component.DataComponents
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

object ContraptionVisualMixinImpl {
    @JvmStatic
    fun setupStructure(
        contraption: ClientContraption,
        virtualRenderWorld: VirtualRenderWorld?,
        embedding: VisualEmbedding,
    ): Instancer<TransformedInstance>? {
        val blocks = contraption.renderedBlocks
        val modelWorld = object : WrappedBlockAndTintGetter(virtualRenderWorld) {
            override fun getBlockState(pos: BlockPos): BlockState {
                return blocks.lookup.apply(pos)
            }
        }
        val enchantedBlocks = blocks.positions.filter { pos ->
            val blockEntity = contraption.getBlockEntity(pos)
                ?: return@filter false
            val enchantments = blockEntity.components().get(DataComponents.ENCHANTMENTS)
            enchantments != null && !enchantments.isEmpty
        }.toList()

        if (enchantedBlocks.isEmpty()) {
            return null
        }

        val enchantedModel = BlockModelBuilder(modelWorld, enchantedBlocks)
            .materialFunc { _, _, _ -> Materials.GLINT }
            .build()
        return embedding.instancerProvider().instancer(InstanceTypes.TRANSFORMED, enchantedModel)
    }
}
