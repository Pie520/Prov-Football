package com.p1emc.provfootball;

import com.p1emc.provfootball.config.ConfigCache;

// Plain constants, no client imports, so both sides can reference it safely.
// ChargeTracker is client-only and a dedicated server would crash trying to
// load it -- which is why these do not live there.
public class ChargeConstants {

    public static final int MAX_CHARGE = ConfigCache.maxCharge;   // 1.5 seconds to full
    public static final int MIN_CHARGE = ConfigCache.minCharge;    // below this, a release does nothing

    public static final int DECAY_PER_TICK = ConfigCache.decayPerTick;

    //How long before decay begins
    public static final int RELEASE_GRACE = ConfigCache.releaseGrace;


    // Maximum speed reduction at full charge.
    public static final double MAX_SLOWDOWN = ConfigCache.maxSlowdown;
}