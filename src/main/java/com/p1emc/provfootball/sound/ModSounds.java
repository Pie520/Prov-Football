package com.p1emc.provfootball.sound;

import com.p1emc.provfootball.ProvFootball;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, ProvFootball.MODID);

    public static final Supplier<SoundEvent> KICK_BALL = SOUNDS.register("kick_ball",
            () -> SoundEvent.createVariableRangeEvent(ProvFootball.id("kick_ball")));
}