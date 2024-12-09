package io.github.cotrin8672.createenchantablemachinery.forge.mixin;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.backend.instancing.InstancedRenderRegistry;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.simibubi.create.foundation.data.CreateBlockEntityBuilder;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BlockEntityBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.util.OneTimeEventReceiver;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.NonNullPredicate;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.BiFunction;

@Mixin(value = CreateBlockEntityBuilder.class, remap = false)
public abstract class CreateBlockEntityBuilderMixin<T extends BlockEntity, P> extends BlockEntityBuilder<T, P> {
    @Unique
    private AbstractRegistrate<?> createEnchantableMachinery$owner;

    protected CreateBlockEntityBuilderMixin(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, BlockEntityFactory<T> factory) {
        super(owner, parent, name, callback, factory);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onConstructor(
            AbstractRegistrate<?> owner,
            Object parent,
            String name,
            BuilderCallback callback,
            BlockEntityFactory<T> factory,
            CallbackInfo ci
    ) {
        createEnchantableMachinery$owner = owner;
    }

    @Shadow
    @Nullable
    private NonNullSupplier<BiFunction<MaterialManager, T, BlockEntityInstance<? super T>>> instanceFactory;

    @Shadow
    private NonNullPredicate<T> renderNormally;

    @Inject(method = "registerInstance", at = @At("HEAD"), cancellable = true)
    private void onRegisterInstance(CallbackInfo ci) {
        OneTimeEventReceiver.addModListener(createEnchantableMachinery$owner, FMLClientSetupEvent.class, $ -> {
            InstancedRenderRegistry.configure(getEntry())
                    .factory(Objects.requireNonNull(instanceFactory).get())
                    .skipRender(be -> !renderNormally.test(be))
                    .apply();
        });

        ci.cancel();
    }
}
