package com.p1emc.provfootball.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Saves as Server config
 */
public class ProvFootballConfig {

    public static final ModConfigSpec SPEC;

    // --- physics ---
    public static final ModConfigSpec.DoubleValue GRAVITY;
    public static final ModConfigSpec.DoubleValue DRAG_COEFFICIENT;
    public static final ModConfigSpec.DoubleValue GROUND_FRICTION;
    public static final ModConfigSpec.DoubleValue MAX_SPEED;

    // --- bouncing ---
    public static final ModConfigSpec.DoubleValue BOUNCE_VERTICAL;
    public static final ModConfigSpec.DoubleValue BOUNCE_FALLOFF;
    public static final ModConfigSpec.DoubleValue BOUNCE_MIN;
    public static final ModConfigSpec.DoubleValue BOUNCE_HORIZONTAL;
    public static final ModConfigSpec.DoubleValue BOUNCE_CROSS_AXIS;
    public static final ModConfigSpec.DoubleValue WALL_CROSS_AXIS;

    // --- striking ---
    public static final ModConfigSpec.DoubleValue STRIKE_POWER;
    public static final ModConfigSpec.DoubleValue SPRINT_MOMENTUM;
    public static final ModConfigSpec.DoubleValue WALK_MOMENTUM;
    public static final ModConfigSpec.DoubleValue INCOMING_BLEND;
    public static final ModConfigSpec.DoubleValue VOLLEY_LIFT;
    public static final ModConfigSpec.DoubleValue FLICK_LIFT;
    public static final ModConfigSpec.DoubleValue FLICK_HORIZONTAL_KEEP;
    public static final ModConfigSpec.IntValue KICK_COOLDOWN;
    public static final ModConfigSpec.IntValue SPAWN_GRACE;

    // --- curve ---
    public static final ModConfigSpec.DoubleValue STRIKE_DEADZONE;
    public static final ModConfigSpec.DoubleValue SPIN_POWER;
    public static final ModConfigSpec.DoubleValue SPIN_DECAY;

    // --- shooting ---
    public static final ModConfigSpec.DoubleValue SHOT_POWER_MIN;
    public static final ModConfigSpec.DoubleValue SHOT_POWER_MAX;
    public static final ModConfigSpec.DoubleValue SHOT_LIFT;
    public static final ModConfigSpec.DoubleValue SHOT_MAX_ELEVATION;

    // --- charge ---
    public static final ModConfigSpec.IntValue MAX_CHARGE;
    public static final ModConfigSpec.IntValue MIN_CHARGE;
    public static final ModConfigSpec.IntValue DECAY_PER_TICK;
    public static final ModConfigSpec.IntValue RELEASE_GRACE;
    public static final ModConfigSpec.DoubleValue MAX_SLOWDOWN;
    public static final ModConfigSpec.DoubleValue ZONE_AMBER;
    public static final ModConfigSpec.DoubleValue ZONE_RED;

    // --- dribbling ---

    public static final ModConfigSpec.DoubleValue DRIBBLE_STANDOFF_WALK;
    public static final ModConfigSpec.DoubleValue DRIBBLE_STANDOFF_SPRINT;
    public static final ModConfigSpec.DoubleValue DRIBBLE_STANDOFF_GAIN_WALK;
    public static final ModConfigSpec.DoubleValue DRIBBLE_STANDOFF_GAIN_SPRINT;
    public static final ModConfigSpec.DoubleValue DRIBBLE_STANDOFF_MARGIN;

    public static final ModConfigSpec.DoubleValue DRIBBLE_ACCEL;
    public static final ModConfigSpec.DoubleValue DRIBBLE_DECEL;
    public static final ModConfigSpec.DoubleValue DRIBBLE_MIN_TOUCH;
    public static final ModConfigSpec.DoubleValue DRIBBLE_CONTROL_TOUCH;
    public static final ModConfigSpec.DoubleValue DRIBBLE_CONTROL_MARGIN;

    public static final ModConfigSpec.DoubleValue DRIBBLE_RETAIN_LATERAL_WALK;
    public static final ModConfigSpec.DoubleValue DRIBBLE_RETAIN_LATERAL_SPRINT;
    public static final ModConfigSpec.DoubleValue DRIBBLE_PULL_WALK;
    public static final ModConfigSpec.DoubleValue DRIBBLE_PULL_SPRINT;
    public static final ModConfigSpec.DoubleValue DRIBBLE_PULL_MAX_WALK;
    public static final ModConfigSpec.DoubleValue DRIBBLE_PULL_MAX_SPRINT;
    public static final ModConfigSpec.DoubleValue DRIBBLE_DEADZONE_WALK;
    public static final ModConfigSpec.DoubleValue DRIBBLE_DEADZONE_SPRINT;

    public static final ModConfigSpec.DoubleValue DRIBBLE_CLOSE_RADIUS;
    public static final ModConfigSpec.DoubleValue DRIBBLE_CLOSE_ARC_DOT;
    public static final ModConfigSpec.DoubleValue DRIBBLE_CLOSE_AUTHORITY;
    public static final ModConfigSpec.DoubleValue DRIBBLE_CLOSE_RETAIN_SCALE;
    public static final ModConfigSpec.DoubleValue DRIBBLE_CLOSE_PULL_SCALE;

    public static final ModConfigSpec.DoubleValue DRIBBLE_BEHIND_BRAKE;
    public static final ModConfigSpec.DoubleValue DRIBBLE_BEHIND_REST_SPEED;

