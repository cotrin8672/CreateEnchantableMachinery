package io.github.cotrin8672.forge.registrate

import com.simibubi.create.foundation.data.CreateRegistrate
import com.tterrag.registrate.builders.BlockEntityBuilder
import com.tterrag.registrate.builders.BuilderCallback
import com.tterrag.registrate.util.nullness.NonNullFunction
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.Material
import net.minecraftforge.eventbus.api.IEventBus

class KotlinRegistrate(modId: String) : CreateRegistrate(modId) {
    companion object {
        fun create(modId: String): KotlinRegistrate {
            return KotlinRegistrate(modId)
        }
    }

    override fun registerEventListeners(bus: IEventBus): CreateRegistrate {
        return super.registerEventListeners(bus)
    }

    override fun <T : Block> block(
        name: String,
        factory: NonNullFunction<BlockBehaviour.Properties, T>,
    ) = customBlock(self(), name, Material.STONE, factory)

    override fun <T : Block, P : Any> block(
        parent: P,
        material: Material,
        factory: NonNullFunction<BlockBehaviour.Properties, T>,
    ) = customBlock(parent, currentName(), material, factory)

    override fun <T : Block, P : Any> block(
        parent: P,
        name: String,
        material: Material,
        factory: NonNullFunction<BlockBehaviour.Properties, T>,
    ) = customBlock(parent, name, material, factory)

    private fun <T : Block, P : Any> customBlock(
        parent: P,
        name: String,
        material: Material,
        factory: NonNullFunction<BlockBehaviour.Properties, T>,
    ): KotlinBlockBuilder<T, P> {
        return entry(name) { callback ->
            KotlinBlockBuilder.createKt(
                this,
                parent,
                name,
                callback,
                factory,
                material
            )
        } as KotlinBlockBuilder<T, P>
    }

    override fun <T : BlockEntity> blockEntity(
        name: String,
        factory: BlockEntityBuilder.BlockEntityFactory<T>,
    ): KotlinBlockEntityBuilder<T, CreateRegistrate> {
        return customBlockEntity(parent = self(), name = name, factory = factory)
    }

    override fun <T : BlockEntity, P : Any> blockEntity(
        parent: P,
        name: String,
        factory: BlockEntityBuilder.BlockEntityFactory<T>,
    ): KotlinBlockEntityBuilder<T, P> = customBlockEntity(parent, name, factory)

    private fun <T : BlockEntity, P> customBlockEntity(
        parent: P,
        name: String,
        factory: BlockEntityBuilder.BlockEntityFactory<T>,
    ): KotlinBlockEntityBuilder<T, P> {
        return entry(name) { callback: BuilderCallback ->
            KotlinBlockEntityBuilder.createKt(
                this,
                parent,
                name,
                callback,
                factory
            )
        } as KotlinBlockEntityBuilder<T, P>
    }
}
