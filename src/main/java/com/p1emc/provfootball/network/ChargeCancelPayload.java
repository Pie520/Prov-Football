package com.p1emc.provfootball.network;

import com.p1emc.provfootball.ProvFootball;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

// Server to client. No payload -- the message is the signal.
public record ChargeCancelPayload() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ChargeCancelPayload> TYPE =
            new CustomPacketPayload.Type<>(ProvFootball.id("charge_cancel"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChargeCancelPayload> STREAM_CODEC =
            StreamCodec.unit(new ChargeCancelPayload());

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}