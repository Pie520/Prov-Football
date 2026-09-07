package com.p1emc.provfootball.network;

import com.p1emc.provfootball.ProvFootball;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ChargeStartPayload() implements CustomPacketPayload {


    public static final CustomPacketPayload.Type<ChargeStartPayload> TYPE =
            new CustomPacketPayload.Type<>(ProvFootball.id("charge_start"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChargeStartPayload> STREAM_CODEC =
            StreamCodec.unit(new ChargeStartPayload());

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}