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
    public static final ModConfigSpec.DoubleValue DRIBBLE_PUSH_WALK;
    public static final ModConfigSpec.DoubleValue DRIBBLE_PUSH_SPRINT;
    public static final ModConfigSpec.DoubleValue DRIBBLE_RETAIN;
    public static final ModConfigSpec.DoubleValue DRIBBLE_MOVEMENT_BIAS;
    public static final ModConfigSpec.DoubleValue DRIBBLE_MIN_TOUCH;
    public static final ModConfigSpec.DoubleValue DRIBBLE_REACH;
    public static final ModConfigSpec.DoubleValue DRIBBLE_MAX_BALL_SPEED;
    public static final ModConfigSpec.DoubleValue DRIBBLE_MAX_HEIGHT;
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
        b.comment("Ball flight. Gravity and drag decide range;",
                        "friction decides how far it rolls.")
                .push("physics");

        GRAVITY = b
                .comment("Downward acceleration per tick, in blocks.",
                        "Higher means flatter arcs and less hang time.",
                        "Vanilla items use 0.04.",
                        "Default: 0.045 (0.0 - 1.0)")
                .defineInRange("gravity", 0.045D, 0.0D, 1.0D);

        DRAG_COEFFICIENT = b
                .comment("Air resistance, scaled by current speed.",
                        "Real drag rises with the square of velocity, so a fast",
                        "shot loses proportionally more than a slow one",
                        "puts a natural ceiling on how far power gets you.",
                        "Higher means shots pull up sooner.",
                        "Default: 0.012 (0.0 - 0.5)")
                .defineInRange("dragCoefficient", 0.012D, 0.0D, 0.5D);

        GROUND_FRICTION = b
                .comment("Fraction of horizontal speed kept per tick while rolling.",
                        "The single biggest factor in how the pitch feels.",
                        "Lower for a draggy surface, higher for ice.",
                        "Default: 0.93 (0.5 - 1.0)")
                .defineInRange("groundFriction", 0.93D, 0.5D, 1.0D);

        MAX_SPEED = b
                .comment("Hard ceiling on total ball speed, blocks per tick.",
                        "Applied after every force, so it catches anything that",
                        "stacks past the shot maximum. All three axes scale",
                        "together, so a capped ball still travels where it was",
                        "aimed.",
                        "Default: 1.6 (0.1 - 5.0)")
                .defineInRange("maxSpeed", 1.6D, 0.1D, 5.0D);

        b.pop();

        // ------------------------------------------------------------------
        b.comment("How the ball comes off surfaces.")
                .push("bouncing");

        BOUNCE_VERTICAL = b
                .comment("Restitution for a gentle landing, fraction of impact",
                        "speed returned. A real ball deforms more on a hard impact",
                        "and loses proportionally more, so this is the ceiling",
                        "rather than a flat multiplier.",
                        "Default: 0.7 (0.0 - 1.0)")
                .defineInRange("vertical", 0.7D, 0.0D, 1.0D);

        BOUNCE_FALLOFF = b
                .comment("How much harder impacts are punished. Subtracted from",
                        "the vertical restitution in proportion to impact speed.",
                        "Higher means fast balls die on landing while slow ones",
                        "stay lively.",
                        "Default: 0.3 (0.0 - 2.0)")
                .defineInRange("falloff", 0.3D, 0.0D, 2.0D);

        BOUNCE_MIN = b
                .comment("Floor on vertical restitution, so even a hard landing",
                        "bounces something.",
                        "Default: 0.35 (0.0 - 1.0)")
                .defineInRange("minimum", 0.35D, 0.0D, 1.0D);

        BOUNCE_HORIZONTAL = b
                .comment("Fraction of speed kept off a wall, on the axis that hit.",
                        "Only matters on an enclosed pitch, on open grass this",
                        "almost never fires.",
                        "Default: 0.7 (0.0 - 1.0)")
                .defineInRange("horizontal", 0.7D, 0.0D, 1.0D);

        BOUNCE_CROSS_AXIS = b
                .comment("Horizontal speed kept after a floor bounce. Impacts cost",
                        "speed in every direction, not just the one you landed on.",
                        "Without this a hard shot skips along the ground at full",
                        "pace instead of pulling up.",
                        "Default: 0.85 (0.0 - 1.0)")
                .defineInRange("floorCrossAxis", 0.85D, 0.0D, 1.0D);

        WALL_CROSS_AXIS = b
                .comment("Speed kept on the axes that did NOT hit a wall.",
                        "Without it, a glancing wall hit costs almost nothing and",
                        "the ball skims along at full pace.",
                        "Default: 0.9 (0.0 - 1.0)")
                .defineInRange("wallCrossAxis", 0.9D, 0.0D, 1.0D);

        b.pop();

        // ------------------------------------------------------------------
        b.comment("Left-click on the ball. Passes, charged shots and flicks.")
                .push("striking");

        STRIKE_POWER = b
                .comment("Base force of a pass. Deliberately weak, a standing",
                        "pass should be a nudge, and momentum is what makes a",
                        "running one travel.",
                        "Default: 0.3 (0.0 - 2.0)")
                .defineInRange("power", 0.3D, 0.0D, 2.0D);

        SPRINT_MOMENTUM = b
                .comment("Weight applied to the player's movement when sprinting.",
                        "Multiplied by their actual speed, so this is a scale",
                        "factor rather than an amount.",
                        "Default: 0.6 (0.0 - 5.0)")
                .defineInRange("sprintMomentum", 0.6D, 0.0D, 5.0D);

        WALK_MOMENTUM = b
                .comment("Same, for walking.",
                        "Default: 1.0 (0.0 - 5.0)")
                .defineInRange("walkMomentum", 1.0D, 0.0D, 5.0D);

        INCOMING_BLEND = b
                .comment("Fraction of the ball's existing horizontal speed that",
                        "survives a strike. Aim dominates, but a fast incoming",
                        "ball carries extra energy, so a volley off a hard pass beats",
                        "one struck from a standstill. Vertical is always",
                        "discarded, or a dropping ball would drive every volley",
                        "into the floor.",
                        "Default: 0.25 (0.0 - 1.0)")
                .defineInRange("incomingBlend", 0.25D, 0.0D, 1.0D);

        VOLLEY_LIFT = b
                .comment("Multiplier on the vertical component of your aim.",
                        "Strike power is tuned for how far a flat pass rolls, and",
                        "at that magnitude the vertical part is too small to see.",
                        "Scaling it separately gives controllable volleys without",
                        "changing ground passes. Symmetric, so aiming down drives",
                        "the ball into the floor just as hard.",
                        "Default: 2.5 (0.0 - 10.0)")
                .defineInRange("volleyLift", 2.5D, 0.0D, 10.0D);

        FLICK_LIFT = b
                .comment("Upward speed of a crouch flick.",
                        "Emphasis on speed, this is not the number of blocks",
                        "Default: 0.55 (0.0 - 2.0)")
                .defineInRange("flickLift", 0.55D, 0.0D, 2.0D);

        FLICK_HORIZONTAL_KEEP = b
                .comment("Horizontal speed preserved through a flick. At 1.0 a",
                        "still ball pops straight up for a volley and a moving one",
                        "becomes a chip.",
                        "One action, two uses, decided by the",
                        "ball rather than an input mode. Lower it to bleed speed",
                        "on repeated flicks if juggling proves too strong.",
                        "Default: 1.0 (0.0 - 1.0)")
                .defineInRange("flickHorizontalKeep", 1.0D, 0.0D, 1.0D);

        KICK_COOLDOWN = b
                .comment("Ticks between strikes. Stops a fast clicker double-firing",
                        "within one swing. 20 Ticks = 1 Second",
                        "Default: 3 (0 - 20)")
                .defineInRange("cooldown", 3, 0, 40);

        SPAWN_GRACE = b
                .comment("Ticks after spawning during which the ball ignores",
                        "contact. A thrown ball starts near eye height moving with",
                        "you, which otherwise reads as a header on the tick it",
                        "appears. 20 Ticks = 1 Second",
                        "Default: 5 (0 - 40)")
                .defineInRange("spawnGrace", 5, 0, 40);

        b.pop();

        // ------------------------------------------------------------------
        b.comment("Sideways spin, from where on the ball you strike it.",
                        "Hit the right side and it hooks left, your foot pushes",
                        "that side away and the spin brings it back.")
                .push("curve");

        STRIKE_DEADZONE = b
                .comment("Fraction of the ball's radius that counts as centre.",
                        "Strikes inside this go straight. Raise it if every pass",
                        "picks up drift from aim jitter.",
                        "Default: 0.05 (0.0 - 0.9)")
                .defineInRange("deadzone", 0.05D, 0.0D, 0.9D);

        SPIN_POWER = b
                .comment("Spin imparted by a full edge strike. The Magnus force is",
                        "spin times speed, so a fast ball already curves more for",
                        "the same contact. Aim for roughly 12-15% lateral",
                        "deviation over the flight, which is what a real free kick manages.",
                        "Default: 0.05 (0.0 - 1.0)")
                .defineInRange("spinPower", 0.05D, 0.0D, 1.0D);

        SPIN_DECAY = b
                .comment("Spin retained per tick. Real spin barely decays in",
                        "flight, so this sits close to 1.0 and the curve continues",
                        "the whole way rather than dying early. Lower it if balls",
                        "start orbiting.",
                        "Default: 0.99 (0.5 - 1.0)")
                .defineInRange("spinDecay", 0.99D, 0.5D, 1.0D);

        b.pop();

        // ------------------------------------------------------------------
        b.comment("Charged shots. Power follows a blended smoothstep/cubic curve,",
                        "so the bottom of the bar is not dead and the top still",
                        "climbs.")
                .push("shooting");

        SHOT_POWER_MIN = b
                .comment("Power at minimum charge",
                        "Default: 0.65 (0.0 - 3.0)")
                .defineInRange("powerMin", 0.65D, 0.0D, 3.0D);

        SHOT_POWER_MAX = b
                .comment("Power at full charge.",
                        "I'd recommend for roughly half-pitch range.",
                        "Default: 1.4 (0.0 - 3.0)")
                .defineInRange("powerMax", 1.4D, 0.0D, 3.0D);

        SHOT_LIFT = b
                .comment("How much of a shot's power goes upward at full elevation.",
                        "Elevation is captured from where you were looking when you",
                        "STARTED charging, because at the moment of the strike your",
                        "crosshair is forced down onto the ball. Under 1.0 so a",
                        "lofted shot still carries forward.",
                        "Default: 0.8 (0.0 - 3.0)")
                .defineInRange("lift", 0.8D, 0.0D, 3.0D);


        SHOT_MAX_ELEVATION = b
                .comment("Degrees above the horizon that give maximum lift. 45 is the",
                        "physical maximum-range launch angle, so aiming steeper gains",
                        "nothing. Raise it to spread the same lift across a wider aim",
                        "range and make specific elevations easier to hit.",
                        "Default: 45.0 (5.0 - 90.0)")
                .defineInRange("maxElevation", 45.0D, 5.0D, 90.0D);

        b.pop();

        // ------------------------------------------------------------------
        b.comment("Charge settings")
                .push("charge");

        MAX_CHARGE = b
                .comment("Ticks to reach full charge. 20 ticks = 1 Second.",
                        "Default: 30 (1 - 200)")
                .defineInRange("maxCharge", 30, 1, 200);

        MIN_CHARGE = b
                .comment("Below this a release does nothing and the strike is a",
                        "normal pass. Keeps shooting cleanly separated from",
                        "passing rather than letting a tap become a weak shot.",
                        "Default: 8 (0 - 200)")
                .defineInRange("minCharge", 8, 0, 200);

        DECAY_PER_TICK = b
                .comment("Charge lost per tick after release. Gradual rather than",
                        "instant, so you have a moment to connect. 20 Ticks = 1 Second.",
                        "Default: 2 (1 - 50)")
                .defineInRange("decayPerTick", 2, 1, 50);

        RELEASE_GRACE = b
                .comment("Ticks at full strength after release before decay starts.",
                        "20 Ticks = 1 Second.",
                        "Default: 5 ( 0 - 20)")
                .defineInRange("releaseGrace", 5, 0, 40);

        MAX_SLOWDOWN = b
                .comment("Movement speed lost at full charge, as a fraction.",
                        "0.6 means you move at 40% speed. Scales with the SQUARE",
                        "of charge, so a short tap is nearly free and a full charge",
                        "roots you, making power shots real commitments",
                        "Default: 0.6 (0.0 - 0.95)")
                .defineInRange("maxSlowdown", 0.6D, 0.0D, 0.95D);

        ZONE_AMBER = b
                .comment("Where the charge bar and particles turn amber, as a",
                        "fraction of the USABLE range (between min and max).",
                        "Default: 0.34 (0.0 - 1.0)")
                .defineInRange("zoneAmber", 0.34D, 0.0D, 1.0D);

        ZONE_RED = b
                .comment("Where they turn red. Keep it higher than zoneAmber.",
                        "Default: 0.67 (0.0 - 1.0)")
                .defineInRange("zoneRed", 0.67D, 0.0D, 1.0D);

        b.pop();

        // ------------------------------------------------------------------
        b.comment("Carrying the ball by walking into it.")
                .push("dribbling");

        DRIBBLE_PUSH_WALK = b
                .comment("Force of a walking touch.",
                        "Default: 0.14 (0.0 - 1.0)")
                .defineInRange("pushWalk", 0.14D, 0.0D, 1.0D);

        DRIBBLE_PUSH_SPRINT = b
                .comment("Force of a sprinting touch. Higher than walking, so",
                        "sprinting covers ground faster but leaves the ball looser",
                        "and easier to take. That trade is the point.",
                        "Default: 0.24 (0.0 - 1.0)")
                .defineInRange("pushSprint", 0.24D, 0.0D, 1.0D);

        DRIBBLE_RETAIN = b
                .comment("Fraction of the ball's existing horizontal speed kept on",
                        "a touch. A real touch redirects rather than only adding,",
                        "so killing some of the old direction is what makes sharp",
                        "turns possible.",
                        "Default: 0.6 (0.0 - 1.0)")
                .defineInRange("retain", 0.6D, 0.0D, 1.0D);

        DRIBBLE_MOVEMENT_BIAS = b
                .comment("How much of the push follows the direction you are",
                        "RUNNING versus the direction from you to the ball.",
                        "All movement lets you stand inside the ball with it never",
                        "escaping; all away-vector is unstable when you are nearly",
                        "centred on it. Lower turns more sharply.",
                        "Default: 0.2 (0.0 - 1.0)")
                .defineInRange("movementBias", 0.2D, 0.0D, 1.0D);

        DRIBBLE_MIN_TOUCH = b
                .comment("Weakest touch, as a fraction of full strength, for a",
                        "player who has only just started moving. Scaled up by",
                        "sustained motion, see the momentum section.",
                        "Default: 0.25 (0.0 - 1.0)")
                .defineInRange("minTouch", 0.25D, 0.0D, 1.0D);

        DRIBBLE_REACH = b
                .comment("How far past the ball's hitbox a player can touch it,",
                        "in blocks.",
                        "Default: 0.3 (0.0 - 2.0)")
                .defineInRange("reach", 0.3D, 0.0D, 2.0D);

        DRIBBLE_MAX_BALL_SPEED = b
                .comment("A ball already moving faster than this ignores dribble.",
                        "An incoming pass has to be controlled with a strike",
                        "Without this you could accelerate a pass by standing in its way.",
                        "Default: 0.25 (0.0 - 2.0)")
                .defineInRange("maxBallSpeed", 0.25D, 0.0D, 2.0D);

        DRIBBLE_MAX_HEIGHT = b
                .comment("How high above the player's feet the ball can be and",
                        "still be dribbled. A ball at chest height being jogged",
                        "along looks magnetic rather than kicked",
                        "Default: 0.4 (0.0 - 3.0)")
                .defineInRange("maxHeight", 0.4D, 0.0D, 3.0D);

        DRIBBLE_COOLDOWN = b
                .comment("Ticks between touches. Touches are discrete, like real",
                        "dribbling. Lower is more responsive but compounds faster.",
                        "Default: 2 (1 - 20)")
                .defineInRange("cooldown", 2, 1, 20);

        b.pop();

        // ------------------------------------------------------------------
        b.comment("Sustained motion. Instantaneous speed cannot tell a shuffle",
                        "from the first tick of a sprint.",
                        "Allows players to lightly readjust the ball")
                .push("momentum");

        SUSTAIN_MAX = b
                .comment("Ticks of consistent motion to reach full commitment.",
                        "Default: 8 (1 - 200)")
                .defineInRange("sustainMax", 8, 1, 200);

        SUSTAIN_MIN_SPEED = b
                .comment("Below this speed you are not considered to be moving.",
                        "Default: 0.04 (0.0 - 1.0)")
                .defineInRange("minSpeed", 0.04D, 0.0D, 1.0D);

        SUSTAIN_DECAY = b
                .comment("Build-up lost per tick while stopped. Decay rather than",
                        "reset, so a feint does not cost you everything.",
                        "Default: 1.5 (0.0 - 20.0)")
                .defineInRange("decay", 1.5D, 0.0D, 20.0D);

        CUT_THRESHOLD = b
                .comment("Dot product between this tick's direction and last",
                        "tick's. Below this the turn counts as a cut. 0.7 is about",
                        "45 degrees. This is what gives you close control.",
                        "Default: 0.7 (-1.0 - 1.0)")
                .defineInRange("cutThreshold", 0.7D, -1.0D, 1.0D);

        CUT_PENALTY = b
                .comment("Build-up retained through a cut. Not a full reset, you",
                        "keep some momentum through a turn, just not much.",
                        "Default: 0.35 (0.0 - 1.0)")
                .defineInRange("cutPenalty", 0.35D, 0.0D, 1.0D);

        b.pop();

        // ------------------------------------------------------------------
        b.comment("Headers happen on CONTACT, not on a click: you get your head",
                        "in the way rather than aiming and triggering. A header",
                        "reflects the incoming velocity about the plane your head",
                        "presents, so it redirects rather than replaces",
                        "Crossing quality matters, and a glancing header can send the ball",
                        "behind you.")
                .push("heading");

        HEADER_RESTITUTION = b
                .comment("Fraction of the ball's pace that survives a header.",
                        "Since a header adds almost nothing of its own, nearly all",
                        "the danger comes from the pace the crosser provided. Set",
                        "this low and crosses become pointless; high and any cross",
                        "is dangerous regardless of quality.",
                        "Default: 0.6 (0.0 - 1.5)")
                .defineInRange("restitution", 0.6D, 0.0D, 1.5D);

        HEADER_POWER = b
                .comment("Most force a header adds on its own, scaled by how fast",
                        "the player was moving.",
                        "Default: 0.18 (0.0 - 1.0)")
                .defineInRange("power", 0.18D, 0.0D, 1.0D);

        HEADER_BAND_ABOVE = b
                .comment("How far above eye level still counts as a header.",
                        "Default: 0.6 (0.0 - 3.0)")
                .defineInRange("bandAbove", 0.6D, 0.0D, 3.0D);

        HEADER_BAND_BELOW = b
                .comment("How far below eye level.",
                        "Note: Eye level is 1.62, ball is 0.5 in height",
                        "Default: 0.4 (0.0 - 1.5)")
                .defineInRange("bandBelow", 0.4D, 0.0D, 1.5D);

        HEADER_APPROACH_DOT = b
                .comment("How directly you must be moving INTO the ball. 0.3 is",
                        "about 72 degrees. Without this a ball drifting past your",
                        "face while you happen to be jumping heads itself.",
                        "Default: 0.3 (-1.0 - 1.0)")
                .defineInRange("approachDot", 0.3D, -1.0D, 1.0D);

        HEADER_REACH = b
                .comment("How far past the ball's hitbox a header connects.",
                        "Default: 0.4 (0.0 - 2.0)")
                .defineInRange("reach", 0.4D, 0.0D, 2.0D);

        HEADER_COOLDOWN = b
                .comment("Ticks between headers. Long enough that one jump is one header",
                        "A short cooldown means the ball rattles off your head",
                        "repeatedly. 20 Ticks = 1 Second",
                        "Default: 15 (1 - 100)")
                .defineInRange("cooldown", 15, 1, 100);

        b.pop();

        // ------------------------------------------------------------------
        b.comment("Protecting the ball. Walking shields, sprinting does not.",
                        "A challenger's touch scales with how far in FRONT",
                        "of the shielder they are:",
                        "full strength face-on, nothing from behind, since the shielder's body is",
                        "in the way.")
                .push("shielding");

        SHIELD_MIN_MULTIPLIER = b
                .comment("Weakest touch a challenger gets when directly behind the",
                        "shielder. At 0 the shield covers a full 180 degrees and",
                        "you must get in front to touch the ball at all. Raise it",
                        "toward 0.2 if defenders feel helpless.",
                        "Default: 0.0 (0.0 - 1.0)")
                .defineInRange("minMultiplier", 0.0D, 0.0D, 1.0D);

        SHIELD_RADIUS = b
                .comment("How close the owner must be to the ball to shield it, in",
                        "blocks. Beyond this the ball is loose regardless of who",
                        "touched it last.",
                        "Default: 2.0 (0.0 - 10.0)")
                .defineInRange("radius", 2.0D, 0.0D, 10.0D);

        b.pop();

        // ------------------------------------------------------------------
        b.comment("The football item: throw-ins, placement and pickup.")
                .push("item");

        THROW_SPEED = b
                .comment("Constant throw power. The angle you aim at changes",
                        "direction only; gravity and drag decide how far it goes.",
                        "Default: 0.5 (0.0 - 3.0)")
                .defineInRange("throwSpeed", 0.5D, 0.0D, 3.0D);

        THROW_MOMENTUM_SCALE = b
                .comment("How much of the thrower's movement is added. A run-up",
                        "genuinely adds distance; backing away takes it off.",
                        "Default: 0.5 (0.0 - 3.0)")
                .defineInRange("throwMomentumScale", 0.5D, 0.0D, 3.0D);

        PICKUP_COOLDOWN = b
                .comment("Ticks before a picked-up ball can be placed or thrown again.",
                        "20 ticks = 1 second.",
                        "Default: 30 (0 - 600)")
                .defineInRange("pickupCooldown", 30, 0, 600);

        b.pop();

        SPEC = b.build();
    }
}