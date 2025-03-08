package io.github.cotrin8672.cem.mixin;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerGamePacketListenerImpl.class)
public interface ServerGamePacketListenerImplMixin {
    @Accessor("aboveGroundTickCount")
    void setAboveGroundTickCount(int value);
}
