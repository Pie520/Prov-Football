package com.p1emc.provfootball.entity;

import com.p1emc.provfootball.ChargeConstants;
import com.p1emc.provfootball.events.PlayerMomentumTracker;
import com.p1emc.provfootball.item.ModItems;
import com.p1emc.provfootball.events.PlayerChargeTracker;
import com.p1emc.provfootball.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class FootballEntity extends Entity {

    // ------------------------------------------------------------------
    // Tuning
    // ------------------------------------------------------------------

    // Ticks after spawning during which the ball ignores contact
    private int spawnGrace;

    // --- flight ---
    //higher = stronger gravity
    private static final double GRAVITY = 0.045D;
    // lose % per tick per unit of speed
    private static final double DRAG_COEFFICIENT = 0.03D;
    //Higher means ball travels further
    private static final double GROUND_FRICTION = 0.93D;

// Restitution is not constant: a real ball deforms more on a hard impact and
// loses proportionally more energy. So the ceiling applies to gentle bounces
// and hard landings get taxed down toward the floor.
    private static final double BOUNCE_VERTICAL = 0.70D;   // gentle-impact restitution
    private static final double BOUNCE_FALLOFF = 0.30D;    // how much hard impacts are punished
    private static final double BOUNCE_MIN = 0.35D;        // floor, so fast balls still bounce

    //Higher means walls absorb less energy
    private static final double BOUNCE_HORIZONTAL = 0.70D;
    //Effect of bouncing on speed
    private static final double BOUNCE_CROSS_AXIS = 0.85D;
    private static final double WALL_CROSS_AXIS = 0.9D;
    //Below this value speed is set to 0, avoids infinite sliding
    private static final double REST_THRESHOLD = 0.005D;

    // --- passing ---
    // Weaker shots, higher value here means stronger shot
    private static final double STRIKE_POWER = 0.3D;
    private static final double SPRINT_MOMENTUM = 0.6D;
    private static final double WALK_MOMENTUM = 1D;

    // Aiming up should loft the ball meaningfully. STRIKE_POWER is tuned for how
// far a flat pass rolls, and at that magnitude the vertical component is too
// small to see -- roughly five ticks of rise against gravity. Scaling Y
// separately gives controllable volleys without changing ground passes.
    private static final double VOLLEY_LIFT = 1.5D;

    // Lower values make new strikes fully change ball directions
    // Higher mean harder passes have a greater effect on the direction of the volley
    private static final double INCOMING_BLEND = 0.25D;

    // --- shooting ---
    private static final double SHOT_POWER_MIN = 0.65D;
    private static final double SHOT_POWER_MAX = 1.4D;

    // How much of the shot's power goes upward at full elevation. Under 1.0 so a
// lofted shot still carries forward rather than going near-vertical.
    private static final double SHOT_LIFT = 0.75D;

    // Hard ceiling on total speed, applied after every force this tick. Catches
// anything that stacks, strikes, shots, bounces, Magnus
    private static final double MAX_SPEED = 1.6D;

    // Momentum normalised, angular only


    // --- flick (crouch) ---
    // Straight up if the ball is still, a chip if it was already moving
    private static final double FLICK_LIFT = 0.45D;
    private static final double FLICK_HORIZONTAL_KEEP = 0.25D;

    // --- heading -------------------------------------------------------------
// Contact-based
//
// A header redirects rather than replaces, reflect the incoming velocity
// about the plane your head presents. A driven cross headed at goal keeps its
// pace; a floated one does not. Crossing quality matters as a result, and a
// glancing header can send the ball behind you, which is correct.
    private static final double HEADER_RESTITUTION = 0.6D;

    // A head is not a foot. Most force a header adds on its own, scaled by how
// fast the player was moving.
    private static final double HEADER_POWER = 0.18D;

    // How far above and below eye level counts. Generous, since you are already
// jumping to meet a moving ball.
    private static final double HEADER_BAND_ABOVE = 0.6D;
    private static final double HEADER_BAND_BELOW = 0.4D;

    // You have to be closing on the ball, not merely near it. Without this a ball
// drifting past your face while you happen to be jumping heads itself.
    private static final double HEADER_APPROACH_DOT = 0.3D;

    // How far out to look for a player to head it.
    private static final double HEADER_REACH = 0.4D;

    // Long enough that one jump is one header.
    private static final int HEADER_COOLDOWN = 15;

    private int headerCooldown;

    // --- curve ---
    // Higher deadzone means you need to aim closer to the balls edge for spin
    // Spin power is the amount of curve
    private static final double STRIKE_DEADZONE = 0.15D;
    private static final double SPIN_POWER = 0.05D;

    // Lower this if the ball starts orbiting
    private static final float SPIN_DECAY = 0.98F;


    // --- dribbling ---------------------------------------------------------
// Only touch the ball when it is low enough to be at foot height.
    private static final double DRIBBLE_MAX_HEIGHT = 0.4D;

    // How close the player has to be. Their box inflated by this.
    private static final double DRIBBLE_REACH = 0.2D;

    // Touches are discrete, not continuous. Separate from kickCooldown so a
// dribble touch never blocks a strike or vice versa.
    private static final int DRIBBLE_COOLDOWN = 3;

    // Push per touch. Walking keeps the ball tight, sprinting shoves it further ahead
    private static final double DRIBBLE_PUSH_WALK = 0.14D;
    private static final double DRIBBLE_PUSH_SPRINT = 0.24D;

    // A ball already moving faster than this ignores dribble touches, so an
// incoming pass has to be controlled with a strike before you can carry it.
    private static final double DRIBBLE_MAX_BALL_SPEED = 0.26D;

    // How much of the push follows the direction you are RUNNING versus the
// direction from you to the ball. Lower values turn
// more sharply, since the away-vector shoves the ball whichever way you cut.
    private static final double DRIBBLE_MOVEMENT_BIAS = 0.3D;

    // Fraction of the ball's existing horizontal velocity that survives a touch.
// Raise toward 1.0 for a looser, more momentum-driven dribble.
    private static final double DRIBBLE_RETAIN = 0.6D;

    // Fine control. A player inching sideways should barely nudge the ball, the
// way a real drag is a much lighter touch than a push into space. Without
// this every touch is the same strength regardless of how fast you're moving,
// so the dribble is a series of identical taps and slow adjustments overshoot.
//
// Fraction of a full-strength touch you get at a standstill.
    private static final double DRIBBLE_MIN_TOUCH = 0.1D;

    // Movement speed at which a touch reaches full strength. Walking is roughly
// 0.21 blocks/tick, so this is a normal walking pace.
    private static final double DRIBBLE_FULL_TOUCH_SPEED = 0.30D;

    private int dribbleCooldown;

    // --- possession ---------------------------------------------------------
// Who touched the ball last, shielding should only work for who touched it last
    private UUID lastToucher;

    // --- shielding ----------------------------------------------------------
// Dictates how strong challenges should be from the back.
    //Higher values mean its easier to dispossess a player
    private static final double SHIELD_MIN_MULTIPLIER = 0.0D;


    // Rolling animation state. Client-visual only, derived from velocity, nothing
// here is synced, because tick() runs on both sides and each computes its own.
    public float roll;
    public float rollPrev;
    public float rollAxis;

    // Degrees per tick the ball is currently spinning
    private float rollSpeed;

    // How fast the spin bleeds away once the ball has stopped.
    private static final float ROLL_DECAY = 0.8F;

    // ------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------

    // Current sideways spin. Set on the strike, bled off during flight.
    public float spin;

    // Stops a fast clicker double-firing within one swing.
    private int kickCooldown;

    public FootballEntity(EntityType<? extends FootballEntity> type, Level level) {
        super(type, level);
    }

    public FootballEntity(Level level, double x, double y, double z) {
        this(ModEntities.FOOTBALL.get(), level);
        this.setPos(x, y, z);
        this.spawnGrace = 5;
    }


    // ------------------------------------------------------------------
    // Entity plumbing
    // ------------------------------------------------------------------

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    // ------------------------------------------------------------------
    // Pickup
    // ------------------------------------------------------------------

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {

        if (this.level().isClientSide()) {
            return player.getItemInHand(hand).isEmpty()
                    ? InteractionResult.SUCCESS
                    : InteractionResult.PASS;
        }

        if (!player.getItemInHand(hand).isEmpty()) {
            return InteractionResult.PASS;
        }

        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.EXPLOSION,
                    this.getX(), this.getY() + 0.25D, this.getZ(),
                    3, 0.15D, 0.15D, 0.15D, 0.0D);
        }

        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.6F, 1.4F);

        ItemStack stack = new ItemStack(ModItems.FOOTBALL.get());
        if (!player.addItem(stack)) {
            player.drop(stack, false);
        }

        player.getCooldowns().addCooldown(ModItems.FOOTBALL.get(), 60);
        this.discard();

        return InteractionResult.CONSUME;
    }

    // ------------------------------------------------------------------
    // Sounds
    // ------------------------------------------------------------------

    private static final float MIN_KICK_VOLUME = 0.25F;

    private void playBounceSound(double impact) {
        if (this.level().isClientSide()) {
            return;
        }
        float volume = (float) Mth.clamp(impact / MAX_SPEED, 0.1D, 0.8D);
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                ModSounds.KICK_BALL.get(), SoundSource.NEUTRAL,
                volume, 0.8F + this.random.nextFloat() * 0.2F);
    }

    private void playKickSound(float volumeScale) {
        // Speed AFTER the velocity is set, so the sound matches what actually
        // happened rather than what was requested.
        double speed = this.getDeltaMovement().length();
        float volume = (float) Mth.clamp(speed / MAX_SPEED, MIN_KICK_VOLUME, 1.0D) * volumeScale;

        // null as the first argument means "send to every nearby client" -- pass a
        // player there and that player is EXCLUDED, which is for prediction cases
        // where they already played it locally.
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                ModSounds.KICK_BALL.get(), SoundSource.PLAYERS,
                volume, 0.9F + this.random.nextFloat() * 0.2F);
    }

    public void setPlacementYaw(float yaw) {
        this.setYRot(yaw);
        this.yRotO = yaw;
    }

    // ------------------------------------------------------------------
    // Physics
    // ------------------------------------------------------------------

    @Override
    public void tick() {
        super.tick();

        if (this.kickCooldown > 0) {
            this.kickCooldown--;
        }

        if (this.headerCooldown > 0) {
            this.headerCooldown--;
        }

        if (this.dribbleCooldown > 0) {
            this.dribbleCooldown--;
        }
        if (this.spawnGrace > 0) {
            this.spawnGrace--;
        }



        Vec3 motion = this.getDeltaMovement().add(0.0D, -GRAVITY, 0.0D);


        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();



        this.setDeltaMovement(motion);
        this.move(MoverType.SELF, motion);


        Vec3 after = this.getDeltaMovement();
        double nx = after.x;
        double ny = after.y;
        double nz = after.z;



        // --- horizontal bounce ---------------------------------------------
        // horizontalCollision says a wall was hit but not which one, so check
        // each axis. The 0.02 floor stops an already-crawling axis being
        // "reflected" into a twitch against the wall forever.
        if (this.horizontalCollision) {
            boolean hit = false;
            double impact = 0.0D;

            if (Math.abs(after.x) < 1.0E-5D && Math.abs(motion.x) > 0.02D) {
                nx = -motion.x * BOUNCE_HORIZONTAL;
                impact = Math.max(impact, Math.abs(motion.x));
                hit = true;
            }
            if (Math.abs(after.z) < 1.0E-5D && Math.abs(motion.z) > 0.02D) {
                nz = -motion.z * BOUNCE_HORIZONTAL;
                impact = Math.max(impact, Math.abs(motion.z));
                hit = true;
            }

            if (hit) {
                nx *= WALL_CROSS_AXIS;
                nz *= WALL_CROSS_AXIS;
                ny *= WALL_CROSS_AXIS;
                playBounceSound(impact);
            }
        }

        // --- vertical bounce -----------------------------------------------
        if (this.verticalCollision) {
            if (motion.y < -0.12D) {
                double impact = Math.abs(motion.y);
                double restitution = Math.max(BOUNCE_MIN, BOUNCE_VERTICAL - impact * BOUNCE_FALLOFF);
                ny = impact * restitution;

                nx *= BOUNCE_CROSS_AXIS;
                nz *= BOUNCE_CROSS_AXIS;

                playBounceSound(impact);
            } else {
                ny = 0.0D;
            }
        }



        // --- friction -------------------------------------------------------
        // After the bounces, deliberately: friction should bleed the reflected
        // velocity, not the incoming one.
        if (this.onGround()) {
            nx *= GROUND_FRICTION;
            nz *= GROUND_FRICTION;
        } else {
            double speed = Math.sqrt(nx * nx + nz * nz);
            double drag = 1.0 - (DRAG_COEFFICIENT * speed);
            drag = Math.max(drag, 0.90);   // floor, so it never reverses or stops dead
            nx *= drag;
            nz *= drag;
        }

        // Multiplication approaches zero without arriving, so the ball would creep
        // imperceptibly forever. Snap it.
        if (Math.sqrt(nx * nx + nz * nz) < REST_THRESHOLD) {
            nx = 0.0D;
            nz = 0.0D;
        }
        if (this.onGround() && Math.abs(ny) < 0.05D) ny = 0.0D;

        // --- curve (Magnus effect) ------------------------------------------
        // A spinning ball is pushed sideways, and the force scales with speed
        if (Math.abs(this.spin) > 1.0E-4F) {
            double speed = Math.sqrt(nx * nx + nz * nz);

            if (speed > 1.0E-4D) {
                // Perpendicular to the CURRENT direction of travel, not the
                // original strike, so the curve follows the ball as it bends.
                double px = -nz / speed;
                double pz = nx / speed;

                nx += px * this.spin * speed;
                nz += pz * this.spin * speed;
            }

            this.spin *= SPIN_DECAY;
        }

        // Scale all three axes together so a capped ball still travels exactly
        // where it was aimed
        double speed = Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (speed > MAX_SPEED) {
            double scale = MAX_SPEED / speed;
            nx *= scale;
            ny *= scale;
            nz *= scale;
        }

        this.setDeltaMovement(nx, ny, nz);

        updateRoll(nx, nz);

        if (!this.level().isClientSide()) {
            // Heading first -- a ball at head height should not also be dribbled.
            if (!tryHeader()) {
                dribble();
            }
        }
    }


    /**
     * Dribbling
     */

    /**
     * How much a challenge survives the current owner's shield.
     * 1.0 if unshielded or if they are directly in front of the shielder, going
     * to 0 behind, you cannot reach through someone's back.
     */


    private double shieldMultiplier(Player challenger) {

        System.out.println("owner=" + this.lastToucher + " challenger=" + challenger.getUUID());

        // Unowned, or this player owns it
        if (this.lastToucher == null || this.lastToucher.equals(challenger.getUUID())) {
            return 1.0D;
        }

        Player owner = this.level().getPlayerByUUID(this.lastToucher);
        if (owner == null || owner.isSpectator()) {
            return 1.0D;
        }

        // Sprinting means the ball is running loose ahead of you hence no shielding
        if (owner.isSprinting()) {
            return 1.0D;
        }

        // Only shielding if they are actually near the ball.
        if (owner.distanceToSqr(this) > 4.0D) {
            return 1.0D;
        }

        Vec3 facing = owner.getLookAngle().multiply(1.0D, 0.0D, 1.0D).normalize();
        Vec3 toChallenger = new Vec3(
                challenger.getX() - owner.getX(), 0.0D, challenger.getZ() - owner.getZ());

        if (toChallenger.lengthSqr() < 1.0E-4D) {
            return 1.0D;
        }

        // 1.0 = directly in front, 0 = side on, -1 = behind. Clamping the negative
        // half to zero gives full push face-on, half side-on, nothing from behind.
        double dot = facing.dot(toChallenger.normalize());
        return Mth.clamp(dot, SHIELD_MIN_MULTIPLIER, 1.0D);
    }

    private void setPossession(Player player) {
        this.lastToucher = player.getUUID();
    }

    //Actual dribbling mechanic

    private void dribble() {
        if (this.dribbleCooldown > 0 || this.spawnGrace > 0) {
            return;
        }



        // Already moving too fast. Control it with a strike first.
        Vec3 current = this.getDeltaMovement();
        double ballSpeed = Math.sqrt(current.x * current.x + current.z * current.z);
        if (ballSpeed > DRIBBLE_MAX_BALL_SPEED) {
            return;
        }

        for (Player player : this.level().getEntitiesOfClass(
                Player.class, this.getBoundingBox().inflate(DRIBBLE_REACH))) {

            if (player.isSpectator()) {
                continue;
            }

// Only touch the ball at foot height
            if (this.getY() - player.getY() > DRIBBLE_MAX_HEIGHT) {
                continue;
            }

            Vec3 momentum = PlayerMomentumTracker.get(player);
            double moved = momentum.length();

            // Standing still is not dribbling.
            if (moved < 0.01D) {
                continue;
            }

// Shielding: if someone else is walking with this ball, a challenger only
// gets a touch to the extent they've got in front of them.
            double pushMultiplier = shieldMultiplier(player);
            if (pushMultiplier <= 0.0D) {
                continue;
            }

            Vec3 movementDir = momentum.normalize();

// Away from the player, horizontal only.
            Vec3 away = new Vec3(this.getX() - player.getX(), 0.0D, this.getZ() - player.getZ());
            if (away.lengthSqr() < 1.0E-4D) {
                away = player.getLookAngle().multiply(1.0D, 0.0D, 1.0D);
            }
            away = away.normalize();

            Vec3 dir = movementDir.scale(DRIBBLE_MOVEMENT_BIAS)
                    .add(away.scale(1.0D - DRIBBLE_MOVEMENT_BIAS))
                    .normalize();

            double push = player.isSprinting() ? DRIBBLE_PUSH_SPRINT : DRIBBLE_PUSH_WALK;

// Replaces the instantaneous-speed scaling. A committed run pushes the ball
// properly ahead and a quick adjustment barely moves it.
            float sustained = PlayerMomentumTracker.getSustained(player);
            double effort = Mth.lerp(sustained, DRIBBLE_MIN_TOUCH, 1.0D);

            push *= effort * pushMultiplier;

            System.out.println("mult=" + pushMultiplier + " push=" + push);

            Vec3 kept = this.getDeltaMovement().multiply(DRIBBLE_RETAIN, 1.0D, DRIBBLE_RETAIN);
            this.setDeltaMovement(kept.add(dir.scale(push)));
            this.dribbleCooldown = DRIBBLE_COOLDOWN;
            this.hasImpulse = true;


            //Keep track of who touched the ball last
            setPossession(player);

            // One touch per cooldown, even in a crowd.
            break;
        }
    }


    /**
     * Contact heading. Runs every tick from tick(), like dribbling
     *
     * @return true if a header happened, so the caller can skip dribbling.
     */
    private boolean tryHeader() {
        if (this.headerCooldown > 0 || this.spawnGrace > 0) {
            return false;
        }

        double ballCentre = this.getY() + this.getBbHeight() / 2.0D;

        for (Player player : this.level().getEntitiesOfClass(
                Player.class, this.getBoundingBox().inflate(HEADER_REACH))) {

            if (player.isSpectator() || player.onGround()) {
                continue;
            }

            // A grounded ball can never pass this: it sits between y 0 and 0.5
            // while eyes are at 1.62, so the bands cannot overlap. That is what
            // makes the airborne check about jumping to MEET a ball rather than
            // about heading anything at your feet.
            double eye = player.getEyeY();
            if (ballCentre > eye + HEADER_BAND_ABOVE || ballCentre < eye - HEADER_BAND_BELOW) {
                continue;
            }

            // Must be moving INTO the ball. Jumping vertically beside it is not a
            // header.
            Vec3 momentum = PlayerMomentumTracker.get(player);
            Vec3 toBall = new Vec3(
                    this.getX() - player.getX(), 0.0D, this.getZ() - player.getZ());

            if (momentum.lengthSqr() > 1.0E-6D && toBall.lengthSqr() > 1.0E-6D) {
                if (momentum.normalize().dot(toBall.normalize()) < HEADER_APPROACH_DOT) {
                    continue;
                }
            }

            header(player, momentum);
            return true;
        }

        return false;
    }

    private void header(Player player, Vec3 momentum) {
        Vec3 incoming = this.getDeltaMovement();
        Vec3 normal = player.getLookAngle();

        // Standard reflection about the plane whose normal is the look vector:
        //     r = v - 2(v . n)n
        // dot(v, n) is how much of the velocity runs along your look axis;
        // removing twice that flips it. Look straight into an incoming ball and it
        // goes back the way it came; angle your head and it glances off.
        Vec3 reflected = incoming.subtract(normal.scale(2.0D * incoming.dot(normal)));

        reflected = reflected.scale(HEADER_RESTITUTION);

        // Your own contribution, scaled by how fast you were moving.
        double effort = Math.min(momentum.length() / 0.25D, 1.0D);
        Vec3 added = normal.scale(HEADER_POWER * effort);

        this.setDeltaMovement(reflected.add(added));

        // Shares kickCooldown, so you cannot head a ball you just struck.
        this.headerCooldown  = HEADER_COOLDOWN;

        applySpin(player);
        playKickSound(0.9F);
        this.hasImpulse = true;
    }


    /**
     * Spins the ball at the rate it would turn if it rolled without slipping:
     * distance travelled divided by circumference, times 360.
     */
    private void updateRoll(double vx, double vz) {
        this.rollPrev = this.roll;

        double horizontalSpeed = Math.sqrt(vx * vx + vz * vz);

        // Axis freezes early. Below this, one axis can still be dying faster than
        // the other and atan2 swings around, which snaps the whole orientation.
        if (horizontalSpeed > 0.02D) {
            this.rollAxis = (float) (Mth.atan2(vz, vx) * (180.0D / Math.PI));
        }

        // Speed tracks the actual velocity right down to a stop, then eases out.
        if (horizontalSpeed > 1.0E-4D) {
            double circumference = Math.PI * this.getBbWidth();
            this.rollSpeed = (float) (horizontalSpeed / circumference * 360.0D);
        } else {
            this.rollSpeed *= ROLL_DECAY;
            if (this.rollSpeed < 0.01F) {
                this.rollSpeed = 0.0F;
            }
        }

        this.roll += this.rollSpeed;

        if (this.roll > 360.0F) {
            this.roll -= 360.0F;
            this.rollPrev -= 360.0F;
        }
    }

    // ------------------------------------------------------------------
    // Striking
    // ------------------------------------------------------------------

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.level().isClientSide() || this.isRemoved()) {
            return false;
        }

        // Only players can strike the ball
        if (!(source.getEntity() instanceof Player player)) {
            return false;
        }


        if (this.kickCooldown > 0) {
            return true;
        }
        this.kickCooldown = 3;

