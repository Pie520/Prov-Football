package com.p1emc.provfootball.client;

import com.p1emc.provfootball.ProvFootball;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class ModModelLayers {

    public static final ModelLayerLocation FOOTBALL = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(ProvFootball.MODID, "football"), "main");
}