package com.caferino.coupdegrace.network;

import com.caferino.coupdegrace.client.CoupOverlayRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CoupEffectPacket {
    private final int duration;
    private final float peakAlpha;

    public CoupEffectPacket(int duration, float peakAlpha) {
        this.duration = duration;
        this.peakAlpha = peakAlpha;
    }

    public CoupEffectPacket(FriendlyByteBuf buf) {
        this.duration = buf.readInt();
        this.peakAlpha = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.duration);
        buf.writeFloat(this.peakAlpha);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> CoupOverlayRenderer.triggerEffect(this.duration, this.peakAlpha));
        context.setPacketHandled(true);
    }
}