package com.p1emc.provfootball.entity;

import com.p1emc.provfootball.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FootballEntity extends Entity {

    public FootballEntity(EntityType<? extends FootballEntity> type, Level level) {
        super(type, level);
    }

    public FootballEntity(Level level, double x, double y, double z) {
        this(ModEntities.FOOTBALL.get(), level);
        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }


    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }


    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }


    //ball pickup
    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {

        if (this.level().isClientSide()) {

            return player.getItemInHand(hand).isEmpty()
                    ? InteractionResult.SUCCESS
                    : InteractionResult.PASS;
        }


        if (!player.getItemInHand(hand).isEmpty()) {
            return InteractionResult.PASS;
        }


        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.EXPLOSION,
                    this.getX(), this.getY() + 0.25D, this.getZ(),
                    3, 0.15D, 0.15D, 0.15D, 0.0D);
        }

        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.6F, 1.4F);

        ItemStack stack = new ItemStack(ModItems.FOOTBALL.get());

        if (!player.addItem(stack)) {
            player.drop(stack, false);
        }


        player.getCooldowns().addCooldown(ModItems.FOOTBALL.get(), 60);


        this.discard();

        return InteractionResult.CONSUME;
    }


}