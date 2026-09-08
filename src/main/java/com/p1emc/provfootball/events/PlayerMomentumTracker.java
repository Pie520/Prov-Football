package com.p1emc.provfootball.events;

import com.p1emc.provfootball.ProvFootball;
import com.p1emc.provfootball.config.ConfigCache;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = ProvFootball.MODID)
public class PlayerMomentumTracker {

    /**
     * Dribbling rework - allows for sharp turns by tracking how long a player has been moving
     */

    private static final Map<UUID, Vec3> LAST_DIRECTION = new ConcurrentHashMap<>();
    private static final Map<UUID, Float> SUSTAINED = new ConcurrentHashMap<>();

    // Keyed by UUID rather than holding Player objects, so a disconnecting
    // player can be garbage collected. Concurrent because the integrated
    // server and client run on different threads.
    private static final Map<UUID, Vec3> LAST_POS = new ConcurrentHashMap<>();
    private static final Map<UUID, Vec3> MOMENTUM = new ConcurrentHashMap<>();


    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        // Server only. The client tracks its own copy of every player and the
        // two would disagree, we want the authoritative one.
        if (player.level().isClientSide()) {
            return;
        }

        UUID id = player.getUUID();
        Vec3 now = player.position();
        Vec3 last = LAST_POS.get(id);

        // Skipped on the first tick after joining, when there is no previous
        // position to diff against. Without this guard, last is null and the
        // subtraction below throws.
        if (last != null) {
            Vec3 movement = new Vec3(now.x - last.x, 0.0D, now.z - last.z);
            MOMENTUM.put(id, movement);

            float sustained = SUSTAINED.getOrDefault(id, 0.0F);
            double speed = movement.length();

            if (speed > ConfigCache.sustainMinSpeed) {
                Vec3 previous = LAST_DIRECTION.get(id);
                Vec3 direction = movement.normalize();

                if (previous != null && previous.dot(direction) < ConfigCache.cutThreshold) {
                    // Sharp turn -- most of the build-up goes.
                    sustained *= ConfigCache.cutPenalty;
                }

                sustained = Math.min(sustained + 1.0F, ConfigCache.sustainMax);
                LAST_DIRECTION.put(id, direction);
            } else {
                sustained = Math.max(0.0F, sustained - ConfigCache.sustainDecay);
            }

            SUSTAINED.put(id, sustained);
        }

        LAST_POS.put(id, now);
    }

    // Without this the maps grow forever with stale UUIDs. Harmless in
    // singleplayer; a slow leak on a server that runs for months.
    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID id = event.getEntity().getUUID();
        LAST_POS.remove(id);
        MOMENTUM.remove(id);
        LAST_DIRECTION.remove(id);
        SUSTAINED.remove(id);
    }



    // What everything else calls. Works from any server-side code with a
    // Player -- throws now, kicks and dribbling later.
    public static Vec3 get(Player player) {
        return MOMENTUM.getOrDefault(player.getUUID(), Vec3.ZERO);
    }

    // 0.0 to 1.0, how committed the player's current run is.
    public static float getSustained(Player player) {
        return SUSTAINED.getOrDefault(player.getUUID(), 0.0F) / ConfigCache.sustainMax;
    }
}