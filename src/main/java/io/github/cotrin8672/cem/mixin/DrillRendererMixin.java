package io.github.cotrin8672.cem.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.kinetics.drill.DrillRenderer;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import io.github.cotrin8672.cem.client.CustomRenderType;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrillRenderer.class)
public class DrillRendererMixin {
    @Inject(method = "renderInContraption", at = @At("TAIL"))
    private static void cem$renderGlint(
            MovementContext context,
            VirtualRenderWorld renderWorld,
            ContraptionMatrices matrices,
            MultiBufferSource buffer,
            CallbackInfo ci,
            @Local(name = "superBuffer") SuperByteBuffer superBuffer
    ) {
        VertexConsumer consumer = new SheetedDecalTextureGenerator(
                buffer.getBuffer(CustomRenderType.getGLINT()),
                matrices.getViewProjection().last(),
                0.0078125f
        );

        superBuffer.renderInto(matrices.getViewProjection(), consumer);
    }
}
