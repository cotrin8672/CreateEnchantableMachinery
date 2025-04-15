package io.github.cotrin8672.cem.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.foundation.element.WorldSectionElementImpl;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WorldSectionElementImpl.class, remap = false)
public abstract class WorldSectionElementImplMixin {
    @Shadow
    protected abstract void renderBlockEntities(PonderLevel world, PoseStack ms, MultiBufferSource buffer, float pt);

    @Shadow
    public abstract void transformMS(PoseStack ms, float pt);

    @Inject(method = "renderLast", at = @At("HEAD"))
    private void cem$renderLayer(
            PonderLevel world,
            MultiBufferSource buffer,
            GuiGraphics graphics,
            float fade,
            float pt,
            CallbackInfo ci
    ) {
        PoseStack poseStack = graphics.pose();

        int light = -1;
        if (fade != 1)
            light = (int) (Mth.lerp(fade, 5, 15));

        poseStack.pushPose();
        transformMS(poseStack, pt);
        world.pushFakeLight(light);
        renderBlockEntities(world, poseStack, buffer, pt);
        world.popLight();
        poseStack.popPose();
    }
}
