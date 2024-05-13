package com.telepathicgrunt.the_bumblezone.worldgen.features.configs;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.List;

public class NbtFeatureConfig implements FeatureConfiguration {
    public static final MapCodec<NbtFeatureConfig> CODEC = RecordCodecBuilder.mapCodec((configInstance) -> configInstance.group(
            ResourceLocation.CODEC.fieldOf("processors").forGetter(nbtFeatureConfig -> nbtFeatureConfig.processor),
            ResourceLocation.CODEC.fieldOf("post_processors").orElse(new ResourceLocation("minecraft:empty")).forGetter(nbtFeatureConfig -> nbtFeatureConfig.postProcessor),
            Codec.mapPair(ResourceLocation.CODEC.fieldOf("resourcelocation"), ExtraCodecs.POSITIVE_INT.fieldOf("weight")).codec().listOf().fieldOf("nbt_entries").forGetter(nbtFeatureConfig -> nbtFeatureConfig.nbtResourcelocationsAndWeights),
            Codec.INT.fieldOf("structure_y_offset").orElse(0).forGetter(nbtFeatureConfig -> nbtFeatureConfig.structureYOffset)
    ).apply(configInstance, NbtFeatureConfig::new));

    public final List<Pair<ResourceLocation, Integer>> nbtResourcelocationsAndWeights;
    public final ResourceLocation processor;
    public final ResourceLocation postProcessor;
    public final int structureYOffset;

    public NbtFeatureConfig(ResourceLocation processor,
                            ResourceLocation postProcessor,
                            List<Pair<ResourceLocation, Integer>> nbtIdentifiersAndWeights,
                            int structureYOffset)
    {
        this.nbtResourcelocationsAndWeights = nbtIdentifiersAndWeights;
        this.processor = processor;
        this.postProcessor = postProcessor;
        this.structureYOffset = structureYOffset;
    }
}
