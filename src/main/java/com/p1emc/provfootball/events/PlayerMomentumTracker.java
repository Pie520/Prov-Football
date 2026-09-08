package com.p1emc.provfootball.events;

import com.p1emc.provfootball.ProvFootball;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// Bus.GAME, NOT the default. PlayerTickEvent is a gameplay event; the mod bus
// is for lifecycle and registration. Get this wrong and the handler silently
// never fires -- no error, no warning, momentum just stays zero.
@EventBusSubscriber(modid = ProvFootball.MODID)public class PlayerMomentumTracker {

    /**
    * Dribbling rework - allows for sharp turns by tracking how long a player has been moving
    */

    // How many consecutive ticks the player has been moving in a consistent direction.

    private static final Map<UUID, Vec3> LAST_DIRECTION = new ConcurrentHashMap<>();
    private static final Map<UUID, Float> SUSTAINED = new ConcurrentHashMap<>();

    // Ticks of consistent motion to reach full commitment.
    private static final float SUSTAIN_MAX = 8.0F;

    // Below this speed you are not really moving.
    private static final double SUSTAIN_MIN_SPEED = 0.04D;

    // Dot product between this tick's direction and last tick's. Below this the
// turn counts as a cut and the build-up drops sharply.
// Gives you close control, since a sharp change of direction should produce a light
// touch even at pace.
    private static final double CUT_THRESHOLD = 0.7D;   // about 45 degrees

    // Lost per tick when stopped. Decay rather than reset, so a feint or a moment
// of hesitation does not cost you everything.
    private static final float SUSTAIN_DECAY = 1.5F;

    // How much a cut costs. Not a full reset -- you keep some momentum through a
// turn, just not much.
    private static final float CUT_PENALTY = 0.35F;

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

            if (speed > SUSTAIN_MIN_SPEED) {
                Vec3 previous = LAST_DIRECTION.get(id);
                Vec3 direction = movement.normalize();

                if (previous != null && previous.dot(direction) < CUT_THRESHOLD) {
                    // Sharp turn -- most of the build-up goes.
                    sustained *= CUT_PENALTY;
                }

                sustained = Math.min(sustained + 1.0F, SUSTAIN_MAX);
                LAST_DIRECTION.put(id, direction);
            } else {
                sustained = Math.max(0.0F, sustained - SUSTAIN_DECAY);
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
        return SUSTAINED.getOrDefault(player.getUUID(), 0.0F) / SUSTAIN_MAX;
    }
}