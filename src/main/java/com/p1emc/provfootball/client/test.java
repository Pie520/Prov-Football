//package com.p1emc.provfootball.client;
//
//import com.p1emc.provfootball.ProvFootball;
//import com.mojang.blaze3d.platform.InputConstants;
//import com.zigythebird.playeranim.animation.PlayerAnimationController;
//import com.zigythebird.playeranim.api.PlayerAnimationAccess;
//import com.zigythebird.playeranim.api.PlayerAnimationFactory;
//import com.zigythebird.playeranimcore.animation.layered.modifier.MirrorModifier;
//import com.zigythebird.playeranimcore.enums.PlayState;
//import net.minecraft.client.KeyMapping;
//import net.minecraft.client.Minecraft;
//import net.minecraft.resources.ResourceLocation;
//import net.neoforged.api.distmarker.Dist;
//import net.neoforged.bus.api.SubscribeEvent;
//import net.neoforged.fml.common.EventBusSubscriber;
//import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
//import net.neoforged.neoforge.client.event.ClientTickEvent;
//import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
//import org.lwjgl.glfw.GLFW;
//
/////
/////
/////                TEMPORARY, KEEP IN MIND I WILL BE DELETING THIS
/////
/////
//
//
//
//@EventBusSubscriber(modid = ProvFootball.MODID, value = Dist.CLIENT)
//public class test {
//
//    private static final ResourceLocation LAYER_ID = ProvFootball.id("test_layer");
//    private static final ResourceLocation TEST_ANIM = ProvFootball.id("test");
//
//    private static final KeyMapping TEST_LEFT = new KeyMapping(
//            "key.provfootball.test_left", InputConstants.Type.KEYSYM,
//            GLFW.GLFW_KEY_A, "key.categories.provfootball");
//
//    private static final KeyMapping TEST_RIGHT = new KeyMapping(
//            "key.provfootball.test_right", InputConstants.Type.KEYSYM,
//            GLFW.GLFW_KEY_D, "key.categories.provfootball");
//
//    @SubscribeEvent
//    static void registerKeys(RegisterKeyMappingsEvent event) {
//        event.register(TEST_LEFT);
//        event.register(TEST_RIGHT);
//    }
//
//
//    @SubscribeEvent
//    static void clientSetup(FMLClientSetupEvent event) {
//        event.enqueueWork(() ->
//                PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
//                        LAYER_ID,
//
//                        1500,
//                        player -> new PlayerAnimationController(player,
//
//                                (controller, state, animSetter) -> PlayState.STOP)));
//
//
//    }
//
//    @SubscribeEvent
//    static void onClientTick(ClientTickEvent.Post event) {
//        var player = Minecraft.getInstance().player;
//        if (player == null) {
//            return;
//        }
//
//        boolean left = TEST_LEFT.consumeClick();
//        boolean right = TEST_RIGHT.consumeClick();
//
//        if (!left && !right) {
//            return;
//        }
//
//        PlayerAnimationController controller = (PlayerAnimationController)
//                PlayerAnimationAccess.getPlayerAnimationLayer(player, LAYER_ID);
//
//        controller.removeAllModifiers();
//
//        if (right) {
//            controller.addModifier(new MirrorModifier(), 0);
//        }
//
//        controller.triggerAnimation(TEST_ANIM);
//    }
//}