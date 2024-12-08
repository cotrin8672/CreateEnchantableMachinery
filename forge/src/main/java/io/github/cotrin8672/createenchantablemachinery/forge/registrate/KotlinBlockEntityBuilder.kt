package io.github.cotrin8672.createenchantablemachinery.forge.registrate

import com.jozufozu.flywheel.backend.instancing.InstancedRenderRegistry
import com.simibubi.create.foundation.data.CreateBlockEntityBuilder
import com.tterrag.registrate.AbstractRegistrate
import com.tterrag.registrate.builders.BuilderCallback
import com.tterrag.registrate.util.OneTimeEventReceiver
import io.github.cotrin8672.createenchantablemachinery.forge.mixin.BlockEntityBuilderMixin
import io.github.cotrin8672.createenchantablemachinery.forge.mixin.CreateBlockEntityBuilderMixin
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import thedarkcolour.kotlinforforge.forge.MOD_BUS

class KotlinBlockEntityBuilder<T : BlockEntity, P>
private constructor(
    owner: AbstractRegistrate<*>,
    parent: P,
    name: String,
    callback: BuilderCallback,
    factory: BlockEntityFactory<T>,
) : CreateBlockEntityBuilder<T, P>(owner, parent, name, callback, factory) {
    companion object {
        @JvmStatic
        fun <T : BlockEntity, P> createKt(
            owner: AbstractRegistrate<*>,
            parent: P,
            name: String,
            callback: BuilderCallback,
            factory: BlockEntityFactory<T>,
        ): KotlinBlockEntityBuilder<T, P> {
            return KotlinBlockEntityBuilder(owner, parent, name, callback, factory)
        }
    }

    override fun registerRenderer() {
        OneTimeEventReceiver.addListener(
            MOD_BUS,
            FMLClientSetupEvent::class.java
        ) { _: FMLClientSetupEvent? ->
            val renderer = (this as BlockEntityBuilderMixin<T>).renderer
            if (renderer != null) {
                BlockEntityRenderers.register(entry) {
                    renderer.get().apply(it)
                }
            }
        }
    }

    override fun registerInstance() {
        OneTimeEventReceiver.addListener(
            MOD_BUS,
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
