package com.p1emc.provfootball.entity;

import com.p1emc.provfootball.PlayerMomentumTracker;
import com.p1emc.provfootball.item.ModItems;
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

public class FootballEntity extends Entity {

    // ------------------------------------------------------------------
    // Tuning
    // ------------------------------------------------------------------

    // --- flight ---
    //higher = stronger gravity
    private static final double GRAVITY = 0.045D;
    // 1-value = % speed lost per tick, 0.99 means 1% lost per tick
    private static final double AIR_DRAG = 0.99D;
    //Higher means ball travels further
    private static final double GROUND_FRICTION = 0.93D;
    //Higher makes it bouncier
    private static final double BOUNCE_VERTICAL = 0.55D;
    //Higher means walls absorb less energy
    private static final double BOUNCE_HORIZONTAL = 0.75D;
    //Effect of bouncing on speed
    private static final double BOUNCE_CROSS_AXIS = 0.85D;
    private static final double WALL_CROSS_AXIS = 0.9D;
    //Below this value speed is set to 0, avoids infinite sliding
    private static final double REST_THRESHOLD = 0.003D;

    // --- striking ---
    // Deliberately weak. A standing strike should be a nudge; momentum is what
    // makes a running pass travel. That gap is the number everything else gets
    // balanced against -- a shot has to clearly beat a sprinting pass.
    private static final double STRIKE_POWER = 0.3D;
    private static final double SPRINT_MOMENTUM = 0.9D;
    private static final double WALK_MOMENTUM = 0.5D;

    // How much of the ball's existing velocity survives a strike. Aim dominates,
    // but a fast incoming ball still carries extra -- so a volley off a hard pass
    // goes further than one struck from a standstill. Raise for deflections that
    // feel heavier, lower for strikes that fully reset the ball.
    private static final double INCOMING_BLEND = 0.25D;

    // --- flick (crouch) ---
    // Straight up if the ball is still, a chip if it was already moving, because
    // the horizontal component is preserved rather than replaced.
    private static final double FLICK_LIFT = 0.45D;
    private static final double FLICK_HORIZONTAL_KEEP = 0.5D;

    // --- curve ---
    // Ignore tiny offsets so a roughly-central hit is genuinely straight, rather
    // than picking up noise from aim jitter. Raise if every strike curves.
    private static final double STRIKE_DEADZONE = 0.15D;
    private static final double SPIN_POWER = 0.05D;

    // Real spin barely decays in flight -- a ball keeps most of its rotation over
    // a two second flight, so the curve continues the whole way rather than dying
    // early. Lower this if balls start orbiting.
    private static final float SPIN_DECAY = 0.99F;

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
    // Physics
    // ------------------------------------------------------------------

    @Override
    public void tick() {
        super.tick();

        if (this.kickCooldown > 0) {
            this.kickCooldown--;
        }

        Vec3 motion = this.getDeltaMovement().add(0.0D, -GRAVITY, 0.0D);

        // "Position last tick", which the renderer interpolates from.
        // LivingEntity maintains these for you; plain Entity does not.
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();

        this.setDeltaMovement(motion);
        this.move(MoverType.SELF, motion);

        // move() zeroes the delta on any axis that collided. Comparing before and
        // after is how we know what we hit and how hard.
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

            if (Math.abs(after.x) < 1.0E-5D && Math.abs(motion.x) > 0.02D) {
                nx = -motion.x * BOUNCE_HORIZONTAL;
                hit = true;
            }
            if (Math.abs(after.z) < 1.0E-5D && Math.abs(motion.z) > 0.02D) {
                nz = -motion.z * BOUNCE_HORIZONTAL;
                hit = true;
            }

            // Cost the perpendicular axes too, so glancing hits scrub speed
            // instead of skimming along the wall at full pace.
            if (hit) {
                nx *= WALL_CROSS_AXIS;
                nz *= WALL_CROSS_AXIS;
                ny *= WALL_CROSS_AXIS;
            }
        }

        // --- vertical bounce -----------------------------------------------
        if (this.verticalCollision) {
            if (motion.y < -0.12D) {
                ny = -motion.y * BOUNCE_VERTICAL;

                // Impacts cost speed in every direction, not just the one you
                // landed on. Without this a hard strike skips along the ground.
                nx *= BOUNCE_CROSS_AXIS;
                nz *= BOUNCE_CROSS_AXIS;
            } else {
                // Below the threshold, treat it as a landing. Otherwise the ball
                // jitters forever on ever-smaller hops.
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
            nx *= AIR_DRAG;
            nz *= AIR_DRAG;
        }

        // Multiplication approaches zero without arriving, so the ball would creep
        // imperceptibly forever. Snap it.
        if (Math.abs(nx) < REST_THRESHOLD) nx = 0.0D;
        if (Math.abs(nz) < REST_THRESHOLD) nz = 0.0D;
        if (this.onGround() && Math.abs(ny) < 0.05D) ny = 0.0D;

        // --- curve (Magnus effect) ------------------------------------------
        // A spinning ball is pushed sideways, and the force scales with speed. A
        // driven ball curves hard, a slow roller barely bends -- which is what
        // people expect without being told.
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

        this.setDeltaMovement(nx, ny, nz);
    }

    // ------------------------------------------------------------------
    // Striking
    // ------------------------------------------------------------------

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.level().isClientSide() || this.isRemoved()) {
            return false;
        }

        // Only players strike the ball. Fire, cacti, arrows and explosions all
        // arrive here too and are ignored. Returning false means "not damaged",
        // which is honest -- the ball has no health and nothing destroys it.
        if (!(source.getEntity() instanceof Player player)) {
            return false;
        }

        // Creative + crouch deletes the ball, so creative players can clear
        // strays. Checked before the flick branch, which also uses crouch.
        if (player.isCreative() && player.isCrouching()) {
            this.discard();
            return true;
        }

        if (this.kickCooldown > 0) {
            return true;
        }
        this.kickCooldown = 3;

        // Crouch lofts, everything else strikes along your aim. No airborne check
        // is needed: striking a bouncing ball downward SHOULD smash it into the
        // ground, and crouching lifts it again regardless of what it was doing.
        if (player.isCrouching()) {
            flick(player);
        } else {
            strike(player);
        }

        // true = "the hit landed", so the attacker gets their swing animation
        // even though no damage was dealt.
        return true;
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
        Vec3 incoming = this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).scale(INCOMING_BLEND);

        Vec3 result = look.scale(STRIKE_POWER)
                .add(momentum.scale(weight))
                .add(incoming);

        this.setDeltaMovement(result);
        applySpin(player);

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

        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 reach = eye.add(look.scale(6.0D));

        // Where the aim ray enters the ball's box. Empty if it misses, which
        // should not happen given hurt() fired -- but lag between the client's aim
        // and the server's copy of the ball position makes it possible.
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
