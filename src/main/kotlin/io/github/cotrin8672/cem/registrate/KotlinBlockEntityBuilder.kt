package io.github.cotrin8672.cem.registrate

import com.simibubi.create.foundation.data.CreateBlockEntityBuilder
import com.tterrag.registrate.AbstractRegistrate
import com.tterrag.registrate.builders.BuilderCallback
import com.tterrag.registrate.util.OneTimeEventReceiver
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
import io.github.cotrin8672.cem.CreateEnchantableMachinery
import io.github.cotrin8672.cem.mixin.CreateBlockEntityBuilderMixin
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent

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

    override fun registerVisualizer() {
        OneTimeEventReceiver.addModListener(
            CreateEnchantableMachinery.REGISTRATE,
            FMLClientSetupEvent::class.java
        ) { _ ->
            val visualFactory = (this as CreateBlockEntityBuilderMixin<T>).visualFactory
            if (visualFactory != null) {
                val renderNormally = (this as CreateBlockEntityBuilderMixin<T>).renderNormally
                SimpleBlockEntityVisualizer.builder(entry)
                    .factory(visualFactory.get())
                    .skipVanillaRender { !renderNormally.test(it) }
                    .apply()
            }
        }
    }
}
