package com.p1emc.provfootball.client;

import com.p1emc.provfootball.ProvFootball;
import com.p1emc.provfootball.entity.ModEntities;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@Mod(value = ProvFootball.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = ProvFootball.MODID, value = Dist.CLIENT)
public class ProvFootballClient {

    public ProvFootballClient(ModContainer container) {
    }

    @SubscribeEvent
    static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.FOOTBALL.get(), FootballRenderer::new);
    }

    @SubscribeEvent
    static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.FOOTBALL, FootballModel::createBodyLayer);
    }

    @SubscribeEvent
    static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(ModKeyBindings.CHARGE_SHOT);
    }

    @SubscribeEvent
    static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(
                ResourceLocation.fromNamespaceAndPath(ProvFootball.MODID, "charge_bar"),
                ChargeHudOverlay::render);
    }
}