    public static final ModConfigSpec.DoubleValue DRIBBLE_CHOP_YAW;
    public static final ModConfigSpec.DoubleValue DRIBBLE_CHOP_DECAY;
    public static final ModConfigSpec.DoubleValue DRIBBLE_CHOP_TICKS;
    public static final ModConfigSpec.DoubleValue DRIBBLE_CHOP_MAX_SPEED;
    public static final ModConfigSpec.IntValue DRIBBLE_CHOP_COOLDOWN;
    public static final ModConfigSpec.DoubleValue DRIBBLE_CHOP_MIN_SPEED;
    public static final ModConfigSpec.DoubleValue DRIBBLE_CHOP_SPRINT_SCALE;

    public static final ModConfigSpec.DoubleValue DRIBBLE_CUT_DOT;
    public static final ModConfigSpec.DoubleValue DRIBBLE_CUT_MIN_SPEED;
    public static final ModConfigSpec.IntValue DRIBBLE_CUT_INTERRUPT;

    public static final ModConfigSpec.DoubleValue DRIBBLE_REACH;
    public static final ModConfigSpec.DoubleValue DRIBBLE_INSTANT_BLEND;
    public static final ModConfigSpec.DoubleValue DRIBBLE_SCATTER;
    public static final ModConfigSpec.DoubleValue DRIBBLE_FRONT_ARC_DOT;
    public static final ModConfigSpec.DoubleValue DRIBBLE_SPEED_CAP;
    public static final ModConfigSpec.DoubleValue DRIBBLE_MAX_HEIGHT;
    public static final ModConfigSpec.DoubleValue DRIBBLE_STEP_OVER;
    public static final ModConfigSpec.IntValue DRIBBLE_COOLDOWN;

    // --- momentum ---
    public static final ModConfigSpec.IntValue SUSTAIN_MAX;
    public static final ModConfigSpec.DoubleValue SUSTAIN_MIN_SPEED;
    public static final ModConfigSpec.DoubleValue SUSTAIN_DECAY;
    public static final ModConfigSpec.DoubleValue CUT_THRESHOLD;
    public static final ModConfigSpec.DoubleValue CUT_PENALTY;

    // --- heading ---
    public static final ModConfigSpec.DoubleValue HEADER_RESTITUTION;
    public static final ModConfigSpec.DoubleValue HEADER_POWER;
    public static final ModConfigSpec.DoubleValue HEADER_BAND_ABOVE;
    public static final ModConfigSpec.DoubleValue HEADER_BAND_BELOW;
    public static final ModConfigSpec.DoubleValue HEADER_APPROACH_DOT;
    public static final ModConfigSpec.DoubleValue HEADER_REACH;
    public static final ModConfigSpec.IntValue HEADER_COOLDOWN;

    // --- shielding ---
    public static final ModConfigSpec.DoubleValue SHIELD_MIN_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue SHIELD_RADIUS;

    // --- item ---
    public static final ModConfigSpec.DoubleValue THROW_SPEED;
    public static final ModConfigSpec.DoubleValue THROW_MOMENTUM_SCALE;
    public static final ModConfigSpec.IntValue PICKUP_COOLDOWN;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();

        // ------------------------------------------------------------------
        b.comment("\n\nBall flight. Gravity and drag decide range;",
                        "friction decides how far it rolls.")
                .push("Physics");

        GRAVITY = b
                .comment("\nDownward acceleration per tick, in blocks.",
                        "Higher means flatter arcs and less hang time.",
                        "Vanilla items use 0.04.",
                        "Default: 0.045 (0.0 - 1.0)")
                .defineInRange("gravity", 0.045D, 0.0D, 1.0D);

        DRAG_COEFFICIENT = b
                .comment("\nAir resistance, scaled by current speed.",
                        "Real drag rises with the square of velocity, so a fast",
                        "shot loses proportionally more than a slow one",
                        "puts a natural ceiling on how far power gets you.",
                        "Higher means shots pull up sooner.",
                        "Default: 0.012 (0.0 - 0.5)")
                .defineInRange("dragCoefficient", 0.012D, 0.0D, 0.5D);

        GROUND_FRICTION = b
                .comment("\nFraction of horizontal speed kept per tick while rolling.",
                        "The single biggest factor in how the pitch feels.",
                        "Lower for a draggy surface, higher for ice.",
                        "Default: 0.93 (0.5 - 1.0)")
                .defineInRange("groundFriction", 0.93D, 0.5D, 1.0D);

        MAX_SPEED = b
                .comment("\nHard ceiling on total ball speed, blocks per tick.",
                        "Applied after every force, so it catches anything that",
                        "stacks past the shot maximum. All three axes scale",
                        "together, so a capped ball still travels where it was",
                        "aimed.",
                        "Default: 1.85 (0.1 - 5.0)")
                .defineInRange("maxSpeed", 1.85D, 0.1D, 5.0D);

        b.pop();

        // ------------------------------------------------------------------
        b.comment("\n\nHow the ball comes off surfaces.")
                .push("Bouncing");

        BOUNCE_VERTICAL = b
                .comment("\nRestitution for a gentle landing, fraction of impact",
                        "speed returned. A real ball deforms more on a hard impact",
                        "and loses proportionally more, so this is the ceiling",
                        "rather than a flat multiplier.",
                        "Default: 0.7 (0.0 - 1.0)")
                .defineInRange("vertical", 0.7D, 0.0D, 1.0D);

