package io.github.cotrin8672.forge.registrate

import com.jozufozu.flywheel.backend.instancing.InstancedRenderRegistry
import com.simibubi.create.foundation.data.CreateBlockEntityBuilder
import com.tterrag.registrate.AbstractRegistrate
import com.tterrag.registrate.builders.BuilderCallback
import com.tterrag.registrate.util.OneTimeEventReceiver
import io.github.cotrin8672.forge.mixin.CreateBlockEntityBuilderMixin
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

class KotlinBlockEntityBuilder<T : BlockEntity, P>
private constructor(
    private val owner: AbstractRegistrate<*>,
    parent: P,
    name: String,
    callback: BuilderCallback,
    factory: BlockEntityFactory<T>,
) : CreateBlockEntityBuilder<T, P>(owner, parent, name, callback, factory) {
    companion object {
        @JvmStatic
        fun <T : BlockEntity, P> create(
            owner: AbstractRegistrate<*>,
            parent: P,
            name: String,
            callback: BuilderCallback,
            factory: BlockEntityFactory<T>,
        ): KotlinBlockEntityBuilder<T, P> {
            return KotlinBlockEntityBuilder(owner, parent, name, callback, factory)
        }
    }

    override fun registerInstance() {
        OneTimeEventReceiver.addModListener(
            owner,
            FMLClientSetupEvent::class.java
        ) { _ ->
            val instanceFactory = (this as CreateBlockEntityBuilderMixin<T>).instanceFactory
            if (instanceFactory != null) {
                val renderNormally = (this as CreateBlockEntityBuilderMixin<T>).renderNormally
                InstancedRenderRegistry.configure(entry)
                    .factory(instanceFactory.get())
                    .skipRender { !renderNormally.test(it) }
                    .apply()
            }
        }
    }
}
