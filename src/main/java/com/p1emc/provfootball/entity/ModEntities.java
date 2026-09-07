package com.p1emc.provfootball.entity;

import com.p1emc.provfootball.ProvFootball;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {


    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, ProvFootball.MODID);

    public static final Supplier<EntityType<FootballEntity>> FOOTBALL =
            ENTITY_TYPES.register("football", () -> EntityType.Builder

                    .<FootballEntity>of(FootballEntity::new, MobCategory.MISC)

                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1)


                    .build("football"));
}