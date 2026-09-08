package com.p1emc.provfootball.config;

import com.p1emc.provfootball.ProvFootball;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;

/**
 * Plain copies of every config value, refreshed on load and reload.
 *
 * ConfigValue.get() is a map lookup plus unboxing every time. That is fine
 * occasionally and wasteful in tick(), which reads a dozen of these twenty
 * times a second per ball. Copying once per config change keeps the hot path
 * as fast as the constants it replaced.
 *
 * Everything is public and mutable by design -- this is a cache, not state.
 */
@EventBusSubscriber(modid = ProvFootball.MODID)
public class ConfigCache {

    // --- physics ---
    public static double gravity;
    public static double dragCoefficient;
    public static double groundFriction;
    public static double maxSpeed;

    // --- bouncing ---
    public static double bounceVertical;
    public static double bounceFalloff;
    public static double bounceMin;
    public static double bounceHorizontal;
    public static double bounceCrossAxis;
    public static double wallCrossAxis;

    // --- striking ---
    public static double strikePower;
    public static double sprintMomentum;
    public static double walkMomentum;
    public static double incomingBlend;
    public static double volleyLift;
    public static double flickLift;
    public static double flickHorizontalKeep;
    public static int kickCooldown;
    public static int spawnGrace;

    // --- curve ---
    public static double strikeDeadzone;
    public static double spinPower;
    public static float spinDecay;

    // --- shooting ---
    public static double shotPowerMin;
    public static double shotPowerMax;
    public static double shotLift;

    // --- charge ---
    public static int maxCharge;
    public static int minCharge;
    public static int decayPerTick;
    public static int releaseGrace;
    public static double maxSlowdown;
    public static float zoneAmber;
    public static float zoneRed;

    // --- dribbling ---
    public static double dribblePushWalk;
    public static double dribblePushSprint;
    public static double dribbleRetain;
    public static double dribbleMovementBias;
    public static double dribbleMinTouch;
    public static double dribbleReach;
    public static double dribbleMaxBallSpeed;
    public static double dribbleMaxHeight;
    public static int dribbleCooldown;

    // --- momentum ---
    public static float sustainMax;
    public static double sustainMinSpeed;
    public static float sustainDecay;
    public static double cutThreshold;
    public static float cutPenalty;

    // --- heading ---
    public static double headerRestitution;
    public static double headerPower;
    public static double headerBandAbove;
    public static double headerBandBelow;
    public static double headerApproachDot;
    public static double headerReach;
    public static int headerCooldown;

    // --- shielding ---
    public static double shieldMinMultiplier;
    public static double shieldRadiusSqr;

    // --- item ---
    public static double throwSpeed;
    public static double throwMomentumScale;
    public static int pickupCooldown;

    @SubscribeEvent
    static void onLoad(ModConfigEvent.Loading event) {
        refresh();
    }

    @SubscribeEvent
    static void onReload(ModConfigEvent.Reloading event) {
        refresh();
    }

