package com.telepathicgrunt.the_bumblezone.worldgen.features;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.the_bumblezone.configs.BzWorldgenConfigs;
import com.telepathicgrunt.the_bumblezone.modinit.BzBlocks;
import com.telepathicgrunt.the_bumblezone.worldgen.features.configs.NbtFeatureConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;


public class BeeDungeon extends NbtFeature<NbtFeatureConfig> {

    public BeeDungeon(Codec<NbtFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean place(FeaturePlaceContext<NbtFeatureConfig> context) {
        //affect rarity
        if (BzWorldgenConfigs.beeDungeonRarity >= 1000 ||
            context.random().nextInt(BzWorldgenConfigs.beeDungeonRarity) != 0) {
            return false;
        }

        if (isValidDungeonSpot(context)) {
            // generate dungeon
            super.place(context);
        }

        return true;
    }

    protected static boolean isValidDungeonSpot(FeaturePlaceContext<?> context) {
        if (!context.level().getBlockState(context.origin()).canOcclude()) {
            return false;
        }

        boolean validSpot = false;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        mutable.set(context.origin());

        //find a cave air spot
        for (Direction face : Direction.Plane.HORIZONTAL) {
            mutable.set(context.origin()).move(face, 3);

            BlockState state = context.level().getBlockState(mutable);
            if (state.is(Blocks.CAVE_AIR) || state.is(BzBlocks.PILE_OF_POLLEN.get())) {
                validSpot = true;
                break;
            }
        }

        //make sure we aren't too close to regular air
        for (int xOffset = -6; xOffset <= 6; xOffset += 6) {
            for (int zOffset = -6; zOffset <= 6; zOffset += 6) {
                for (int yOffset = -3; yOffset <= 9; yOffset += 3) {
                    mutable.set(context.origin()).move(xOffset, yOffset, zOffset);

                    if (context.level().getBlockState(mutable).is(Blocks.AIR))
                        validSpot = false;
                }
            }
        }

        return validSpot;
    }
}
