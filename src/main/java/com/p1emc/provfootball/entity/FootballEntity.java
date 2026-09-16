package com.p1emc.provfootball.entity;

import com.p1emc.provfootball.config.ConfigCache;
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


    private int spawnGrace;

    //Below this value speed is set to 0, avoids infinite sliding
    private static final double REST_THRESHOLD = 0.005D;


    private int headerCooldown;


    private int dribbleCooldown;

    private UUID chopTracked;
    private float chopLastYaw;
    private float chopYawAccum;
    private int chopCooldown;

    // --- possession ---------------------------------------------------------

    private UUID lastToucher;

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
        this.spawnGrace = ConfigCache.spawnGrace;
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

        player.getCooldowns().addCooldown(ModItems.FOOTBALL.get(), ConfigCache.pickupCooldown);
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
        float volume = (float) Mth.clamp(impact / ConfigCache.maxSpeed, 0.1D, 0.8D);
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                ModSounds.KICK_BALL.get(), SoundSource.NEUTRAL,
                volume, 0.8F + this.random.nextFloat() * 0.2F);
    }

    private void playKickSound(float volumeScale) {
        // Speed AFTER the velocity is set, so the sound matches what actually
        // happened rather than what was requested.
        double speed = this.getDeltaMovement().length();
        float volume = (float) Mth.clamp(speed / ConfigCache.maxSpeed, MIN_KICK_VOLUME, 1.0D) * volumeScale;

        // null as the first argument means "send to every nearby client"
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

        if (this.chopCooldown > 0) {
            this.chopCooldown--;
        }



        Vec3 motion = this.getDeltaMovement().add(0.0D, -ConfigCache.gravity, 0.0D);


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
                nx = -motion.x * ConfigCache.bounceHorizontal;
                impact = Math.max(impact, Math.abs(motion.x));
                hit = true;
            }
            if (Math.abs(after.z) < 1.0E-5D && Math.abs(motion.z) > 0.02D) {
                nz = -motion.z * ConfigCache.bounceHorizontal;
                impact = Math.max(impact, Math.abs(motion.z));
                hit = true;
            }

            if (hit) {
                nx *= ConfigCache.wallCrossAxis;
                nz *= ConfigCache.wallCrossAxis;
                ny *= ConfigCache.wallCrossAxis;
                playBounceSound(impact);
            }
        }

        // --- vertical bounce -----------------------------------------------
        if (this.verticalCollision) {
            if (motion.y < -0.12D) {
                double impact = Math.abs(motion.y);
                double restitution = Math.max(ConfigCache.bounceMin, ConfigCache.bounceVertical - impact * ConfigCache.bounceFalloff);
                ny = impact * restitution;

                nx *= ConfigCache.bounceCrossAxis;
                nz *= ConfigCache.bounceCrossAxis;

                playBounceSound(impact);
            } else {
                ny = 0.0D;
            }
        }



        // --- friction -------------------------------------------------------
        // After the bounces, deliberately: friction should bleed the reflected
        // velocity, not the incoming one.
        if (this.onGround()) {
            nx *= ConfigCache.groundFriction;
            nz *= ConfigCache.groundFriction;
        } else {
            double speed = Math.sqrt(nx * nx + nz * nz);
            double drag = 1.0 - (ConfigCache.dragCoefficient * speed);
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

            this.spin *= ConfigCache.spinDecay;
        }

        // Scale all three axes together so a capped ball still travels exactly
        // where it was aimed
        double speed = Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (speed > ConfigCache.maxSpeed) {
            double scale = ConfigCache.maxSpeed / speed;
            nx *= scale;
            ny *= scale;
            nz *= scale;
        }

        this.setDeltaMovement(nx, ny, nz);

        updateRoll(nx, nz);

        if (!this.level().isClientSide()) {
            // Before dribbling, so a flick registers on the same tick it happens.
            trackChopYaw();

            // Heading first, a ball at head height should not also be dribbled.
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
        if (owner.distanceToSqr(this) > ConfigCache.shieldRadiusSqr) {
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
        return Mth.clamp(dot, ConfigCache.shieldMinMultiplier, 1.0D);
    }

    private void setPossession(Player player) {
        this.lastToucher = player.getUUID();
    }

    /**
     * Walk-to-sprint blend.
     */
    private static double loose(double walk, double sprint, double t) {
        return walk + (sprint - walk) * t;
    }

    /**
     * Accumulates the ball owner's view swing, for chop detection.
     *
     * Keyed on lastToucher rather than on whoever happens to be in range
     */
    private void trackChopYaw() {
        if (this.lastToucher == null) {
            this.chopTracked = null;
            this.chopYawAccum = 0.0F;
            return;
        }

        Player owner = this.level().getPlayerByUUID(this.lastToucher);
        if (owner == null) {
            // Logged out, or in another dimension
            this.chopTracked = null;
            this.chopYawAccum = 0.0F;
            return;
        }

        float yaw = owner.getYRot();

        // Possession changed
        if (!owner.getUUID().equals(this.chopTracked)) {
            this.chopTracked = owner.getUUID();
            this.chopLastYaw = yaw;
            this.chopYawAccum = 0.0F;
            return;
        }

        // Decay first, then add
        this.chopYawAccum *= (float) ConfigCache.dribbleChopDecay;

        // Signed shortest-arc difference, so a flick left and a flick right are
        // distinguishable
        this.chopYawAccum += Mth.degreesDifference(this.chopLastYaw, yaw);
        this.chopLastYaw = yaw;

        if (Math.abs(this.chopYawAccum) < 0.01F) {
            this.chopYawAccum = 0.0F;
        }
    }

    private void dribble() {
        if (this.spawnGrace > 0) {
            return;
        }

        for (Player player : this.level().getEntitiesOfClass(
                Player.class, this.getBoundingBox().inflate(ConfigCache.dribbleReach))) {

            if (player.isSpectator()) {
                continue;
            }

            // Re-read per player
            Vec3 current = this.getDeltaMovement();
            Vec3 ballHorizontal = new Vec3(current.x, 0.0D, current.z);
            double ballSpeed = ballHorizontal.length();

            // Vertical band around the player's feet
            double lift = player.getY() - this.getY();
            if (lift > ConfigCache.dribbleStepOver
                    || -lift > ConfigCache.dribbleMaxHeight) {
                continue;
            }

            // The tracker smoothed over several ticks
            Vec3 tracked = PlayerMomentumTracker.get(player).multiply(1.0D, 0.0D, 1.0D);
            Vec3 instant = player.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D);
            double trackedSpeed = tracked.length();
            double instantSpeed = instant.length();

            Vec3 heading = Vec3.ZERO;
            if (instantSpeed > 1.0E-4D) {
                heading = heading.add(
                        instant.scale(ConfigCache.dribbleInstantBlend / instantSpeed));
            }
            if (trackedSpeed > 1.0E-4D) {
                heading = heading.add(
                        tracked.scale((1.0D - ConfigCache.dribbleInstantBlend) / trackedSpeed));
            }
            if (heading.lengthSqr() < 1.0E-8D) {
                continue;
            }
            Vec3 runDir = heading.normalize();

            double playerSpeed = Math.max(trackedSpeed, instantSpeed);
            if (playerSpeed < 0.004D) {
                continue;
            }

            Vec3 toBall = new Vec3(
                    this.getX() - player.getX(), 0.0D, this.getZ() - player.getZ());
            double gap = toBall.length();

            // Explicit range gate
            if (gap > ConfigCache.dribbleReach) {
                continue;
            }

            // Close control
            double closeness = ConfigCache.dribbleCloseRadius <= 0.0D ? 0.0D
                    : Mth.clamp(1.0D - gap / ConfigCache.dribbleCloseRadius, 0.0D, 1.0D);

            double pushMultiplier = shieldMultiplier(player);
            if (pushMultiplier <= 0.0D) {
                continue;
            }

            double t = player.isSprinting() ? 1.0D : 0.0D;

            // Clamped inside the carry range
            double standoff = Math.min(
                    loose(ConfigCache.dribbleStandoffWalk,
                            ConfigCache.dribbleStandoffSprint, t),
                    ConfigCache.dribbleReach * ConfigCache.dribbleStandoffMargin);
            double gain = loose(
                    ConfigCache.dribbleStandoffGainWalk,
                    ConfigCache.dribbleStandoffGainSprint, t);

            // --- chop ----------------------------------------------------------

            boolean chop = this.chopCooldown <= 0
                    && player.getUUID().equals(this.chopTracked)
                    && closeness > 0.0D
                    && playerSpeed > ConfigCache.dribbleChopMinSpeed
                    && Math.abs(this.chopYawAccum) > ConfigCache.dribbleChopYaw;

            if (chop) {

                Vec3 lookDir = player.getLookAngle().multiply(1.0D, 0.0D, 1.0D);
                if (lookDir.lengthSqr() > 1.0E-4D) {
                    lookDir = lookDir.normalize();

                    Vec3 target = new Vec3(player.getX(), 0.0D, player.getZ())
                            .add(lookDir.scale(standoff));
                    Vec3 shift = target.subtract(new Vec3(this.getX(), 0.0D, this.getZ()));

                    Vec3 chopVel = shift.scale(1.0D / ConfigCache.dribbleChopTicks)
                            .add(lookDir.scale(playerSpeed));

                    if (t > 0.0D) {
                        chopVel = chopVel.scale(ConfigCache.dribbleChopSprintScale);
                    }

                    double chopSpeed = chopVel.length();
                    if (chopSpeed > ConfigCache.dribbleChopMaxSpeed) {
                        chopVel = chopVel.scale(ConfigCache.dribbleChopMaxSpeed / chopSpeed);
                    }

                    this.setDeltaMovement(chopVel.x, current.y, chopVel.z);
                    this.hasImpulse = true;

                    this.chopCooldown = ConfigCache.dribbleChopCooldown;
                    this.chopYawAccum = 0.0F;

                    // Deliberately not setting dribbleCooldown
                    setPossession(player);
                    break;
                }
            }

            // Front arc
            double arcDot = Mth.lerp(closeness,
                    ConfigCache.dribbleFrontArcDot, ConfigCache.dribbleCloseArcDot);
            boolean behind = gap > 1.0E-4D && toBall.scale(1.0D / gap).dot(runDir) < arcDot;

            if (behind) {
                // A ball behind you cannot be kicked forward
                if (ballSpeed > ConfigCache.dribbleBehindRestSpeed) {
                    Vec3 braked = ballHorizontal.scale(ConfigCache.dribbleBehindBrake);
                    this.setDeltaMovement(braked.x, current.y, braked.z);
                    this.dribbleCooldown = ConfigCache.dribbleCooldown;
                    this.hasImpulse = true;
                }
                // Not a break, this ball may be in front of somebody else.
                continue;
            }

            double ahead = toBall.dot(runDir);
            double along = ballHorizontal.dot(runDir);
            Vec3 lateral = ballHorizontal.subtract(runDir.scale(along));

            // The ball is asked to travel at EXACTLY the player's pace
            double desiredAlong = playerSpeed + (standoff - ahead) * gain;
            desiredAlong = Math.max(desiredAlong, 0.0D);

            // A cut may interrupt the tail of the cooldown, otherwise a turn
            // reads as input lag.
            boolean cutting = ballSpeed > ConfigCache.dribbleCutMinSpeed
                    && ballHorizontal.scale(1.0D / ballSpeed).dot(runDir) < ConfigCache.dribbleCutDot;

            if (this.dribbleCooldown > (cutting ? ConfigCache.dribbleCutInterrupt : 0)) {
                continue;
            }

            // How hard this touch may change the ball's speed
            float sustained = PlayerMomentumTracker.getSustained(player);
            double authority = Mth.lerp(sustained, ConfigCache.dribbleMinTouch, 1.0D);

            authority = Math.max(authority, closeness * ConfigCache.dribbleCloseAuthority);
            authority *= pushMultiplier;

            if (ballSpeed > desiredAlong + ConfigCache.dribbleControlMargin) {
                authority *= ConfigCache.dribbleControlTouch;
            }

            double accel = ConfigCache.dribbleAccel * authority;
            double decel = ConfigCache.dribbleDecel * authority;

            double newAlong = along + Mth.clamp(desiredAlong - along, -decel, accel);

            // Sideways skid bled off
            double retainLateral = loose(
                    ConfigCache.dribbleRetainLateralWalk,
                    ConfigCache.dribbleRetainLateralSprint, t)
                    * Mth.lerp(closeness, 1.0D, ConfigCache.dribbleCloseRetainScale);

            Vec3 next = runDir.scale(newAlong).add(lateral.scale(retainLateral));

            // Nudge back onto the run line
            Vec3 offLine = toBall.subtract(runDir.scale(ahead));
            double offset = offLine.length();
            double deadzone = loose(
                    ConfigCache.dribbleDeadzoneWalk, ConfigCache.dribbleDeadzoneSprint, t);

            if (offset > deadzone) {
                double pullScale = Mth.lerp(closeness, 1.0D, ConfigCache.dribbleClosePullScale);
                double pull = Math.min(
                        offset * loose(ConfigCache.dribblePullWalk,
                                ConfigCache.dribblePullSprint, t) * pullScale,
                        loose(ConfigCache.dribblePullMaxWalk,
                                ConfigCache.dribblePullMaxSprint, t) * pullScale);
                next = next.add(offLine.scale(-pull / offset));
            }

            // Sprinting only
            double scatter = ConfigCache.dribbleScatter * t;
            if (scatter > 0.0D) {
                double kick = (this.random.nextDouble() * 2.0D - 1.0D) * scatter * playerSpeed;
                next = next.add(runDir.z * kick, 0.0D, -runDir.x * kick);
            }

            // Never slows a ball that was already faster
            double cap = Math.max(ConfigCache.dribbleSpeedCap, ballSpeed);
            double nextSpeed = next.length();
            if (nextSpeed > cap) {
                next = next.scale(cap / nextSpeed);
            }


            boolean significant = next.subtract(ballHorizontal).lengthSqr() > 1.0E-4D;

            this.setDeltaMovement(next.x, current.y, next.z);
            this.dribbleCooldown = ConfigCache.dribbleCooldown;
            if (significant) {
                this.hasImpulse = true;
            }

            setPossession(player);

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
                Player.class, this.getBoundingBox().inflate(ConfigCache.headerReach))) {

            if (player.isSpectator() || player.onGround()) {
                continue;
            }

            // A grounded ball can never pass this: it sits between y 0 and 0.5
            // while eyes are at 1.62, so the bands cannot overlap. That is what
            // makes the airborne check about jumping to MEET a ball rather than
            // about heading anything at your feet.
            double eye = player.getEyeY();
            if (ballCentre > eye + ConfigCache.headerBandAbove || ballCentre < eye - ConfigCache.headerBandBelow) {
                continue;
            }

            // Must be moving INTO the ball. Jumping vertically beside it is not a
            // header.
            Vec3 momentum = PlayerMomentumTracker.get(player);
            Vec3 toBall = new Vec3(
                    this.getX() - player.getX(), 0.0D, this.getZ() - player.getZ());

            if (momentum.lengthSqr() > 1.0E-6D && toBall.lengthSqr() > 1.0E-6D) {
                if (momentum.normalize().dot(toBall.normalize()) < ConfigCache.headerApproachDot) {
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

        reflected = reflected.scale(ConfigCache.headerRestitution);

        // Your own contribution, scaled by how fast you were moving.
        double effort = Math.min(momentum.length() / 0.25D, 1.0D);
        Vec3 added = normal.scale(ConfigCache.headerPower * effort);

        this.setDeltaMovement(reflected.add(added));

        // Shares kickCooldown, so you cannot head a ball you just struck.
        this.headerCooldown  = ConfigCache.headerCooldown;

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


        if (this.kickCooldown > 0 || this.headerCooldown > 0) {
            return true;
        }

        this.kickCooldown = ConfigCache.kickCooldown;

// Shoot takes priority: a charged player who happens to be crouching should
// shoot, not flick.
        int charge = PlayerChargeTracker.getCharge(player);

        if (charge >= ConfigCache.minCharge) {
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

        float t = (float) (charge - ConfigCache.minCharge)
                / (ConfigCache.maxCharge - ConfigCache.minCharge);
        t = Mth.clamp(t, 0.0F, 1.0F);

// Blend of smoothstep and cubic. Smoothstep alone leaves the top of the bar
// nearly flat; cubic alone kills the bottom half. Averaging keeps a live top
// end while mid-charge still means something.
        double smooth = t * t * (3.0D - 2.0D * t);
        double cube = t * t * t;
        double curve = (smooth + cube) / 2.0D;

        double power = Mth.lerp(curve, ConfigCache.shotPowerMin, ConfigCache.shotPowerMax);

        // Elevation comes from where the player was looking when they STARTED
        // charging, not from where they are aiming now.
        float startPitch = PlayerChargeTracker.getStartPitch(player);

        // getXRot() is -90 straight up, 0 at the horizon, +90 down. Negate so that
        // looking up is positive, then clamp: 45 degrees or more gives full lift,
        // looking level or below gives none.
        double elevation = Mth.clamp(-startPitch / ConfigCache.shotMaxElevation, 0.0D, 1.0D);

        // Horizontal direction still comes from the strike itself
        Vec3 flat = new Vec3(look.x, 0.0D, look.z).normalize();

        this.setDeltaMovement(
                flat.x * power,
                elevation * power * ConfigCache.shotLift,
                flat.z * power);

        applySpin(player);
        playKickSound(1.75f * (charge / ConfigCache.maxCharge));

        float chargePercent = (float) charge / ConfigCache.maxCharge;

        if (chargePercent >= ConfigCache.zoneRed) {

            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS,
                    1, 0.7F + this.random.nextFloat() * 0.2F);
        }


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

        // Momentum keeps its MAGNITUDE here
        double weight = player.isSprinting() ? ConfigCache.sprintMomentum : ConfigCache.walkMomentum;

        // Blend rather than replace. Aim dominates, but the ball's existing
        // velocity carries through

        Vec3 aim = look.scale(ConfigCache.strikePower);
        Vec3 incoming = this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).scale(ConfigCache.incomingBlend);
        Vec3 flat = momentum.scale(weight).add(incoming);

        this.setDeltaMovement(
                aim.x + flat.x,
                aim.y * ConfigCache.volleyLift,
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
                current.x * ConfigCache.flickHorizontalKeep,
                ConfigCache.flickLift,
                current.z * ConfigCache.flickHorizontalKeep);

        applySpin(player);
        playKickSound(1F);


        this.hasImpulse = true;
    }

    /**
     * Curve, from where on the ball the player struck it horizontally.
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

            // "Right" relative to the player's facing
            Vec3 right = new Vec3(-look.z, 0.0D, look.x).normalize();

            double h = deadzone(offset.dot(right) / radius);
            this.spin = (float) (-h * ConfigCache.spinPower);
        });
    }

    private static double deadzone(double value) {
        if (Math.abs(value) < ConfigCache.strikeDeadzone) {
            return 0.0D;
        }
        // Rescale so the effect starts from zero at the deadzone edge, rather than
        // jumping straight to 0.15 worth of curve on leaving it.
        double sign = Math.signum(value);
        double scaled = (Math.abs(value) - ConfigCache.strikeDeadzone) / (1.0D - ConfigCache.strikeDeadzone);
        return sign * Mth.clamp(scaled, 0.0D, 1.0D);
    }
}