package io.github.cotrin8672.createenchantablemachinery.util;

import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.world.level.BlockAndTintGetter;
import org.joml.Matrix4f;

public class SuperBufferUtil {

    // Apply color
    public static void applyColor(SuperByteBuffer buffer, int color) {
        buffer.color(color);
    }

    // Packed light (BlockEntityRenderer)
    public static void applyPackedLight(SuperByteBuffer buffer, int light) {
        buffer.light(light);
    }

    // World light (static transform)
    public static void applyWorldLight(
            SuperByteBuffer buffer,
            BlockAndTintGetter level
    ) {
        buffer.useLevelLight(level);
    }

    // World light (moving / contraption transform)
    public static void applyWorldLight(
            SuperByteBuffer buffer,
            BlockAndTintGetter level,
            Matrix4f lightTransform
    ) {
        buffer.useLevelLight(level, lightTransform);
    }

    private SuperBufferUtil() {}
}
