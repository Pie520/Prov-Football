package com.p1emc.provfootball;

// Plain constants, no client imports, so both sides can reference it safely.
// ChargeTracker is client-only and a dedicated server would crash trying to
// load it -- which is why these do not live there.
public class ChargeConstants {

    public static final int MAX_CHARGE = 30;   // 1.5 seconds to full
    public static final int MIN_CHARGE = 8;    // below this, a release does nothing

    public static final int DECAY_PER_TICK = 2;

    //How long before decay begins
    public static final int RELEASE_GRACE = 5;


    // Maximum speed reduction at full charge.
    public static final double MAX_SLOWDOWN = 0.6D;
}