        BOUNCE_FALLOFF = b
                .comment("\nHow much harder impacts are punished. Subtracted from",
                        "the vertical restitution in proportion to impact speed.",
                        "Higher means fast balls die on landing while slow ones",
                        "stay lively.",
                        "Default: 0.3 (0.0 - 2.0)")
                .defineInRange("falloff", 0.3D, 0.0D, 2.0D);

        BOUNCE_MIN = b
                .comment("\nFloor on vertical restitution, so even a hard landing",
                        "bounces something.",
                        "Default: 0.35 (0.0 - 1.0)")
                .defineInRange("minimum", 0.35D, 0.0D, 1.0D);

        BOUNCE_HORIZONTAL = b
                .comment("\nFraction of speed kept off a wall, on the axis that hit.",
                        "Only matters on an enclosed pitch, on open grass this",
                        "almost never fires.",
                        "Default: 0.7 (0.0 - 1.0)")
                .defineInRange("horizontal", 0.7D, 0.0D, 1.0D);

        BOUNCE_CROSS_AXIS = b
                .comment("\nHorizontal speed kept after a floor bounce. Impacts cost",
                        "speed in every direction, not just the one you landed on.",
                        "Without this a hard shot skips along the ground at full",
                        "pace instead of pulling up.",
                        "Default: 0.85 (0.0 - 1.0)")
                .defineInRange("floorCrossAxis", 0.85D, 0.0D, 1.0D);

        WALL_CROSS_AXIS = b
                .comment("\nSpeed kept on the axes that did NOT hit a wall.",
                        "Without it, a glancing wall hit costs almost nothing and",
                        "the ball skims along at full pace.",
                        "Default: 0.9 (0.0 - 1.0)")
                .defineInRange("wallCrossAxis", 0.9D, 0.0D, 1.0D);

        b.pop();

        // ------------------------------------------------------------------
        b.comment("\n\nLeft-click on the ball. Passes, charged shots and flicks.")
                .push("Striking");

        STRIKE_POWER = b
                .comment("\nBase force of a pass. Deliberately weak, a standing",
                        "pass should be a nudge, and momentum is what makes a",
                        "running one travel.",
                        "Default: 0.3 (0.0 - 2.0)")
                .defineInRange("power", 0.3D, 0.0D, 2.0D);

        SPRINT_MOMENTUM = b
                .comment("\nWeight applied to the player's movement when sprinting.",
                        "Multiplied by their actual speed, so this is a scale",
                        "factor rather than an amount.",
                        "Default: 0.6 (0.0 - 5.0)")
                .defineInRange("sprintMomentum", 0.6D, 0.0D, 5.0D);

        WALK_MOMENTUM = b
                .comment("\nSame, for walking.",
                        "Default: 1.0 (0.0 - 5.0)")
                .defineInRange("walkMomentum", 1.0D, 0.0D, 5.0D);

        INCOMING_BLEND = b
                .comment("\nFraction of the ball's existing horizontal speed that",
                        "survives a strike. Aim dominates, but a fast incoming",
                        "ball carries extra energy, so a volley off a hard pass beats",
                        "one struck from a standstill. Vertical is always",
                        "discarded, or a dropping ball would drive every volley",
                        "into the floor.",
                        "Default: 0.25 (0.0 - 1.0)")
                .defineInRange("incomingBlend", 0.25D, 0.0D, 1.0D);

        VOLLEY_LIFT = b
                .comment("\nMultiplier on the vertical component of your aim.",
                        "Strike power is tuned for how far a flat pass rolls, and",
                        "at that magnitude the vertical part is too small to see.",
                        "Scaling it separately gives controllable volleys without",
                        "changing ground passes. Symmetric, so aiming down drives",
                        "the ball into the floor just as hard.",
                        "Default: 2.5 (0.0 - 10.0)")
                .defineInRange("volleyLift", 2.5D, 0.0D, 10.0D);

        FLICK_LIFT = b
                .comment("\nUpward speed of a crouch flick.",
                        "Emphasis on speed, this is not the number of blocks",
                        "Default: 0.55 (0.0 - 2.0)")
                .defineInRange("flickLift", 0.55D, 0.0D, 2.0D);

        FLICK_HORIZONTAL_KEEP = b
                .comment("\nHorizontal speed preserved through a flick. At 1.0 a",
                        "still ball pops straight up for a volley and a moving one",
                        "becomes a chip.",
                        "One action, two uses, decided by the",
                        "ball rather than an input mode. Lower it to bleed speed",
                        "on repeated flicks if juggling proves too strong.",
                        "Default: 1.0 (0.0 - 1.0)")
                .defineInRange("flickHorizontalKeep", 1.0D, 0.0D, 1.0D);

        KICK_COOLDOWN = b
                .comment("\nTicks between strikes. Stops a fast clicker double-firing",
                        "within one swing. 20 Ticks = 1 Second",
                        "Default: 3 (0 - 20)")
                .defineInRange("cooldown", 3, 0, 40);

        SPAWN_GRACE = b
                .comment("\nTicks after spawning during which the ball ignores",
                        "contact. A thrown ball starts near eye height moving with",
                        "you, which otherwise reads as a header on the tick it",
                        "appears. 20 Ticks = 1 Second",
                        "Default: 5 (0 - 40)")
                .defineInRange("spawnGrace", 5, 0, 40);

        b.pop();

        // ------------------------------------------------------------------
        b.comment("\n\nSideways spin, from where on the ball you strike it.",
                        "Hit the right side and it hooks left, your foot pushes",
                        "that side away and the spin brings it back.")
                .push("Curve");

