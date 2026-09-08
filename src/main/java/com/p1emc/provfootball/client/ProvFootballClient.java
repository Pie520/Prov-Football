package com.p1emc.provfootball.client;

import com.p1emc.provfootball.ChargeConstants;
import com.p1emc.provfootball.ProvFootball;
import com.p1emc.provfootball.entity.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

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

    private static final float MAX_SHAKE = 0.2F;

    @SubscribeEvent
    public static void onCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        int charge = ChargeTracker.getCharge();
        if (charge < ChargeConstants.MIN_CHARGE) {
            return;
        }

        float progress = Mth.clamp(
                (float) charge / ChargeConstants.MAX_CHARGE, 0.0F, 1.0F);

        // Squared, matching the slowdown curve -- barely there early, noticeable
        // at full. Keep the magnitude small: this should feel like effort, not
        // like being hit.
        float magnitude = progress * progress * MAX_SHAKE;

        RandomSource random = Minecraft.getInstance().level.random;

        event.setYaw(event.getYaw() + (random.nextFloat() - 0.5F) * magnitude);
        event.setPitch(event.getPitch() + (random.nextFloat() - 0.5F) * magnitude);
        event.setRoll(event.getRoll() + (random.nextFloat() - 0.5F) * magnitude * 0.5F);
    }
}