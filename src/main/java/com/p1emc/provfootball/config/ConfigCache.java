package com.p1emc.provfootball.config;

import com.p1emc.provfootball.ProvFootball;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

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
    public static double shotMaxElevation;

    // --- charge ---
    public static int maxCharge;
    public static int minCharge;
    public static int decayPerTick;
    public static int releaseGrace;
    public static double maxSlowdown;
    public static float zoneAmber;
    public static float zoneRed;

    // --- dribbling ---

    // --- dribbling ---

    public static double dribbleStandoffWalk;
    public static double dribbleStandoffSprint;
    public static double dribbleStandoffGainWalk;
    public static double dribbleStandoffGainSprint;
    public static double dribbleStandoffMargin;

    public static double dribbleAccel;
    public static double dribbleDecel;
    public static double dribbleMinTouch;
    public static double dribbleControlTouch;
    public static double dribbleControlMargin;

    public static double dribbleRetainLateralWalk;
    public static double dribbleRetainLateralSprint;
    public static double dribblePullWalk;
    public static double dribblePullSprint;
    public static double dribblePullMaxWalk;
    public static double dribblePullMaxSprint;
    public static double dribbleDeadzoneWalk;
    public static double dribbleDeadzoneSprint;

    public static double dribbleCloseRadius;
    public static double dribbleCloseArcDot;
    public static double dribbleCloseAuthority;
    public static double dribbleCloseRetainScale;
    public static double dribbleClosePullScale;

    public static double dribbleBehindBrake;
    public static double dribbleBehindRestSpeed;

    public static double dribbleChopYaw;
    public static double dribbleChopDecay;
    public static double dribbleChopTicks;
    public static double dribbleChopMaxSpeed;
    public static int dribbleChopCooldown;
    public static double dribbleChopMinSpeed;
    public static double dribbleChopSprintScale;

    public static double dribbleCutDot;
    public static double dribbleCutMinSpeed;
    public static int dribbleCutInterrupt;

    public static double dribbleReach;
    public static double dribbleInstantBlend;
    public static double dribbleScatter;
    public static double dribbleFrontArcDot;
    public static double dribbleSpeedCap;
    public static double dribbleMaxHeight;
    public static double dribbleStepOver;
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
        shotMaxElevation = ProvFootballConfig.SHOT_MAX_ELEVATION.get();

        maxCharge = ProvFootballConfig.MAX_CHARGE.get();
        minCharge = ProvFootballConfig.MIN_CHARGE.get();
        decayPerTick = ProvFootballConfig.DECAY_PER_TICK.get();
        releaseGrace = ProvFootballConfig.RELEASE_GRACE.get();
        maxSlowdown = ProvFootballConfig.MAX_SLOWDOWN.get();
        zoneAmber = ProvFootballConfig.ZONE_AMBER.get().floatValue();
        zoneRed = ProvFootballConfig.ZONE_RED.get().floatValue();

        dribbleStandoffWalk = ProvFootballConfig.DRIBBLE_STANDOFF_WALK.get();
        dribbleStandoffSprint = ProvFootballConfig.DRIBBLE_STANDOFF_SPRINT.get();
        dribbleStandoffGainWalk = ProvFootballConfig.DRIBBLE_STANDOFF_GAIN_WALK.get();
        dribbleStandoffGainSprint = ProvFootballConfig.DRIBBLE_STANDOFF_GAIN_SPRINT.get();
        dribbleStandoffMargin = ProvFootballConfig.DRIBBLE_STANDOFF_MARGIN.get();

        dribbleAccel = ProvFootballConfig.DRIBBLE_ACCEL.get();
        dribbleDecel = ProvFootballConfig.DRIBBLE_DECEL.get();
        dribbleMinTouch = ProvFootballConfig.DRIBBLE_MIN_TOUCH.get();
        dribbleControlTouch = ProvFootballConfig.DRIBBLE_CONTROL_TOUCH.get();
        dribbleControlMargin = ProvFootballConfig.DRIBBLE_CONTROL_MARGIN.get();

        dribbleRetainLateralWalk = ProvFootballConfig.DRIBBLE_RETAIN_LATERAL_WALK.get();
        dribbleRetainLateralSprint = ProvFootballConfig.DRIBBLE_RETAIN_LATERAL_SPRINT.get();
        dribblePullWalk = ProvFootballConfig.DRIBBLE_PULL_WALK.get();
        dribblePullSprint = ProvFootballConfig.DRIBBLE_PULL_SPRINT.get();
        dribblePullMaxWalk = ProvFootballConfig.DRIBBLE_PULL_MAX_WALK.get();
        dribblePullMaxSprint = ProvFootballConfig.DRIBBLE_PULL_MAX_SPRINT.get();
        dribbleDeadzoneWalk = ProvFootballConfig.DRIBBLE_DEADZONE_WALK.get();
        dribbleDeadzoneSprint = ProvFootballConfig.DRIBBLE_DEADZONE_SPRINT.get();

        dribbleCloseRadius = ProvFootballConfig.DRIBBLE_CLOSE_RADIUS.get();
        dribbleCloseArcDot = ProvFootballConfig.DRIBBLE_CLOSE_ARC_DOT.get();
        dribbleCloseAuthority = ProvFootballConfig.DRIBBLE_CLOSE_AUTHORITY.get();
        dribbleCloseRetainScale = ProvFootballConfig.DRIBBLE_CLOSE_RETAIN_SCALE.get();
        dribbleClosePullScale = ProvFootballConfig.DRIBBLE_CLOSE_PULL_SCALE.get();

        dribbleBehindBrake = ProvFootballConfig.DRIBBLE_BEHIND_BRAKE.get();
        dribbleBehindRestSpeed = ProvFootballConfig.DRIBBLE_BEHIND_REST_SPEED.get();

        dribbleChopYaw = ProvFootballConfig.DRIBBLE_CHOP_YAW.get();
        dribbleChopDecay = ProvFootballConfig.DRIBBLE_CHOP_DECAY.get();
        dribbleChopTicks = ProvFootballConfig.DRIBBLE_CHOP_TICKS.get();
        dribbleChopMaxSpeed = ProvFootballConfig.DRIBBLE_CHOP_MAX_SPEED.get();
        dribbleChopCooldown = ProvFootballConfig.DRIBBLE_CHOP_COOLDOWN.get();
        dribbleChopMinSpeed = ProvFootballConfig.DRIBBLE_CHOP_MIN_SPEED.get();
        dribbleChopSprintScale = ProvFootballConfig.DRIBBLE_CHOP_SPRINT_SCALE.get();

        dribbleCutDot = ProvFootballConfig.DRIBBLE_CUT_DOT.get();
        dribbleCutMinSpeed = ProvFootballConfig.DRIBBLE_CUT_MIN_SPEED.get();
        dribbleCutInterrupt = ProvFootballConfig.DRIBBLE_CUT_INTERRUPT.get();

        dribbleReach = ProvFootballConfig.DRIBBLE_REACH.get();
        dribbleInstantBlend = ProvFootballConfig.DRIBBLE_INSTANT_BLEND.get();
        dribbleScatter = ProvFootballConfig.DRIBBLE_SCATTER.get();
        dribbleFrontArcDot = ProvFootballConfig.DRIBBLE_FRONT_ARC_DOT.get();
        dribbleSpeedCap = ProvFootballConfig.DRIBBLE_SPEED_CAP.get();
        dribbleMaxHeight = ProvFootballConfig.DRIBBLE_MAX_HEIGHT.get();
        dribbleStepOver = ProvFootballConfig.DRIBBLE_STEP_OVER.get();
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