        STRIKE_DEADZONE = b
                .comment("\nFraction of the ball's radius that counts as centre.",
                        "Strikes inside this go straight. Raise it if every pass",
                        "picks up drift from aim jitter.",
                        "Default: 0.05 (0.0 - 0.9)")
                .defineInRange("deadzone", 0.05D, 0.0D, 0.9D);

        SPIN_POWER = b
                .comment("\nSpin imparted by a full edge strike. The Magnus force is",
                        "spin times speed, so a fast ball already curves more for",
                        "the same contact. Aim for roughly 12-15% lateral",
                        "deviation over the flight, which is what a real free kick manages.",
                        "Default: 0.04 (0.0 - 1.0)")
                .defineInRange("spinPower", 0.04D, 0.0D, 1.0D);

        SPIN_DECAY = b
                .comment("\nSpin retained per tick. Real spin barely decays in",
                        "flight, so this sits close to 1.0 and the curve continues",
                        "the whole way rather than dying early. Lower it if balls",
                        "start orbiting.",
                        "Default: 0.99 (0.5 - 1.0)")
                .defineInRange("spinDecay", 0.99D, 0.5D, 1.0D);

        b.pop();

        // ------------------------------------------------------------------
        b.comment("\n\nCharged shots. Power follows a blended smoothstep/cubic curve,",
                        "so the bottom of the bar is not dead and the top still",
                        "climbs.")
                .push("Shooting");

        SHOT_POWER_MIN = b
                .comment("\nPower at minimum charge",
                        "Default: 0.65 (0.0 - 3.0)")
                .defineInRange("powerMin", 0.65D, 0.0D, 3.0D);

        SHOT_POWER_MAX = b
                .comment("\nPower at full charge.",
                        "I'd recommend for roughly half-pitch range.",
                        "Default: 1.6 (0.0 - 3.0)")
                .defineInRange("powerMax", 1.6D, 0.0D, 3.0D);

        SHOT_LIFT = b
                .comment("\nHow much of a shot's power goes upward at full elevation.",
                        "Elevation is captured from where you were looking when you",
                        "STARTED charging, because at the moment of the strike your",
                        "crosshair is forced down onto the ball. Under 1.0 so a",
                        "lofted shot still carries forward.",
                        "Default: 0.8 (0.0 - 3.0)")
                .defineInRange("lift", 0.8D, 0.0D, 3.0D);


        SHOT_MAX_ELEVATION = b
                .comment("\nDegrees above the horizon that give maximum lift. 45 is the",
                        "physical maximum-range launch angle, so aiming steeper gains",
                        "nothing. Raise it to spread the same lift across a wider aim",
                        "range and make specific elevations easier to hit.",
                        "Default: 45.0 (5.0 - 90.0)")
                .defineInRange("maxElevation", 45.0D, 5.0D, 90.0D);

        b.pop();

        // ------------------------------------------------------------------
        b.comment("\n\nCharge settings")
                .push("Charge");

        MAX_CHARGE = b
                .comment("\nTicks to reach full charge. 20 ticks = 1 Second.",
                        "Default: 30 (1 - 200)")
                .defineInRange("maxCharge", 30, 1, 200);

        MIN_CHARGE = b
                .comment("\nBelow this a release does nothing and the strike is a",
                        "normal pass. Keeps shooting cleanly separated from",
                        "passing rather than letting a tap become a weak shot.",
                        "Default: 8 (0 - 200)")
                .defineInRange("minCharge", 8, 0, 200);

        DECAY_PER_TICK = b
                .comment("\nCharge lost per tick after release. Gradual rather than",
                        "instant, so you have a moment to connect. 20 Ticks = 1 Second.",
                        "Default: 2 (1 - 50)")
                .defineInRange("decayPerTick", 2, 1, 50);

        RELEASE_GRACE = b
                .comment("\nTicks at full strength after release before decay starts.",
                        "20 Ticks = 1 Second.",
                        "Default: 10 ( 0 - 40)")
                .defineInRange("releaseGrace", 10, 0, 40);

        MAX_SLOWDOWN = b
                .comment("\nMovement speed lost at full charge, as a fraction.",
                        "0.6 means you move at 40% speed. Scales with the SQUARE",
                        "of charge, so a short tap is nearly free and a full charge",
                        "roots you, making power shots real commitments",
                        "Default: 0.6 (0.0 - 0.95)")
                .defineInRange("maxSlowdown", 0.6D, 0.0D, 0.95D);

        ZONE_AMBER = b
                .comment("\nWhere the charge bar and particles turn amber, as a",
                        "fraction of the USABLE range (between min and max).",
                        "Default: 0.34 (0.0 - 1.0)")
                .defineInRange("zoneAmber", 0.34D, 0.0D, 1.0D);

        ZONE_RED = b
                .comment("\nWhere they turn red. Keep it higher than zoneAmber.",
                        "Default: 0.67 (0.0 - 1.0)")
                .defineInRange("zoneRed", 0.67D, 0.0D, 1.0D);

        b.pop();

        // ------------------------------------------------------------------
        b.comment("\n\nCarrying the ball by walking into it.",
                        "A touch does not apply a fixed force. It steers the ball",
                        "toward a target: travelling at the player's own pace.")
                .push("Dribbling");

        b.comment("\nWhere the ball is held relative to the player, in blocks",
                        "along the run line.",
                        "These are the values that decide how the carry feels.")
                .push("Standoff");

        DRIBBLE_STANDOFF_WALK = b
                .comment("\nCarry distance when walking. Short, so the ball is",
                        "shieldable and turnable.",
                        "Default: 0.5 (0.0 - 4.0)")
                .defineInRange("walk", 0.5D, 0.0D, 4.0D);

