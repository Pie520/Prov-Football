package com.p1emc.provfootball.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.p1emc.provfootball.ProvFootball;
import com.p1emc.provfootball.config.ConfigCache;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = ProvFootball.MODID)
public class ProvFootballCommandReload {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
                Commands.literal("football")
                        // Level 2 is the usual bar for anything that changes
                        // how the world behaves. Ops and command blocks pass.
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("reload")
                                .executes(ProvFootballCommandReload::reload)));
    }

    private static int reload(CommandContext<CommandSourceStack> context) {
        ConfigCache.refresh();

        // sendSuccess takes a Supplier so the component is only built if
        // anyone is actually going to see it. The boolean is whether to also
        // tell other ops -- false, since this is not worth broadcasting.
        context.getSource().sendSuccess(
                () -> Component.literal("Football config reloaded."), false);



        // Brigadier's return value is a "result count". 1 means success and
        // feeds comparator output on a command block; 0 counts as failure.
        return 1;
    }
}