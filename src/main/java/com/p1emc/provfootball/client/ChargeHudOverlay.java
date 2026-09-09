package com.p1emc.provfootball.client;

import com.p1emc.provfootball.config.ConfigCache;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

public class ChargeHudOverlay {

    private static final int BAR_WIDTH = 80;
    private static final int BAR_HEIGHT = 6;

    private static final int BOTTOM_MARGIN = 55;

    // Charge power colours
    private static final int COLOUR_BACKGROUND = 0xC0000000;
    private static final int COLOUR_BORDER     = 0xFF202020;
    private static final int COLOUR_WEAK       = 0xFF9E9E9E;
    private static final int COLOUR_READY      = 0xFF42b528;
    private static final int COLOUR_HIGH       = 0xFFFFC107;
    private static final int COLOUR_FULL       = 0xFFE53935;

    // Elevation marker, riding along the bar
    private static final int COLOUR_ELEVATION = 0xFFFFFFFF;
    private static final int COLOUR_NOTCH     = 0xFF808080;

    private static final int NOTCHES = 4;
    private static final int NOTCH_HEIGHT = 2;
    private static final int MARKER_WIDTH = 2;
    private static final int MARKER_OVERHANG = 1;

    // Ticks to fade in. Derived from the charge value itself rather than a
    // separate counter, so it also fades out as the charge drains away.
    private static final float FADE_TICKS = 6.0F;

    /**
     * Scales a colour's existing alpha by the fade amount, rather than
     * replacing it
     */
    private static int withFade(int colour, float fade) {
        int baseAlpha = (colour >>> 24) & 0xFF;
        int alpha = Math.round(baseAlpha * Mth.clamp(fade, 0.0F, 1.0F));
        return (alpha << 24) | (colour & 0x00FFFFFF);
    }

    public static void render(GuiGraphics guiGraphics, DeltaTracker delta) {
        int charge = ChargeTracker.getCharge();

        if (charge <= 0) {
            return;
        }

        float fade = Mth.clamp(charge / FADE_TICKS, 0.0F, 1.0F);

        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();

        int x = (screenWidth - BAR_WIDTH) / 2;
        int y = screenHeight - BOTTOM_MARGIN;

        guiGraphics.fill(x - 1, y - 1, x + BAR_WIDTH + 1, y + BAR_HEIGHT + 1,
                withFade(COLOUR_BORDER, fade));
        guiGraphics.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT,
                withFade(COLOUR_BACKGROUND, fade));

        // Power
        float progress = ChargeTracker.getProgress();
        int filled = Math.round(BAR_WIDTH * progress);

        int colour;
        if (charge < ConfigCache.minCharge) {
            colour = COLOUR_WEAK;
        } else {
            // Progress through the USABLE range, not the whole bar, so the
            // bands line up with what defenders see in the particles.
            float usable = (float) (charge - ConfigCache.minCharge)
                    / (ConfigCache.maxCharge - ConfigCache.minCharge);

            if (usable >= ConfigCache.zoneRed) {
                colour = COLOUR_FULL;
            } else if (usable >= ConfigCache.zoneAmber) {
                colour = COLOUR_HIGH;
            } else {
                colour = COLOUR_READY;
            }
        }

        if (filled > 0) {
            guiGraphics.fill(x, y, x + filled, y + BAR_HEIGHT, withFade(colour, fade));
        }

        // Elevation
        // Quarter marks along the top edge. Reference points rather than stops:
        // free kicks come from anywhere, so the useful elevation varies
        // continuously and snapping would make most of them unhittable.
        for (int i = 1; i < NOTCHES; i++) {
            int notchX = x + (BAR_WIDTH * i / NOTCHES);
            guiGraphics.fill(notchX, y, notchX + 1, y + NOTCH_HEIGHT,
                    withFade(COLOUR_NOTCH, fade));
        }

        // getXRot() is negative looking up, so negate. Clamped, so anything
        // steeper than the maximum pins the marker at the right-hand end
        float elevation = Mth.clamp(
                -ChargeTracker.getStartPitch() / (float) ConfigCache.shotMaxElevation,
                0.0F, 1.0F);

        // Drawn full height with a slight overhang top and bottom
        int markerX = x + Math.round((BAR_WIDTH - MARKER_WIDTH) * elevation);
        guiGraphics.fill(markerX, y - MARKER_OVERHANG,
                markerX + MARKER_WIDTH, y + BAR_HEIGHT + MARKER_OVERHANG,
                withFade(COLOUR_ELEVATION, fade));
    }
}