        DRIBBLE_STANDOFF_SPRINT = b
                .comment("\nCarry distance when sprinting. Further out in front,",
                        "so less protected from a challenge. Must stay under the",
                        "carry range times the margin below, or it is clamped.",
                        "Default: 0.7 (0.0 - 4.0)")
                .defineInRange("sprint", 0.7D, 0.0D, 4.0D);

        DRIBBLE_STANDOFF_GAIN_WALK = b
                .comment("\nHow strongly a wrong carry distance is corrected when",
                        "walking, as ball speed added per block of error. High holds",
                        "the ball firmly at its distance; too high and it snaps",
                        "there and feels tethered rather than controlled.",
                        "Default: 0.4 (0.0 - 2.0)")
                .defineInRange("gainWalk", 0.4D, 0.0D, 2.0D);

        DRIBBLE_STANDOFF_GAIN_SPRINT = b
                .comment("\nSame when sprinting.",
                        "This is most of what makes sprinting loose",
                        "Default: 0.15 (0.0 - 2.0)")
                .defineInRange("gainSprint", 0.15D, 0.0D, 2.0D);

        DRIBBLE_STANDOFF_MARGIN = b
                .comment("\nHard limit on the standoff, as a fraction of the carry",
                        "range. A safety clamp: a standoff near the range holds the",
                        "ball at the very edge of what can be touched.",
                        "Default: 0.85 (0.1 - 1.0)")
                .defineInRange("margin", 0.85D, 0.1D, 1.0D);

        b.pop();

        b.comment("\nA touch's authority over the ball's speed along the run line,",
                        "in blocks per tick per touch.")
                .push("Authority");

        DRIBBLE_ACCEL = b
                .comment("\nMost a touch may speed the ball up by. Low feels like",
                        "the ball takes a moment to get going under you.",
                        "Default: 0.09 (0.0 - 1.0)")
                .defineInRange("accel", 0.09D, 0.0D, 1.0D);

        DRIBBLE_DECEL = b
                .comment("\nMost a touch may slow the ball down by. Higher than",
                        "accel, because checking a ball is easier than accelerating",
                        "it, and this is what stops the ball escaping when you slow.",
                        "Default: 0.14 (0.0 - 1.0)")
                .defineInRange("decel", 0.14D, 0.0D, 1.0D);

        DRIBBLE_MIN_TOUCH = b
                .comment("\nWeakest touch, as a fraction of the authority above, for",
                        "a player who has only just started moving.",
                        "Default: 0.35 (0.0 - 1.0)")
                .defineInRange("minTouch", 0.35D, 0.0D, 1.0D);

        DRIBBLE_CONTROL_TOUCH = b
                .comment("\nTouch authority, as a fraction, on a ball identified as",
                        "an incoming pass by the margin below. Low enough not to",
                        "carry a fast ball, high enough for touches.",
                        "Default: 0.3 (0.0 - 1.0)")
                .defineInRange("controlTouch", 0.3D, 0.0D, 1.0D);

        DRIBBLE_CONTROL_MARGIN = b
                .comment("\nHow far above the ball's target speed, in blocks per",
                        "tick, counts as an incoming pass rather than a carry.",
                        "Default: 0.12 (0.0 - 1.0)")
                .defineInRange("controlMargin", 0.12D, 0.0D, 1.0D);

        b.pop();

        b.comment("\nSideways movement across the run line. Bleeding this off is",
                        "what makes a cut bite instead of the ball skidding on past",
                        "you, so it is the main lever on how sharply you can turn.")
                .push("Lateral");

        DRIBBLE_RETAIN_LATERAL_WALK = b
                .comment("\nFraction of sideways motion kept when walking. Low, so",
                        "walking cuts are sharp.",
                        "!! Change this first if you dont like the feel of dribbling !!",
                        "Default: 0.12 (0.0 - 1.0)")
                .defineInRange("retainWalk", 0.12D, 0.0D, 1.0D);

        DRIBBLE_RETAIN_LATERAL_SPRINT = b
                .comment("\nSame when sprinting. High, so the ball goes wide.",
                        "Default: 0.6 (0.0 - 1.0)")
                .defineInRange("retainSprint", 0.6D, 0.0D, 1.0D);

        DRIBBLE_PULL_WALK = b
                .comment("\nStrength of the nudge back onto the run line, per block",
                        "of sideways offset.",
                        "Default: 0.5 (0.0 - 2.0)")
                .defineInRange("pullWalk", 0.5D, 0.0D, 2.0D);

        DRIBBLE_PULL_SPRINT = b
                .comment("\nSame for sprinting.",
                        "Default: 0.12 (0.0 - 2.0)")
                .defineInRange("pullSprint", 0.12D, 0.0D, 2.0D);

        DRIBBLE_PULL_MAX_WALK = b
                .comment("\nHard cap on that nudge, blocks per tick, when walking.",
                        "Matters more than the strength above, set it too high and the ball",
                        "snaps onto your line and looks magnetic.",
                        "Default: 0.13 (0.0 - 1.0)")
                .defineInRange("pullMaxWalk", 0.13D, 0.0D, 1.0D);

        DRIBBLE_PULL_MAX_SPRINT = b
                .comment("\nSame as above.",
                        "Default: 0.04 (0.0 - 1.0)")
                .defineInRange("pullMaxSprint", 0.04D, 0.0D, 1.0D);

