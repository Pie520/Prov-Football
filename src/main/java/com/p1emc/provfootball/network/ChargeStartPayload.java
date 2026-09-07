package com.p1emc.provfootball.network;

import com.p1emc.provfootball.ProvFootball;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

// Carries the player's pitch at the moment charging began. Elevation has to be
// captured here because at the moment of the strike the crosshair is forced
// down onto the ball, so look.y cannot express "aim for the top corner".
public record ChargeStartPayload(float pitch) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ChargeStartPayload> TYPE =
            new CustomPacketPayload.Type<>(ProvFootball.id("charge_start"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChargeStartPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.FLOAT, ChargeStartPayload::pitch,
                    ChargeStartPayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}