    /** Also called by the reload command, hence public. */
    public static void refresh() {
        gravity = ProvFootballConfig.GRAVITY.get();
        dragCoefficient = ProvFootballConfig.DRAG_COEFFICIENT.get();
        groundFriction = ProvFootballConfig.GROUND_FRICTION.get();
        maxSpeed = ProvFootballConfig.MAX_SPEED.get();

        bounceVertical = ProvFootballConfig.BOUNCE_VERTICAL.get();
        bounceFalloff = ProvFootballConfig.BOUNCE_FALLOFF.get();
        bounceMin = ProvFootballConfig.BOUNCE_MIN.get();
        bounceHorizontal = ProvFootballConfig.BOUNCE_HORIZONTAL.get();
        bounceCrossAxis = ProvFootballConfig.BOUNCE_CROSS_AXIS.get();
        wallCrossAxis = ProvFootballConfig.WALL_CROSS_AXIS.get();

        strikePower = ProvFootballConfig.STRIKE_POWER.get();
        sprintMomentum = ProvFootballConfig.SPRINT_MOMENTUM.get();
        walkMomentum = ProvFootballConfig.WALK_MOMENTUM.get();
        incomingBlend = ProvFootballConfig.INCOMING_BLEND.get();
        volleyLift = ProvFootballConfig.VOLLEY_LIFT.get();
        flickLift = ProvFootballConfig.FLICK_LIFT.get();
        flickHorizontalKeep = ProvFootballConfig.FLICK_HORIZONTAL_KEEP.get();
        kickCooldown = ProvFootballConfig.KICK_COOLDOWN.get();
        spawnGrace = ProvFootballConfig.SPAWN_GRACE.get();

        strikeDeadzone = ProvFootballConfig.STRIKE_DEADZONE.get();
        spinPower = ProvFootballConfig.SPIN_POWER.get();
        spinDecay = ProvFootballConfig.SPIN_DECAY.get().floatValue();

        shotPowerMin = ProvFootballConfig.SHOT_POWER_MIN.get();
        shotPowerMax = ProvFootballConfig.SHOT_POWER_MAX.get();
        shotLift = ProvFootballConfig.SHOT_LIFT.get();

        maxCharge = ProvFootballConfig.MAX_CHARGE.get();
        minCharge = ProvFootballConfig.MIN_CHARGE.get();
        decayPerTick = ProvFootballConfig.DECAY_PER_TICK.get();
        releaseGrace = ProvFootballConfig.RELEASE_GRACE.get();
        maxSlowdown = ProvFootballConfig.MAX_SLOWDOWN.get();
        zoneAmber = ProvFootballConfig.ZONE_AMBER.get().floatValue();
        zoneRed = ProvFootballConfig.ZONE_RED.get().floatValue();

        dribblePushWalk = ProvFootballConfig.DRIBBLE_PUSH_WALK.get();
        dribblePushSprint = ProvFootballConfig.DRIBBLE_PUSH_SPRINT.get();
        dribbleRetain = ProvFootballConfig.DRIBBLE_RETAIN.get();
        dribbleMovementBias = ProvFootballConfig.DRIBBLE_MOVEMENT_BIAS.get();
        dribbleMinTouch = ProvFootballConfig.DRIBBLE_MIN_TOUCH.get();
        dribbleReach = ProvFootballConfig.DRIBBLE_REACH.get();
        dribbleMaxBallSpeed = ProvFootballConfig.DRIBBLE_MAX_BALL_SPEED.get();
        dribbleMaxHeight = ProvFootballConfig.DRIBBLE_MAX_HEIGHT.get();
        dribbleCooldown = ProvFootballConfig.DRIBBLE_COOLDOWN.get();

        sustainMax = ProvFootballConfig.SUSTAIN_MAX.get().floatValue();
        sustainMinSpeed = ProvFootballConfig.SUSTAIN_MIN_SPEED.get();
        sustainDecay = ProvFootballConfig.SUSTAIN_DECAY.get().floatValue();
        cutThreshold = ProvFootballConfig.CUT_THRESHOLD.get();
        cutPenalty = ProvFootballConfig.CUT_PENALTY.get().floatValue();

        headerRestitution = ProvFootballConfig.HEADER_RESTITUTION.get();
        headerPower = ProvFootballConfig.HEADER_POWER.get();
        headerBandAbove = ProvFootballConfig.HEADER_BAND_ABOVE.get();
        headerBandBelow = ProvFootballConfig.HEADER_BAND_BELOW.get();
        headerApproachDot = ProvFootballConfig.HEADER_APPROACH_DOT.get();
        headerReach = ProvFootballConfig.HEADER_REACH.get();
        headerCooldown = ProvFootballConfig.HEADER_COOLDOWN.get();

        shieldMinMultiplier = ProvFootballConfig.SHIELD_MIN_MULTIPLIER.get();

        // Squared once here so the distance check in shieldMultiplier can skip
        // a square root every tick.
        double radius = ProvFootballConfig.SHIELD_RADIUS.get();
        shieldRadiusSqr = radius * radius;

        throwSpeed = ProvFootballConfig.THROW_SPEED.get();
        throwMomentumScale = ProvFootballConfig.THROW_MOMENTUM_SCALE.get();
        pickupCooldown = ProvFootballConfig.PICKUP_COOLDOWN.get();

    }
}