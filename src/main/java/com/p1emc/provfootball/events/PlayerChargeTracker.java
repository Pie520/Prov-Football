package com.p1emc.provfootball.events;

import com.p1emc.provfootball.ChargeConstants;
import com.p1emc.provfootball.ProvFootball;
import com.p1emc.provfootball.network.ChargeCancelPayload;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector3f;

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

    private static final Map<UUID, Integer> HELD_TICKS = new ConcurrentHashMap<>();

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
        HELD_TICKS.remove(id);
        clearSlowdown(player);
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

        if (isCharging(player)) {
            int held = HELD_TICKS.merge(id, 1, Integer::sum);

            //Tick limit to prevent trails (timeout)
            if (held > ChargeConstants.MAX_CHARGE + 300) {
                setCharging(player, false);
                HELD_TICKS.remove(id);
                clearSlowdown(player);
            }else {
                //Slowdown Modifier
                applySlowdown(player, held);

                if (player.level() instanceof ServerLevel serverLevel) {
                    spawnChargeParticles(serverLevel, player, held);
                }
            }
        } else {
            HELD_TICKS.remove(id);
            clearSlowdown(player);
        }


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



    // Ring radius around the player's feet. Wide enough to read from across the
// pitch without swallowing them.
    private static final double PARTICLE_RADIUS = 0.55D;
    public static final float ZONE_AMBER = 0.34F;
    public static final float ZONE_RED = 0.67F;

    /**
     * Charge particle system
     */




    private static void spawnChargeParticles(ServerLevel level, Player player, int heldTicks) {
        float progress = Mth.clamp(
                (float) heldTicks / ChargeConstants.MAX_CHARGE, 0.0F, 1.0F);

        // Nothing until the shot is actually viable
        if (heldTicks < ChargeConstants.MIN_CHARGE) {
            return;
        }

        // One particle at minimum, four at full.
        int count = 1 + Math.round(progress * 3.0F);

        for (int i = 0; i < count; i++) {
            // Spread them round the player rather than stacking on one side.
            double angle = level.random.nextDouble() * Math.PI * 2.0D;
            double x = player.getX() + Math.cos(angle) * PARTICLE_RADIUS;
            double z = player.getZ() + Math.sin(angle) * PARTICLE_RADIUS;

            // Rises up the body as the charge builds
            double y = player.getY() + 0.1D + progress * level.random.nextDouble() * 1.2D;

// Progress through the USABLE range

            float usable = (float) (heldTicks - ChargeConstants.MIN_CHARGE)
                    / (ChargeConstants.MAX_CHARGE - ChargeConstants.MIN_CHARGE);

            float r, g, b;
            if (usable >= ZONE_RED) {
                r = 1.0F; g = 0.2F; b = 0.2F;
            } else if (usable >= ZONE_AMBER) {
                r = 1.0F; g = 0.75F; b = 0.15F;
            } else {
                r = 0.25F; g = 1.0F; b = 0.3F;
            }

            DustParticleOptions dust = new DustParticleOptions(new Vector3f(r, g, b), 1.2F);
            level.sendParticles(dust, x, y, z, 1, 0.0D, 0.04D, 0.0D, 0.0D);

        }
    }


    // Modifier id
    private static final ResourceLocation SLOWDOWN_ID = ProvFootball.id("charge_slowdown");

    /**
     * Cost of power shots, the stronger the shot the slower and less agile the player becomes
     */
    private static void applySlowdown(Player player, int heldTicks) {
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) {
            return;
        }


        speed.removeModifier(SLOWDOWN_ID);

        float progress = Mth.clamp(
                (float) heldTicks / ChargeConstants.MAX_CHARGE, 0.0F, 1.0F);

        // Squared, not linear: near-free at the start, punishing at the end.
        double factor = -ChargeConstants.MAX_SLOWDOWN * progress * progress;

        speed.addTransientModifier(new AttributeModifier(
                SLOWDOWN_ID, factor,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }

    private static void clearSlowdown(Player player) {
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            speed.removeModifier(SLOWDOWN_ID);
        }
    }





}