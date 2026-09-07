package com.p1emc.provfootball.network;

import com.p1emc.provfootball.ChargeConstants;
import com.p1emc.provfootball.client.ClientPayloadHandler;
import com.p1emc.provfootball.events.PlayerChargeTracker;
import com.p1emc.provfootball.ProvFootball;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = ProvFootball.MODID)
public class ModNetworking {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        // The version string lets NeoForge reject clients running a mismatched
        // build rather than letting them send packets you no longer understand.
        PayloadRegistrar registrar = event.registrar("1");

        // playToServer: the client sends, the server handles. There is also
        // playToClient and playBidirectional.
        registrar.playToServer(
                ChargeStartPayload.TYPE,
                ChargeStartPayload.STREAM_CODEC,
                ModNetworking::handleStart);

        registrar.playToServer(
                ChargeReleasePayload.TYPE,
                ChargeReleasePayload.STREAM_CODEC,
                ModNetworking::handleRelease);

        // playToClient, not playToServer -- this one travels the other way.
        registrar.playToClient(
                ChargeCancelPayload.TYPE,
                ChargeCancelPayload.STREAM_CODEC,
                ClientPayloadHandler::handleCancel);
    }

    private static void handleStart(ChargeStartPayload payload, IPayloadContext context) {
        // enqueueWork hops onto the main server thread. Packet handlers run on
        // the network thread, and touching world or entity state from there is
        // a race condition waiting to happen.
        context.enqueueWork(() -> {
            Player player = context.player();
            PlayerChargeTracker.setCharging(player, true);
            float pitch = Mth.clamp(payload.pitch(), -90.0F, 90.0F);
            PlayerChargeTracker.setStartPitch(player, pitch);
        });


    }

    private static void handleRelease(ChargeReleasePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();

            // NEVER trust the number. A modified client can send anything, and
            // clamping here caps the exploit at "can always shoot at full
            // power" rather than "can shoot at 10,000 power".
            int ticks = Math.max(0, Math.min(payload.ticks(), ChargeConstants.MAX_CHARGE));

            ProvFootball.LOGGER.info("server received charge={}", ticks);

            PlayerChargeTracker.setCharge(player, ticks);
            PlayerChargeTracker.setCharging(player, false);
        });
    }
}