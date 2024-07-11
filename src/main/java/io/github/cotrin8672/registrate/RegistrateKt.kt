package io.github.cotrin8672.registrate

import com.simibubi.create.foundation.data.CreateRegistrate
import com.tterrag.registrate.builders.BlockEntityBuilder
import com.tterrag.registrate.builders.BuilderCallback
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraftforge.eventbus.api.IEventBus
import thedarkcolour.kotlinforforge.forge.MOD_BUS

class RegistrateKt(modId: String) : CreateRegistrate(modId) {
    companion object {
        fun create(modId: String): RegistrateKt {
            return RegistrateKt(modId)
        }
    }

    override fun getModEventBus(): IEventBus {
        return MOD_BUS
    }

    override fun <T : BlockEntity> blockEntity(
        name: String,
        factory: BlockEntityBuilder.BlockEntityFactory<T>,
    ): CreateBlockEntityBuilderKt<T, CreateRegistrate> {
        return customBlockEntity(parent = self(), name = name, factory = factory)
    }

    override fun <T : BlockEntity, P> blockEntity(
        parent: P,
        name: String,
        factory: BlockEntityBuilder.BlockEntityFactory<T>,
    ): CreateBlockEntityBuilderKt<T, P> = customBlockEntity(parent, name, factory)

    private fun <T : BlockEntity, P> customBlockEntity(
        parent: P,
        name: String,
        factory: BlockEntityBuilder.BlockEntityFactory<T>,
    ): CreateBlockEntityBuilderKt<T, P> {
        return entry(
            name
        ) { callback: BuilderCallback ->
            CreateBlockEntityBuilderKt.createKt(
                this,
                parent,
                name,
                callback,
                factory
            )
        } as CreateBlockEntityBuilderKt<T, P>
    }
}