        DRIBBLE_DEADZONE_WALK = b
                .comment("\nSideways offset left uncorrected when walking.",
                        "Without it the ball jitters holding a perfect line.",
                        "Default: 0.07 (0.0 - 2.0)")
                .defineInRange("deadzoneWalk", 0.07D, 0.0D, 2.0D);

        DRIBBLE_DEADZONE_SPRINT = b
                .comment("\nSame for sprinting.",
                        "Default: 0.35 (0.0 - 2.0)")
                .defineInRange("deadzoneSprint", 0.35D, 0.0D, 2.0D);

        b.pop();

        b.comment("\nClose control. At short range a touch's strength comes from",
                        "how near the ball is rather than from sustained momentum,",
                        "because a ball at your feet is always controllable.",
                        "This is what lets you chop the ball side to side past a defender.")
                .push("CloseControl");

        DRIBBLE_CLOSE_RADIUS = b
                .comment("\nRange over which close control applies, in blocks,",
                        "Zero (0.0) disables it.",
                        "Should sit between the walking standoff and the carry range",
                        "Default: 0.7 (0.0 - 4.0)")
                .defineInRange("radius", 0.7D, 0.0D, 4.0D);

        DRIBBLE_CLOSE_ARC_DOT = b
                .comment("\nFront arc at zero distance.",
                        "-1.0 lets you touch a ball anywhere around you when it is underfoot.",
                        "Default: -0.9 (-1.0 - 1.0)")
                .defineInRange("arcDot", -0.9D, -1.0D, 1.0D);

        DRIBBLE_CLOSE_AUTHORITY = b
                .comment("\nMinimum touch authority at zero distance, as a fraction of full.",
                        "At 1.0 a ball at your feet always gets a",
                        "full touch; lower introduces some fumbling.",
                        "Default: 0.9 (0.0 - 1.0)")
                .defineInRange("authority", 0.9D, 0.0D, 1.0D);

        DRIBBLE_CLOSE_RETAIN_SCALE = b
                .comment("\nMultiplier on lateral retain at zero distance.",
                        "Keep below 1.0 so sideways movement is reduce even more at the feet",
                        "This is the sharpness of the chop itself.",
                        "Default: 0.4 (0.0 - 2.0)")
                .defineInRange("retainScale", 0.4D, 0.0D, 2.0D);

        DRIBBLE_CLOSE_PULL_SCALE = b
                .comment("\nMultiplier on the centring nudge.",
                        "Raise if chops feel sluggish, lower if the ball feels tethered.",
                        "Default: 2.5 (0.0 - 8.0)")
                .defineInRange("pullScale", 2.5D, 0.0D, 8.0D);

        b.pop();

        b.comment("\nHandles balls that get behind the player")
                .push("Recovery");


        DRIBBLE_BEHIND_BRAKE = b
                .comment("\nFraction of speed a ball behind the player keeps per touch. ",
                        "Low stops it almost dead underfoot",
                        "At 1.0 nothing is recovered and a lost ball stays lost.",
                        "Default: 0.6 (0.0 - 1.0)")
                .defineInRange("brake", 0.6D, 0.0D, 1.0D);

        DRIBBLE_BEHIND_REST_SPEED = b
                .comment("\nSpeed below which a ball behind the player is left",
                        "alone, in blocks per tick. Stops a dead ball being",
                        "re-braked every other tick for no reason.",
                        "Default: 0.02 (0.0 - 1.0)")
                .defineInRange("restSpeed", 0.02D, 0.0D, 1.0D);

        b.pop();

        b.comment("\nChops. A flick of the mouse while the ball is at your feet",
                        "takes it across your body.",
                        "See it as a body feint.")
                .push("Chop");

        DRIBBLE_CHOP_YAW = b
                .comment("\nHow far the view must swing, in degrees, to trigger a",
                        "chop. Accumulated with decay, so a fast flick counts and a",
                        "steady turn of the same size does not. Lower is easier to",
                        "trigger and starts firing on ordinary looking-around.",
                        "Default: 55.0 (10.0 - 180.0)")
                .defineInRange("yaw", 55.0D, 10.0D, 180.0D);

        DRIBBLE_CHOP_DECAY = b
                .comment("\nFraction of the accumulated swing kept each tick. This",
                        "sets how SHARP a flick has to be.",
                        "At 0.6 you have around four ticks to complete it.",
                        "Default: 0.6 (0.0 - 0.99)")
                .defineInRange("decay", 0.6D, 0.0D, 0.99D);

        DRIBBLE_CHOP_TICKS = b
                .comment("\nTicks the ball is given to cross to its new position.",
                        "Low is a snappy, high is a slow drag.",
                        "Default: 3.0 (1.0 - 10.0)")
                .defineInRange("ticks", 3.0D, 1.0D, 10.0D);

        DRIBBLE_CHOP_MAX_SPEED = b
                .comment("\nCeiling on the resulting ball speed, blocks per tick.",
                        "The relocation is a straight line to the new carry spot, so",
                        "a near-180 flick with the ball out in front asks for a very",
                        "large velocity. This is what stops that reading as the ball",
                        "being fired sideways.",
                        "Default: 0.5 (0.0 - 2.0)")
                .defineInRange("maxSpeed", 0.5D, 0.0D, 2.0D);

        DRIBBLE_CHOP_COOLDOWN = b
                .comment("\nTicks before another chop is allowed.",
                        "Stops infinitely chaining chops.",
                        "Default: 8 (1 - 60)")
                .defineInRange("cooldown", 8, 1, 60);

