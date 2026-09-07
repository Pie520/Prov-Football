package com.p1emc.provfootball.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {

    public static final KeyMapping CHARGE_SHOT = new KeyMapping(

            "key.provfootball.charge_shot",

            InputConstants.Type.KEYSYM,

            GLFW.GLFW_KEY_V,

            "key.categories.provfootball");
}