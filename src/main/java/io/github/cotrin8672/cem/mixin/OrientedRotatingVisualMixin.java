package io.github.cotrin8672.cem.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.material.Materials;
import dev.engine_room.flywheel.lib.model.SimpleModel;
import dev.engine_room.flywheel.lib.model.baked.BlockModelBuilder;
import io.github.cotrin8672.cem.client.CemMaterials;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Consumer;

@Mixin(OrientedRotatingVisual.class)
public abstract class OrientedRotatingVisualMixin<T extends KineticBlockEntity> extends KineticBlockEntityVisual<T> {
    @Unique
    private RotatingInstance cem$glintModel;

    @Unique
    private TransformedInstance cem$blockGlint;

    @Unique
    private Model cem$sourceModel;

    @Unique
    private Direction cem$from;

    @Unique
    private Direction cem$to;

    protected OrientedRotatingVisualMixin(VisualizationContext context, T blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void cem$addGlint(
            VisualizationContext context,
            T blockEntity,
            float partialTick,
            Direction from,
            Direction to,
            Model model,
            CallbackInfo ci
    ) {
        cem$sourceModel = model;
        cem$from = from;
        cem$to = to;
        cem$ensureGlint();
    }

    @Unique
    private void cem$ensureGlint() {
        ItemEnchantments enchantments = blockEntity.components().get(DataComponents.ENCHANTMENTS);
        if (enchantments == null || enchantments.isEmpty()) {
            return;
        }

        if (cem$blockGlint == null && blockEntity.getLevel() != null) {
            BlockPos blockPos = blockEntity.getBlockPos();
            PoseStack poseStack = new PoseStack();
            poseStack.translate(-blockPos.getX(), -blockPos.getY(), -blockPos.getZ());

            Model blockGlintModel = new BlockModelBuilder(blockEntity.getLevel(), List.of(blockPos))
                    .poseStack(poseStack)
                    .materialFunc((renderType, shaded, ambientOcclusion) -> CemMaterials.BLOCK_GLINT)
                    .build();

            cem$blockGlint = instancerProvider()
                    .instancer(InstanceTypes.TRANSFORMED, blockGlintModel)
                    .createInstance();
            cem$blockGlint
                    .setIdentityTransform()
                    .translate(getVisualPosition())
                    .light(computePackedLight());
            cem$blockGlint.setChanged();
        }

        if (cem$glintModel == null && cem$sourceModel != null && cem$from != null && cem$to != null) {
            Model glintModel = new SimpleModel(cem$sourceModel.meshes().stream()
                    .map(configuredMesh -> new Model.ConfiguredMesh(Materials.GLINT, configuredMesh.mesh()))
                    .toList());

            cem$glintModel = instancerProvider().instancer(AllInstanceTypes.ROTATING, glintModel)
                    .createInstance()
                    .rotateToFace(cem$from, cem$to)
                    .setup(blockEntity)
                    .setPosition(getVisualPosition());
            cem$glintModel.setChanged();
        }
    }

    @Inject(method = "update", at = @At("TAIL"))
    private void cem$updateGlint(float partialTick, CallbackInfo ci) {
        cem$ensureGlint();
        if (cem$glintModel != null) {
            cem$glintModel.setup(blockEntity).setChanged();
        }
    }

    @Inject(method = "updateLight", at = @At("TAIL"))
    private void cem$updateGlintLight(float partialTick, CallbackInfo ci) {
        if (cem$glintModel != null) {
            relight(cem$glintModel);
        }
        if (cem$blockGlint != null) {
            relight(cem$blockGlint);
        }
    }

    @Inject(method = "_delete", at = @At("TAIL"))
    private void cem$deleteGlint(CallbackInfo ci) {
        if (cem$glintModel != null) {
            cem$glintModel.delete();
        }
        if (cem$blockGlint != null) {
            cem$blockGlint.delete();
        }
    }

    @Inject(method = "collectCrumblingInstances", at = @At("TAIL"))
    private void cem$collectGlintCrumblingInstance(Consumer<Instance> consumer, CallbackInfo ci) {
        if (cem$glintModel != null) {
            consumer.accept(cem$glintModel);
        }
        if (cem$blockGlint != null) {
            consumer.accept(cem$blockGlint);
        }
    }
}
