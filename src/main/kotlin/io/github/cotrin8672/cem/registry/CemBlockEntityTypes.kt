package io.github.cotrin8672.cem.registry

import com.zurrtum.create.AllBlocks
import io.github.cotrin8672.cem.Cem
import io.github.cotrin8672.cem.content.plough.PloughEnchantmentBlockEntity
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.entity.BlockEntityType

object CemBlockEntityTypes {
    /** Create's plough has no block entity, so CEM attaches one to hold its enchantments. */
    @JvmField
    val PLOUGH: BlockEntityType<PloughEnchantmentBlockEntity> = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        Cem.asResource("mechanical_plough"),
        BlockEntityType(::PloughEnchantmentBlockEntity, setOf(AllBlocks.MECHANICAL_PLOUGH)),
    )

    fun register() {}
}
