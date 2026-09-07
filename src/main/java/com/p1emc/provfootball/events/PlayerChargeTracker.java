package com.p1emc.provfootball.events;

import com.p1emc.provfootball.ChargeConstants;
import com.p1emc.provfootball.ProvFootball;
import com.p1emc.provfootball.network.ChargeCancelPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


@EventBusSubscriber(modid = ProvFootball.MODID)
public class PlayerChargeTracker {

    // Charge as the SERVER understands it. Set when a release packet arrives,
    // read by FootballEntity.hurt(), cleared once spent.
    private static final Map<UUID, Integer> CHARGE = new ConcurrentHashMap<>();

    // Whether they are mid-charge. Drives the slowdown and the particles other
    // players see -- that's the telegraph that makes shots interruptible.
    private static final Map<UUID, Boolean> CHARGING = new ConcurrentHashMap<>();

    private static final Map<UUID, Integer> GRACE = new ConcurrentHashMap<>();

    private static final Map<UUID, Float> START_PITCH = new ConcurrentHashMap<>();

    public static void setStartPitch(Player player, float pitch) {
        START_PITCH.put(player.getUUID(), pitch);
    }

    public static float getStartPitch(Player player) {
        return START_PITCH.getOrDefault(player.getUUID(), 0.0F);
    }


    public static void setCharging(Player player, boolean charging) {
        CHARGING.put(player.getUUID(), charging);
    }

    public static boolean isCharging(Player player) {
        return CHARGING.getOrDefault(player.getUUID(), false);
    }

    // Clamped by the caller, not here -- the handler is where untrusted input
    // arrives, so that's where it gets sanitised.
    public static void setCharge(Player player, int ticks) {
        GRACE.put(player.getUUID(), ChargeConstants.RELEASE_GRACE);
        CHARGE.put(player.getUUID(), ticks);
    }


    public static int getCharge(Player player) {
        return CHARGE.getOrDefault(player.getUUID(), 0);
    }

    // Called after a shot so one charge cannot fire twice.
    public static void clear(Player player) {
        UUID id = player.getUUID();
        CHARGE.remove(id);
        CHARGING.remove(id);
        GRACE.remove(id);
        START_PITCH.remove(id);
    }





    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        clear(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;

        UUID id = player.getUUID();

        Integer grace = GRACE.get(id);
        if (grace != null && grace > 0) {
            GRACE.put(id, grace - 1);
            return;   // charge holds at full while grace lasts
        }

        Integer current = CHARGE.get(id);
        if (current != null && current > 0) {
            int next = current - ChargeConstants.DECAY_PER_TICK;
            if (next <= 0) {
                CHARGE.remove(id);
            } else {
                CHARGE.put(id, next);
            }
        }
    }




    @SubscribeEvent
    public static void onHurt(LivingDamageEvent.Post event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        if (CHARGE.containsKey(player.getUUID()) || isCharging(player)) {
            clear(player);

            if (player instanceof ServerPlayer serverPlayer) {
                PacketDistributor.sendToPlayer(serverPlayer, new ChargeCancelPayload());
            }
        }
    }






}