        DRIBBLE_CHOP_MIN_SPEED = b
                .comment("\nHow fast the player must be moving to chop, in blocks per tick. ",
                        "Keep this under walking pace (0.215).",
                        "Default: 0.05 (0.0 - 1.0)")
                .defineInRange("minSpeed", 0.05D, 0.0D, 1.0D);

        DRIBBLE_CHOP_SPRINT_SCALE = b
                .comment("\nMultiplier on the chop when sprinting. Above 1.0, so a",
                        "sprinting chop sends the ball further than intended and has to be chased.",
                        "At 1.0 sprint chops = walking chops",
                        "Default: 1.35 (0.5 - 3.0)")
                .defineInRange("sprintScale", 1.35D, 0.5D, 3.0D);

        b.pop();

        b.comment("\nTouching on sharp cuts")
                .push("Cutting");

        DRIBBLE_CUT_DOT = b
                .comment("\nHow far off your run line the ball must be travelling to",
                        "count as a cut. 1.0 is straight ahead,",
                        "-1.0 is straight back at you.",
                        "Default: 0.4 (-1.0 - 1.0)")
                .defineInRange("cutDot", 0.4D, -1.0D, 1.0D);

        DRIBBLE_CUT_MIN_SPEED = b
                .comment("\nBall speed below which nothing counts as a cut. A nearly",
                        "stationary ball has a meaningless direction.",
                        "Default: 0.08 (0.0 - 1.0)")
                .defineInRange("cutMinSpeed", 0.08D, 0.0D, 1.0D);

        DRIBBLE_CUT_INTERRUPT = b
                .comment("\nTicks of remaining cooldown a cut may skip. Must be",
                        "below the cooldown to do anything.",
                        "Raise if turns feel late",
                        "Default: 1 (0 - 20)")
                .defineInRange("cutInterrupt", 1, 0, 20);

        b.pop();

        DRIBBLE_REACH = b
                .comment("\nHow far from the player the ball can be, in blocks, and",
                        "still be dribbled. Measured centre to centre.",
                        "Default: 0.85 (0.0 - 4.0)")
                .defineInRange("reach", 0.85D, 0.0D, 4.0D);

        DRIBBLE_INSTANT_BLEND = b
                .comment("\nHow much of the run line comes from the player's velocity.",
                        "High is responsive and slightly jittery;",
                        "low is smooth and laggy on turns.",
                        "Default: 0.7 (0.0 - 1.0)")
                .defineInRange("instantBlend", 0.7D, 0.0D, 1.0D);

        DRIBBLE_SCATTER = b
                .comment("\nRandom sideways kick on a sprinting touch,",
                        "as a fraction of the player's speed. ",
                        "Zero at a walk regardless of this value.",
                        "touches.",
                        "Default: 0.09 (0.0 - 1.0)")
                .defineInRange("scatter", 0.09D, 0.0D, 1.0D);

        DRIBBLE_FRONT_ARC_DOT = b
                .comment("\nHow far round the player the ball may sit and still be",
                        "touched,",
                        "1.0 is straight ahead only, ",
                        "0.0 allows the whole forward half,",
                        "-1.0 allows anything.",
                        "A ball outside the arc is braked rather than kicked, see (Recovery).",
                        "Default: -0.15 (-1.0 - 1.0)")
                .defineInRange("frontArcDot", -0.15D, -1.0D, 1.0D);

        DRIBBLE_SPEED_CAP = b
                .comment("\nMax speed a touch can leave the ball at.",
                        "Never slows a ball that was already faster,",
                        "e.g. running onto a through ball is unaffected.",
                        "Default: 0.45 (0.0 - 2.0)")
                .defineInRange("speedCap", 0.45D, 0.0D, 2.0D);

        DRIBBLE_MAX_HEIGHT = b
                .comment("\nHow high above the players feet the ball can be and",
                        "still be dribbled.",
                        "Default: 0.4 (0.0 - 3.0)")
                .defineInRange("maxHeight", 0.4D, 0.0D, 3.0D);

        DRIBBLE_STEP_OVER = b
                .comment("\nHow far the player's feet can be ABOVE the ball, in",
                        "blocks, and still dribble it. Past this you are jumping",
                        "over the ball rather than running with it.",
                        "Default: 0.55 (0.0 - 3.0)")
                .defineInRange("stepOver", 0.55D, 0.0D, 3.0D);

        DRIBBLE_COOLDOWN = b
                .comment("\nTicks between touches.",
                        "Lower is more responsive but may seem unrealistic",
                        "Default: 2 (1 - 20)")
                .defineInRange("cooldown", 2, 1, 20);

        b.pop();

        // ------------------------------------------------------------------
        b.comment("\n\nSustained motion. Instantaneous speed cannot tell a shuffle",
                        "from the first tick of a sprint.",
                        "Allows players to lightly readjust the ball")
                .push("Momentum");

        SUSTAIN_MAX = b
                .comment("\nTicks of consistent motion to reach full commitment.",
                        "Default: 8 (1 - 200)")
                .defineInRange("sustainMax", 8, 1, 200);

        SUSTAIN_MIN_SPEED = b
                .comment("\nBelow this speed you are not considered to be moving.",
                        "Default: 0.04 (0.0 - 1.0)")
                .defineInRange("minSpeed", 0.04D, 0.0D, 1.0D);

        SUSTAIN_DECAY = b
                .comment("\nBuild-up lost per tick while stopped. Decay rather than",
                        "reset, so a feint does not cost you everything.",
                        "Default: 1.5 (0.0 - 20.0)")
                .defineInRange("decay", 1.5D, 0.0D, 20.0D);

