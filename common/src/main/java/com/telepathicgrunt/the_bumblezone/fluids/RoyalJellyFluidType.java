package com.telepathicgrunt.the_bumblezone.fluids;

import com.telepathicgrunt.the_bumblezone.fluids.base.FluidProperties;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.pathfinder.PathType;

public class RoyalJellyFluidType {

    public static FluidProperties.Builder create() {
        return new FluidProperties.Builder("royal_jelly")
                .supportsBoating(true)
                .canHydrate(false)
                .canDrown(true)
                .canExtinguish(true)
                .canPushEntity(true)
                .canSwim(true)
                .pathType(PathType.WATER)
                .adjacentPathType(PathType.WATER_BORDER)
                .canConvertToSource(false)
                .fallDistanceModifier(0.15f)
                .motionScale(0.0115)
                .rarity(Rarity.UNCOMMON)
                .viscosity(5000)
                .density(2000)
                .temperature(300)
                .tickDelay(30)
                .sound("bucket_fill", () -> SoundEvents.BUCKET_FILL)
                .sound("bucket_empty", () -> SoundEvents.BUCKET_EMPTY)
                .sound("fluid_vaporize", () -> SoundEvents.FIRE_EXTINGUISH);
    }
}
