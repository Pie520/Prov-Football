package com.p1emc.provfootball.client;

import com.p1emc.provfootball.ChargeConstants;
import com.p1emc.provfootball.ProvFootball;
import com.p1emc.provfootball.network.ChargeReleasePayload;
import com.p1emc.provfootball.network.ChargeStartPayload;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import static com.p1emc.provfootball.ChargeConstants.DECAY_PER_TICK;

@EventBusSubscriber(modid = ProvFootball.MODID, value = Dist.CLIENT)
public class ChargeTracker {

    private static int charge;
    private static boolean wasHeld;
    private static int grace;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {

        boolean held = ModKeyBindings.CHARGE_SHOT.isDown();

        // Fire on the TRANSITION, not while held -- otherwise you send a packet
        // every tick for as long as the key is down.
        if (held && !wasHeld) {
            float pitch = Minecraft.getInstance().player.getXRot();
            PacketDistributor.sendToServer(new ChargeStartPayload(pitch));
        }

        if (!held && wasHeld && charge >= ChargeConstants.MIN_CHARGE) {
            PacketDistributor.sendToServer(new ChargeReleasePayload(charge));
            grace = ChargeConstants.RELEASE_GRACE;
        }

        wasHeld = held;

        if (held) {
            grace = 0;
            if (charge < ChargeConstants.MAX_CHARGE) {
                charge++;
            }
        } else if (grace > 0) {
            grace--;              // charge holds steady, mirroring the server
        } else if (charge > 0) {
            charge = Math.max(0, charge - ChargeConstants.DECAY_PER_TICK);
        }
    }

    public static int getCharge() {
        return charge;
    }

    public static float getProgress() {
        return (float) charge / ChargeConstants.MAX_CHARGE;
    }

    public static void cancel() {
        charge = 0;
        // Prevents the release check firing on the next tick and sending a stale
        // charge value back to the server.
        wasHeld = false;
    }
}