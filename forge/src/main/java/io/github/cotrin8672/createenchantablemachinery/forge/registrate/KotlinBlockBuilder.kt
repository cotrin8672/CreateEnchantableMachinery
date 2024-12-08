package io.github.cotrin8672.createenchantablemachinery.forge.registrate

import com.tterrag.registrate.AbstractRegistrate
import com.tterrag.registrate.builders.BlockBuilder
import com.tterrag.registrate.builders.BuilderCallback
import com.tterrag.registrate.util.OneTimeEventReceiver
import com.tterrag.registrate.util.nullness.NonNullFunction
import com.tterrag.registrate.util.nullness.NonNullSupplier
import io.github.cotrin8672.createenchantablemachinery.forge.mixin.BlockBuilderMixin
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.Material
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.ColorHandlerEvent
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import java.util.function.Supplier
import java.util.stream.Collectors

class KotlinBlockBuilder<T : Block, P>
private constructor(
    owner: AbstractRegistrate<*>,
    parent: P & Any,
    name: String,
    callback: BuilderCallback,
    factory: NonNullFunction<BlockBehaviour.Properties, T>,
    initialProperties: NonNullSupplier<BlockBehaviour.Properties>,
) : BlockBuilder<T, P>(owner, parent, name, callback, factory, initialProperties) {
    companion object {
        @JvmStatic
        fun <T : Block, P : Any> createKt(
            owner: AbstractRegistrate<*>,
            parent: P,
            name: String,
            callback: BuilderCallback,
            factory: NonNullFunction<BlockBehaviour.Properties, T>,
            material: Material,
        ): KotlinBlockBuilder<T, P> {
            return KotlinBlockBuilder(owner, parent, name, callback, factory) {
                BlockBehaviour.Properties.of(material)
            }
        }
    }

    override fun registerLayers(entry: T) {
        DistExecutor.runWhenOn(Dist.CLIENT) {
            val renderLayers = (this as BlockBuilderMixin).renderLayers
            Runnable {
                OneTimeEventReceiver.addListener(MOD_BUS, FMLClientSetupEvent::class.java) { _: FMLClientSetupEvent ->
                    if (renderLayers.size == 1) {
                        val layer = renderLayers[0].get().get()
                        ItemBlockRenderTypes.setRenderLayer(entry, layer)
                    } else if (renderLayers.size > 1) {
                        val layers = renderLayers.stream()
                            .map { s: Supplier<Supplier<RenderType?>> ->
                                s.get().get()
                            }
                            .collect(Collectors.toSet())
                        ItemBlockRenderTypes.setRenderLayer(entry) { o: RenderType ->
                            layers.contains(o)
                        }
                    }
                }
            }
        }
    }

    override fun registerBlockColor() {
        OneTimeEventReceiver.addModListener(
            ColorHandlerEvent.Block::class.java
        ) { e: ColorHandlerEvent.Block ->
            val colorHandler = (this as BlockBuilderMixin).colorHandler
            if (colorHandler != null) {
                e.blockColors.register(colorHandler.get().get(), entry)
            }
        }
    }
}
