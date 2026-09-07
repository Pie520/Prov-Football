package com.p1emc.provfootball;

import com.p1emc.provfootball.entity.ModEntities;
import com.p1emc.provfootball.item.ModItems;
import com.p1emc.provfootball.sound.ModSounds;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;



@Mod(ProvFootball.MODID)
public class ProvFootball {
    public static final String MODID = "provfootball";
    public static final Logger LOGGER = LogUtils.getLogger();


    public ProvFootball(IEventBus modEventBus, ModContainer modContainer) {
        ModItems.ITEMS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);

        //    ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);


        modEventBus.addListener(this::addCreative);

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
