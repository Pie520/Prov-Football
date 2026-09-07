package com.p1emc.provfootball.network;

import com.p1emc.provfootball.ProvFootball;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ChargeReleasePayload(int ticks) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ChargeReleasePayload> TYPE =
            new CustomPacketPayload.Type<>(ProvFootball.id("charge_release"));


    public static final StreamCodec<RegistryFriendlyByteBuf, ChargeReleasePayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, ChargeReleasePayload::ticks,
                    ChargeReleasePayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}