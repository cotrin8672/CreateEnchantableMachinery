package io.github.cotrin8672.cem.mixinimpl

import com.simibubi.create.content.contraptions.Contraption
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld
import dev.engine_room.flywheel.api.instance.Instancer
import dev.engine_room.flywheel.api.visualization.VisualEmbedding
import dev.engine_room.flywheel.lib.instance.InstanceTypes
import dev.engine_room.flywheel.lib.instance.TransformedInstance
import dev.engine_room.flywheel.lib.material.Materials
import dev.engine_room.flywheel.lib.model.baked.BlockModelBuilder
import io.github.cotrin8672.cem.util.EnchantableBlockMapping

object ContraptionVisualMixinImpl {
    @JvmStatic
    fun setupModel(
        contraption: Contraption,
        virtualRenderWorld: VirtualRenderWorld?,
        embedding: VisualEmbedding,
    ): Instancer<TransformedInstance> {
        val blocks = contraption.renderedBlocks
        val level = contraption.entity.level()
        val enchantedBlocks = blocks.positions.filter {
            EnchantableBlockMapping.getEnchantableBlocks().contains(level.getBlockState(it).block)
        }.toList()

        val enchantedModel = BlockModelBuilder.create(level, enchantedBlocks)
            .materialFunc { _, _ -> Materials.GLINT }
            .build()
        return embedding.instancerProvider().instancer(InstanceTypes.TRANSFORMED, enchantedModel)
    }
}
