package com.p1emc.provfootball.client;

import com.p1emc.provfootball.ProvFootball;
import com.p1emc.provfootball.config.ConfigCache;
import com.p1emc.provfootball.network.ChargeReleasePayload;
import com.p1emc.provfootball.network.ChargeStartPayload;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = ProvFootball.MODID, value = Dist.CLIENT)
public class ChargeTracker {

    private static int charge;
    private static boolean wasHeld;
    private static int grace;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {

        boolean held = ModKeyBindings.CHARGE_SHOT.isDown();

        if (held && !wasHeld) {

            charge = 0;
            grace = 0;
            float pitch = Minecraft.getInstance().player.getXRot();
            PacketDistributor.sendToServer(new ChargeStartPayload(pitch));
        }

        if (!held && wasHeld) {
            PacketDistributor.sendToServer(new ChargeReleasePayload(charge));
            grace = ConfigCache.releaseGrace;
        }

        wasHeld = held;

        if (held) {
            grace = 0;
            if (charge < ConfigCache.maxCharge) {
                charge++;
            }
        } else if (grace > 0) {
            grace--;              // charge holds steady, mirroring the server
        } else if (charge > 0) {
            charge = Math.max(0, charge - ConfigCache.decayPerTick);
        }
    }

    public static int getCharge() {
        return charge;
    }

    public static float getProgress() {
        return (float) charge / ConfigCache.maxCharge;
    }

    public static void cancel() {
        charge = 0;
        // Prevents the release check firing on the next tick and sending a stale
        // charge value back to the server.
        wasHeld = false;
    }
}