package io.github.cotrin8672.cem.client

import dev.engine_room.flywheel.api.material.DepthTest
import dev.engine_room.flywheel.api.material.Material
import dev.engine_room.flywheel.lib.material.Materials
import dev.engine_room.flywheel.lib.material.SimpleMaterial

object CemMaterials {
    @JvmField
    val BLOCK_GLINT: Material = SimpleMaterial.builderOf(Materials.GLINT)
        .depthTest(DepthTest.LEQUAL)
        .polygonOffset(true)
        .build()
}
