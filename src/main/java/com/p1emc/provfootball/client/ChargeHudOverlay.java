package com.p1emc.provfootball.client;

import com.p1emc.provfootball.ChargeConstants;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

import static com.p1emc.provfootball.events.PlayerChargeTracker.ZONE_AMBER;
import static com.p1emc.provfootball.events.PlayerChargeTracker.ZONE_RED;

public class ChargeHudOverlay {

    private static final int BAR_WIDTH = 80;
    private static final int BAR_HEIGHT = 6;

    // How far above the bottom of the screen. Clears the hotbar and the
    // health/hunger rows without sitting in the middle of the view.
    private static final int BOTTOM_MARGIN = 60;

    // ARGB, and the leading FF is the alpha byte. Get that wrong -- 0x00FFAA00
    // instead of 0xFFFFAA00 -- and the rect is fully transparent, draws
    // nothing, and looks exactly like a broken render call.
    private static final int COLOUR_BACKGROUND = 0xC0000000;  // translucent black
    private static final int COLOUR_BORDER     = 0xFF202020;
    private static final int COLOUR_WEAK       = 0xFF9E9E9E;  // grey, below minimum
    private static final int COLOUR_READY      = 0xFF42b528;  // green
    private static final int COLOUR_HIGH = 0xFFFFC107;  // amber
    private static final int COLOUR_FULL       = 0xFFE53935; // red
// red

    // Where the amber turns red. Not full charge -- a little warning before the
    // cap reads better than a colour that only appears at the very end.

    public static void render(GuiGraphics guiGraphics, DeltaTracker delta) {
        int charge = ChargeTracker.getCharge();

        // No bar when idle. The decay means it lingers briefly after release,
        // which is intentional -- you can see the charge draining.
        if (charge <= 0) {
            return;
        }

        // GUI coordinates, not real pixels: already divided by the player's GUI
        // scale, so these numbers mean the same thing at any scale setting.
        // Origin is top-left with y increasing DOWNWARD.
        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();

        int x = (screenWidth - BAR_WIDTH) / 2;
        int y = screenHeight - BOTTOM_MARGIN;

        // fill() takes two corners -- x1, y1, x2, y2 -- not a width and height.
        // Easy to pass (x, y, width, height) by reflex and get a rect that
        // starts in the right place and ends somewhere strange.
        guiGraphics.fill(x - 1, y - 1, x + BAR_WIDTH + 1, y + BAR_HEIGHT + 1, COLOUR_BORDER);
        guiGraphics.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, COLOUR_BACKGROUND);

        float progress = ChargeTracker.getProgress();
        int filled = Math.round(BAR_WIDTH * progress);

        int colour;
        if (charge < ChargeConstants.MIN_CHARGE) {
            colour = COLOUR_WEAK;
        } else {
            // Same measure the particles use: progress through the USABLE range, not
            // the whole bar. The dead ticks below MIN_CHARGE are excluded so the bands
            // line up with what defenders see.
            float usable = (float) (charge - ChargeConstants.MIN_CHARGE)
                    / (ChargeConstants.MAX_CHARGE - ChargeConstants.MIN_CHARGE);

            if (usable >= ZONE_RED) {
                colour = COLOUR_FULL;
            } else if (usable >= ZONE_AMBER) {
                colour = COLOUR_HIGH;
            } else {
                colour = COLOUR_READY;
            }
        }

        if (filled > 0) {
            guiGraphics.fill(x, y, x + filled, y + BAR_HEIGHT, colour);
        }
    }
}