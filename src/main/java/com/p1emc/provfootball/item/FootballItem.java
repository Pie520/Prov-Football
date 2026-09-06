package com.p1emc.provfootball.item;

import com.p1emc.provfootball.entity.FootballEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FootballItem extends Item {


    public FootballItem(Properties properties) {
        super(properties);
    }


    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();


        if (!(level instanceof ServerLevel serverLevel)) {

            return InteractionResult.SUCCESS;
        }


        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());


        FootballEntity ball = new FootballEntity(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
        serverLevel.addFreshEntity(ball);

        Player player = context.getPlayer();
        if (player != null) {

            context.getItemInHand().consume(1, player);
        }


        return InteractionResult.CONSUME;
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level instanceof ServerLevel serverLevel) {

            Vec3 look = player.getLookAngle();


            Vec3 spawn = player.getEyePosition().add(look.x, -0.45D, look.z);

            FootballEntity ball = new FootballEntity(level, spawn.x, spawn.y, spawn.z);


            ball.setDeltaMovement(look.x * 0.3D, 0.1D, look.z * 0.3D);

            serverLevel.addFreshEntity(ball);
            stack.consume(1, player);
        }


        player.awardStat(Stats.ITEM_USED.get(this));

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}