        CUT_THRESHOLD = b
                .comment("\nDot product between this tick's direction and last",
                        "tick's. Below this the turn counts as a cut. 0.7 is about",
                        "45 degrees. This is what gives you close control.",
                        "Default: 0.7 (-1.0 - 1.0)")
                .defineInRange("cutThreshold", 0.7D, -1.0D, 1.0D);

        CUT_PENALTY = b
                .comment("\nBuild-up retained through a cut. Not a full reset, you",
                        "keep some momentum through a turn, just not much.",
                        "Default: 0.35 (0.0 - 1.0)")
                .defineInRange("cutPenalty", 0.35D, 0.0D, 1.0D);

        b.pop();

        // ------------------------------------------------------------------
        b.comment("\n\nHeaders happen on CONTACT, not on a click: you get your head",
                        "in the way rather than aiming and triggering. A header",
                        "reflects the incoming velocity about the plane your head",
                        "presents, so it redirects rather than replaces",
                        "Crossing quality matters, and a glancing header can send the ball",
                        "behind you.")
                .push("Heading");

        HEADER_RESTITUTION = b
                .comment("\nFraction of the ball's pace that survives a header.",
                        "Since a header adds almost nothing of its own, nearly all",
                        "the danger comes from the pace the crosser provided. Set",
                        "this low and crosses become pointless; high and any cross",
                        "is dangerous regardless of quality.",
                        "Default: 0.6 (0.0 - 1.5)")
                .defineInRange("restitution", 0.6D, 0.0D, 1.5D);

        HEADER_POWER = b
                .comment("\nMost force a header adds on its own, scaled by how fast",
                        "the player was moving.",
                        "Default: 0.25 (0.0 - 1.0)")
                .defineInRange("power", 0.25D, 0.0D, 1.0D);

        HEADER_BAND_ABOVE = b
                .comment("\nHow far above eye level still counts as a header.",
                        "Default: 0.6 (0.0 - 3.0)")
                .defineInRange("bandAbove", 0.6D, 0.0D, 3.0D);

        HEADER_BAND_BELOW = b
                .comment("\nHow far below eye level.",
                        "Note: Eye level is 1.62, ball is 0.5 in height",
                        "Default: 0.4 (0.0 - 1.5)")
                .defineInRange("bandBelow", 0.4D, 0.0D, 1.5D);

        HEADER_APPROACH_DOT = b
                .comment("\nHow directly you must be moving INTO the ball. 0.3 is",
                        "about 72 degrees. Without this a ball drifting past your",
                        "face while you happen to be jumping heads itself.",
                        "Default: 0.3 (-1.0 - 1.0)")
                .defineInRange("approachDot", 0.3D, -1.0D, 1.0D);

        HEADER_REACH = b
                .comment("\nHow far past the ball's hitbox a header connects.",
                        "Default: 0.4 (0.0 - 2.0)")
                .defineInRange("reach", 0.4D, 0.0D, 2.0D);

        HEADER_COOLDOWN = b
                .comment("\nTicks between headers. Long enough that one jump is one header",
                        "A short cooldown means the ball rattles off your head",
                        "repeatedly. 20 Ticks = 1 Second",
                        "Default: 15 (1 - 100)")
                .defineInRange("cooldown", 15, 1, 100);

        b.pop();

        // ------------------------------------------------------------------
        b.comment("\n\nProtecting the ball. Walking shields, sprinting does not.",
                        "A challenger's touch scales with how far in FRONT",
                        "of the shielder they are:",
                        "full strength face-on, nothing from behind, since the shielder's body is",
                        "in the way.")
                .push("Shielding");

        SHIELD_MIN_MULTIPLIER = b
                .comment("\nWeakest touch a challenger gets when directly behind the",
                        "shielder. At 0 the shield covers a full 180 degrees and",
                        "you must get in front to touch the ball at all. Raise it",
                        "toward 0.2 if defenders feel helpless.",
                        "Default: 0.0 (0.0 - 1.0)")
                .defineInRange("minMultiplier", 0.0D, 0.0D, 1.0D);

        SHIELD_RADIUS = b
                .comment("\nHow close the owner must be to the ball to shield it, in",
                        "blocks. Beyond this the ball is loose regardless of who",
                        "touched it last.",
                        "Default: 2.0 (0.0 - 10.0)")
                .defineInRange("radius", 2.0D, 0.0D, 10.0D);

        b.pop();

        // ------------------------------------------------------------------
        b.comment("\n\nThe football item: throw-ins, placement and pickup.")
                .push("Items");

        THROW_SPEED = b
                .comment("\nConstant throw power. The angle you aim at changes",
                        "direction only; gravity and drag decide how far it goes.",
                        "Default: 0.5 (0.0 - 3.0)")
                .defineInRange("throwSpeed", 0.5D, 0.0D, 3.0D);

        THROW_MOMENTUM_SCALE = b
                .comment("\nHow much of the thrower's movement is added. A run-up",
                        "genuinely adds distance; backing away takes it off.",
                        "Default: 0.5 (0.0 - 3.0)")
                .defineInRange("throwMomentumScale", 0.5D, 0.0D, 3.0D);

        PICKUP_COOLDOWN = b
                .comment("\nTicks before a picked-up ball can be placed or thrown again.",
                        "20 ticks = 1 second.",
                        "Default: 30 (0 - 600)")
                .defineInRange("pickupCooldown", 30, 0, 600);

        b.pop();

        SPEC = b.build();
    }
}