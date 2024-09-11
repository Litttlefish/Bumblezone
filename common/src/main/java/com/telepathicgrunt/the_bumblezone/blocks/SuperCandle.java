package com.telepathicgrunt.the_bumblezone.blocks;

import com.telepathicgrunt.the_bumblezone.modinit.BzBlocks;
import com.telepathicgrunt.the_bumblezone.modinit.BzCriterias;
import com.telepathicgrunt.the_bumblezone.modinit.BzTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkAccess;


public interface SuperCandle {
    static boolean canBeLit(Level level, BlockState state, BlockPos pos) {
        BlockState aboveState = level.getBlockState(pos.above());
        return aboveState.is(BzTags.CANDLE_WICKS) &&
                !state.getValue(BlockStateProperties.WATERLOGGED) &&
                !state.getValue(BlockStateProperties.LIT) &&
                !aboveState.getValue(BlockStateProperties.WATERLOGGED) &&
                !aboveState.getValue(BlockStateProperties.LIT);
    }

    static void placeWickIfPossible(LevelAccessor levelAccessor, BlockPos blockPos, boolean lit) {
        ChunkAccess chunkAccess = levelAccessor.getChunk(blockPos);
        BlockPos wickPosition = SuperCandleWick.getLitWickPositionAbove(levelAccessor, blockPos);
        BlockState aboveState = chunkAccess.getBlockState(blockPos.above());

        boolean wickSpotAvaliableAbove = false;
        if (wickPosition == null) {
            if (!aboveState.is(BzTags.CANDLE_WICKS) && (aboveState.canBeReplaced() || aboveState.isAir())) {
                wickPosition = blockPos.above();
                wickSpotAvaliableAbove = true;
            }
            else {
                return;
            }
        }

        BlockState wickState = chunkAccess.getBlockState(wickPosition);
        boolean isBelowSoul = SuperCandleWick.isSoulBelowInRange(levelAccessor, blockPos.below());
        boolean wickWaterlogged = wickState.getFluidState().is(FluidTags.WATER) && wickState.getFluidState().isSource();

        if ((wickState.is(BzBlocks.SUPER_CANDLE_WICK.get()) && isBelowSoul) ||
            (wickState.is(BzBlocks.SUPER_CANDLE_WICK_SOUL.get()) && !isBelowSoul) ||
            wickSpotAvaliableAbove)
        {
            if (wickState.is(BzTags.CANDLE_WICKS)) {
                lit = wickState.getValue(BlockStateProperties.LIT);
            }
            Block wickBlock = (isBelowSoul && lit) ? BzBlocks.SUPER_CANDLE_WICK_SOUL.get() : BzBlocks.SUPER_CANDLE_WICK.get();
            BlockState candleWick = wickBlock.defaultBlockState()
                    .setValue(BlockStateProperties.LIT, lit)
                    .setValue(BlockStateProperties.WATERLOGGED, wickWaterlogged);
            levelAccessor.setBlock(wickPosition, candleWick, 3);

            if (wickWaterlogged) {
                BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
                mutableBlockPos.set(wickPosition.below());
                for (int i = 0; i < chunkAccess.getMaxBuildHeight() - mutableBlockPos.getY(); i++) {
                    BlockState currentState = chunkAccess.getBlockState(mutableBlockPos);
                    if (currentState.getBlock() instanceof SuperCandle) {
                        levelAccessor.setBlock(blockPos, currentState.setValue(BlockStateProperties.WATERLOGGED, true), 3);
                    }
                    else {
                        break;
                    }
                    mutableBlockPos.move(Direction.DOWN);
                }
            }
        }
    }

    default boolean CandleUnlightBehaviors(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, boolean simulate) {
        if (itemStack.isEmpty() && blockState.getValue(BlockStateProperties.LIT)) {
            if (!simulate) {
                SuperCandleWick.extinguish(player, level.getBlockState(blockPos.above()), level, blockPos.above());
            }
            return true;
        }
        return false;
    }

    default boolean CandleLightBehaviors(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand playerHand, boolean simulate) {
        if (!blockState.getValue(BlockStateProperties.LIT)) {
            if (itemStack.is(BzTags.INFINITE_CANDLE_LIGHTING_ITEMS)) {
                if (!simulate) {
                    if (lightCandle(level, blockPos, player)) {
                        if (!itemStack.isEmpty()) {
                            player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
                        }
                    }
                }
                return true;
            }
            else if (itemStack.is(BzTags.DAMAGEABLE_CANDLE_LIGHTING_ITEMS)) {
                if (!simulate) {
                    boolean successfulLit = lightCandle(level, blockPos, player);
                    if (!itemStack.isEmpty() && successfulLit) {
                        player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
                    }
                    if (successfulLit && player instanceof ServerPlayer serverPlayer && !player.getAbilities().instabuild) {
                        itemStack.hurtAndBreak(1, serverPlayer, LivingEntity.getSlotForHand(playerHand));
                    }
                }
                return true;
            }
            else if (itemStack.is(BzTags.CONSUMABLE_CANDLE_LIGHTING_ITEMS)) {
                if (!simulate) {
                    boolean successfulLit = lightCandle(level, blockPos, player);
                    if (!itemStack.isEmpty() && successfulLit) {
                        player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
                    }
                    if (successfulLit && !player.getAbilities().instabuild) {
                        itemStack.shrink(1);
                    }
                }
                return true;
            }
        }
        return false;
    }

    default boolean lightCandle(Level level, BlockPos blockPos, Player player) {
        boolean litWick = SuperCandleWick.setLit(level, level.getBlockState(blockPos.above()), blockPos.above(), true);

        if (litWick &&
                player instanceof ServerPlayer serverPlayer &&
                level.getBlockState(blockPos.above()).getBlock() instanceof SuperCandleWick candleWick &&
                candleWick.isSoul())
        {
            BzCriterias.LIGHT_SOUL_SUPER_CANDLE_TRIGGER.get().trigger(serverPlayer);
        }

        return litWick;
    }
}
