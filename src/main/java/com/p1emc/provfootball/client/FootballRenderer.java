package com.p1emc.provfootball.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.p1emc.provfootball.ProvFootball;
import com.p1emc.provfootball.entity.FootballEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class FootballRenderer extends EntityRenderer<FootballEntity> {


    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ProvFootball.MODID, "textures/entity/football.png");


    private final FootballModel model;

    public FootballRenderer(EntityRendererProvider.Context context) {
        super(context);


        this.model = new FootballModel(context.bakeLayer(ModModelLayers.FOOTBALL));

        this.shadowRadius = 0.25F;
    }

    @Override
    public ResourceLocation getTextureLocation(FootballEntity entity) {

        return TEXTURE;
    }

    @Override
    public void render(FootballEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();

        // Model root sits 16px above the entity's feet, shift it back down.
        poseStack.translate(0.0F, -1F, 0.0F);

        VertexConsumer vertexConsumer = buffer.getBuffer(this.model.renderType(TEXTURE));

        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight,
                OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);

        poseStack.popPose();

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }
}