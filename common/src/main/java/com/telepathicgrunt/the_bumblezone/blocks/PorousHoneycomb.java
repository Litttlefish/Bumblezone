package com.telepathicgrunt.the_bumblezone.blocks;

import com.mojang.serialization.MapCodec;
import com.telepathicgrunt.the_bumblezone.mixin.entities.BeeEntityInvoker;
import com.telepathicgrunt.the_bumblezone.modinit.BzBlocks;
import com.telepathicgrunt.the_bumblezone.modinit.BzTags;
import com.telepathicgrunt.the_bumblezone.utils.GeneralUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class PorousHoneycomb extends Block {

    public static final MapCodec<PorousHoneycomb> CODEC = Block.simpleCodec(PorousHoneycomb::new);

    public PorousHoneycomb() {
        this(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_ORANGE)
                .instrument(NoteBlockInstrument.BANJO)
                .strength(0.5F, 0.5F)
                .sound(SoundType.CORAL_BLOCK));
    }

    public PorousHoneycomb(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<? extends PorousHoneycomb> codec() {
        return CODEC;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos blockPos, Entity entity) {
        beeHoneyFill(state, level, blockPos, entity);
    }

    @Override
    public void stepOn(Level level, BlockPos blockPos, BlockState state, Entity entity) {
        beeHoneyFill(state, level, blockPos, entity);
    }

    public static void beeHoneyFill(BlockState state, Level level, BlockPos blockPos, Entity entity) {
        if(entity instanceof Bee beeEntity &&
            beeEntity.hasNectar() &&
            state.is(BzBlocks.POROUS_HONEYCOMB.get()))
        {
            ((BeeEntityInvoker) entity).callSetHasNectar(false);
            level.setBlock(blockPos, BzBlocks.FILLED_POROUS_HONEYCOMB.get().defaultBlockState(), 3);

            Vec3 centerOfBee = beeEntity.getBoundingBox().getCenter();
            PileOfPollen.spawnParticlesServer(
                    level,
                    centerOfBee,
                    beeEntity.getRandom(),
                    0.05D,
                    0.05D,
                    -0.001D,
                    55);
        }
    }

    /**
     * Allow player to harvest honey and put honey into this block using bottles
     */
    @Override
    public ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos position, Player playerEntity, InteractionHand playerHand, BlockHitResult raytraceResult) {
        /*
         * Player is adding honey to this block if it is not filled with honey
         */
        if (itemStack.getItem() == Items.HONEY_BOTTLE) {
            level.setBlock(position, BzBlocks.FILLED_POROUS_HONEYCOMB.get().defaultBlockState(), 3); // added honey to this block
            level.playSound(playerEntity, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.BOTTLE_EMPTY, SoundSource.PLAYERS, 1.0F, 1.0F);

            GeneralUtils.givePlayerItem(playerEntity, playerHand, ItemStack.EMPTY, true, true);

            return ItemInteractionResult.SUCCESS;
        }

        else if (itemStack.is(BzTags.HONEY_BUCKETS)) {
            // added honey to this block and neighboring blocks
            level.setBlock(position, BzBlocks.FILLED_POROUS_HONEYCOMB.get().defaultBlockState(), 3);

            // Clientside shuffle wont match server so let server fill neighbors and autosync to client.
            if(!level.isClientSide()) {
                int filledNeighbors = 0;
                List<Direction> shuffledDirections = Arrays.asList(Direction.values());
                Collections.shuffle(shuffledDirections);
                for(Direction direction : shuffledDirections) {
                    BlockState sideState = level.getBlockState(position.relative(direction));
                    if(sideState.is(BzBlocks.POROUS_HONEYCOMB.get())) {
                        level.setBlock(position.relative(direction), BzBlocks.FILLED_POROUS_HONEYCOMB.get().defaultBlockState(), 3);
                        filledNeighbors++;
                    }

                    if(filledNeighbors == 2) break;
                }
            }

            level.playSound(playerEntity, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.BUCKET_EMPTY, SoundSource.PLAYERS, 1.0F, 1.0F);

            GeneralUtils.givePlayerItem(playerEntity, playerHand, ItemStack.EMPTY, true, true);

            return ItemInteractionResult.SUCCESS;
        }

        return super.useItemOn(itemStack, blockState, level, position, playerEntity, playerHand, raytraceResult);
    }
}
