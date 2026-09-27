package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import com.simibubi.create.content.kinetics.drill.DrillActorVisual;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.material.Materials;
import dev.engine_room.flywheel.lib.model.baked.BakedModelBuilder;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrillActorVisual.class)
public abstract class DrillActorVisualMixin extends ActorVisual {
    @Shadow
    TransformedInstance drillHead;

    @Unique
    private TransformedInstance cem$glintHead;

    protected DrillActorVisualMixin(
            VisualizationContext visualizationContext,
            BlockAndTintGetter world,
            MovementContext movementContext
    ) {
        super(visualizationContext, world, movementContext);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void cem$creatGlint(
            VisualizationContext visualizationContext,
            VirtualRenderWorld contraption,
            MovementContext context, CallbackInfo ci
    ) {
        var blockEntity = contraption.getBlockEntity(context.localPos);
        if (blockEntity == null) {
            return;
        }

        ItemEnchantments enchantments = blockEntity.components().get(DataComponents.ENCHANTMENTS);
        if (enchantments == null || enchantments.isEmpty()) {
            return;
        }

        cem$glintHead = instancerProvider.instancer(
                InstanceTypes.TRANSFORMED,
                new BakedModelBuilder(AllPartialModels.DRILL_HEAD.get())
                        .materialFunc((layer, shaded, ambientOcclusion) -> Materials.GLINT)
                        .build()
        ).createInstance();
    }

    @Inject(method = "beginFrame", at = @At("TAIL"))
    private void cem$syncGlint(CallbackInfo ci) {
        if (cem$glintHead != null) {
            cem$glintHead
                    .setTransform(drillHead.pose)
                    .setChanged();
        }
    }

    @Inject(method = "_delete", at = @At("TAIL"))
    private void cem$deleteGlint(CallbackInfo ci) {
        if (cem$glintHead != null)
            cem$glintHead.delete();
    }
}
