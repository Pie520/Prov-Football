package com.p1emc.provfootball.item;

import com.p1emc.provfootball.ProvFootball;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ProvFootball.MODID);

    public static final Supplier<CreativeModeTab> FOOTBALL_TAB =
            CREATIVE_MODE_TABS.register("football", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.provfootball"))
                    .icon(() -> new ItemStack(ModItems.FOOTBALL.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.FOOTBALL.get());
                    })
                    .build());
}