package com.telepathicgrunt.the_bumblezone.worldgen.features.configs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class HoneyCrystalFeatureConfig implements FeatureConfiguration {
    public static final MapCodec<HoneyCrystalFeatureConfig> CODEC = RecordCodecBuilder.mapCodec((configInstance) -> configInstance.group(
            Codec.BOOL.fieldOf("exposed").forGetter(nbtFeatureConfig -> nbtFeatureConfig.exposed)
    ).apply(configInstance, HoneyCrystalFeatureConfig::new));

    public final boolean exposed;

    public HoneyCrystalFeatureConfig(boolean exposed)
    {
        this.exposed = exposed;
    }
}
