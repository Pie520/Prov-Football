package com.p1emc.provfootball.item;

import com.p1emc.provfootball.ProvFootball;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(ProvFootball.MODID);

    public static final DeferredItem<Item> FOOTBALL =
            ITEMS.register("football", () -> new FootballItem(new Item.Properties().stacksTo(1)));
}

