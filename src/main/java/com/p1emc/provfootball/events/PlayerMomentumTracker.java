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

    // Keyed by UUID rather than holding Player objects, so a disconnecting
    // player can be garbage collected. Concurrent because the integrated
    // server and client run on different threads.
    private static final Map<UUID, Vec3> LAST_POS = new ConcurrentHashMap<>();
    private static final Map<UUID, Vec3> MOMENTUM = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        // Server only. The client tracks its own copy of every player and the
        // two would disagree -- we want the authoritative one.
        if (player.level().isClientSide()) {
            return;
        }

        UUID id = player.getUUID();
        Vec3 now = player.position();
        Vec3 last = LAST_POS.get(id);

        // Skipped on the first tick after joining, when there is no previous
        // position to diff against. get() returns ZERO until then.
        if (last != null) {
            // Horizontal only. Falling should not add throw distance.
            MOMENTUM.put(id, new Vec3(now.x - last.x, 0.0D, now.z - last.z));
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
    }



    // What everything else calls. Works from any server-side code with a
    // Player -- throws now, kicks and dribbling later.
    public static Vec3 get(Player player) {
        return MOMENTUM.getOrDefault(player.getUUID(), Vec3.ZERO);
    }
}