// Shoot takes priority: a charged player who happens to be crouching should
// shoot, not flick.
        int charge = PlayerChargeTracker.getCharge(player);

        if (charge >= ChargeConstants.MIN_CHARGE) {
            shoot(player, charge);
        } else if (player.isCrouching()) {
            flick(player);
        } else {
            strike(player);
        }

        // true = "the hit landed", so the attacker gets their swing animation
        // even though no damage was dealt.
        return true;
    }



    /**
     * Charged shot. Power scales with how long the keybind was held; direction
     * comes from aim, nudged slightly by which way the player was moving.
     */
    private void shoot(Player player, int charge) {
        Vec3 look = player.getLookAngle();

        float t = (float) (charge - ChargeConstants.MIN_CHARGE)
                / (ChargeConstants.MAX_CHARGE - ChargeConstants.MIN_CHARGE);
        t = Mth.clamp(t, 0.0F, 1.0F);

// Blend of smoothstep and cubic. Smoothstep alone leaves the top of the bar
// nearly flat; cubic alone kills the bottom half. Averaging keeps a live top
// end while mid-charge still means something.
        double smooth = t * t * (3.0D - 2.0D * t);
        double cube = t * t * t;
        double curve = (smooth + cube) / 2.0D;

        double power = Mth.lerp(curve, SHOT_POWER_MIN, SHOT_POWER_MAX);

        // Elevation comes from where the player was looking when they STARTED
        // charging, not from where they are aiming now.
        float startPitch = PlayerChargeTracker.getStartPitch(player);

        // getXRot() is -90 straight up, 0 at the horizon, +90 down. Negate so that
        // looking up is positive, then clamp: 45 degrees or more gives full lift,
        // looking level or below gives none.
        double elevation = Mth.clamp(-startPitch / 45.0F, 0.0D, 1.0D);

        // Horizontal direction still comes from the strike itself
        Vec3 flat = new Vec3(look.x, 0.0D, look.z).normalize();

        this.setDeltaMovement(
                flat.x * power,
                elevation * power * SHOT_LIFT,
                flat.z * power);

        applySpin(player);
        playKickSound(1f);

        PlayerChargeTracker.clear(player);

        this.hasImpulse = true;
    }

    /**
     * Normal strike. Direction comes entirely from where the player aims, which
     * means a ball on the ground gets driven flat and a ball in the air can be
     * volleyed at whatever angle you are looking.
     */

    private void strike(Player player) {
        Vec3 look = player.getLookAngle();
        Vec3 momentum = PlayerMomentumTracker.get(player);

        // Momentum keeps its MAGNITUDE here -- that is what makes a running strike
        // travel further than a standing one.
        double weight = player.isSprinting() ? SPRINT_MOMENTUM : WALK_MOMENTUM;

        // Blend rather than replace. Aim dominates, but the ball's existing
        // velocity carries through, so a volley off a hard pass beats one struck
        // from a standstill. Horizontal only -- inheriting the fall speed of a
        // dropping ball would drive every volley into the floor.

        Vec3 aim = look.scale(STRIKE_POWER);
        Vec3 incoming = this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).scale(INCOMING_BLEND);
        Vec3 flat = momentum.scale(weight).add(incoming);

        this.setDeltaMovement(
                aim.x + flat.x,
                aim.y * VOLLEY_LIFT,
                aim.z + flat.z);

        applySpin(player);
        playKickSound(1F);


        this.hasImpulse = true;
    }

    /**
     * Crouch flick. Adds lift while preserving whatever horizontal velocity the
     * ball already had, so a still ball pops straight up for a volley and a
     * moving ball becomes a chip. One action, two uses, decided by the ball's
     * state rather than an input mode.
     */
    private void flick(Player player) {
        Vec3 current = this.getDeltaMovement();

        this.setDeltaMovement(
                current.x * FLICK_HORIZONTAL_KEEP,
                FLICK_LIFT,
                current.z * FLICK_HORIZONTAL_KEEP);

        applySpin(player);
        playKickSound(1F);


        this.hasImpulse = true;
    }

    /**
     * Curve, from where on the ball the player struck it horizontally. Hit the
     * right side and it hooks left: your foot pushes that side away and the spin
     * brings it back. If it bends the wrong way, negate SPIN_POWER.
     *
     * Only the horizontal axis is used. The vertical offset is unusable in
     * practice -- from a standing player the top face of the ball dominates the
     * view, so nearly every strike reads as a maximum high hit and the lower half
     * is almost impossible to target. Lift lives on crouch instead.
     */
    private void applySpin(Player player) {
        double radius = this.getBbWidth() / 2.0D;

        // Every strike resets spin. Without this, a missed raycast leaves the
        // previous strike's spin in place and a dead-centre hit curves anyway.
        this.spin = 0.0F;

        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 reach = eye.add(look.scale(6.0D));

        this.getBoundingBox().clip(eye, reach).ifPresent(hit -> {
            Vec3 centre = new Vec3(this.getX(), this.getY() + radius, this.getZ());
            Vec3 offset = hit.subtract(centre);

            // "Right" relative to the player's facing -- look crossed with world
            // up. Without this, striking the east side of the ball would mean the
            // same thing regardless of which way the player stands.
            Vec3 right = new Vec3(-look.z, 0.0D, look.x).normalize();

            double h = deadzone(offset.dot(right) / radius);
            this.spin = (float) (-h * SPIN_POWER);
        });
    }

    private static double deadzone(double value) {
        if (Math.abs(value) < STRIKE_DEADZONE) {
            return 0.0D;
        }
        // Rescale so the effect starts from zero at the deadzone edge, rather than
        // jumping straight to 0.15 worth of curve on leaving it.
        double sign = Math.signum(value);
        double scaled = (Math.abs(value) - STRIKE_DEADZONE) / (1.0D - STRIKE_DEADZONE);
        return sign * Mth.clamp(scaled, 0.0D, 1.0D);
    }
}
