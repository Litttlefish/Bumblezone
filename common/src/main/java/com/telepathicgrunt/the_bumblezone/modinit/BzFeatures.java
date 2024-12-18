package com.telepathicgrunt.the_bumblezone.modinit;

import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.worldgen.features.BeeDungeon;
import com.telepathicgrunt.the_bumblezone.worldgen.features.BeehiveBeewaxBoundaries;
import com.telepathicgrunt.the_bumblezone.worldgen.features.BlockEntityCombOre;
import com.telepathicgrunt.the_bumblezone.worldgen.features.CaveSugarWaterfall;
import com.telepathicgrunt.the_bumblezone.worldgen.features.FloralFillWithRootmin;
import com.telepathicgrunt.the_bumblezone.worldgen.features.GiantHoneyCrystalFeature;
import com.telepathicgrunt.the_bumblezone.worldgen.features.HangingGardenMob;
import com.telepathicgrunt.the_bumblezone.worldgen.features.HoneyCrystalFeature;
import com.telepathicgrunt.the_bumblezone.worldgen.features.HoneycombCaves;
import com.telepathicgrunt.the_bumblezone.worldgen.features.HoneycombHole;
import com.telepathicgrunt.the_bumblezone.worldgen.features.ItemFrameWithRandomItem;
import com.telepathicgrunt.the_bumblezone.worldgen.features.LayeredBlockSurface;
import com.telepathicgrunt.the_bumblezone.worldgen.features.NbtFeature;
import com.telepathicgrunt.the_bumblezone.worldgen.features.PollinatedCaves;
import com.telepathicgrunt.the_bumblezone.worldgen.features.SpiderInfestedBeeDungeon;
import com.telepathicgrunt.the_bumblezone.worldgen.features.StickyHoneyResidueFeature;
import com.telepathicgrunt.the_bumblezone.worldgen.features.TreeDungeon;
import com.telepathicgrunt.the_bumblezone.worldgen.features.TwoToneSpikeFeature;
import com.telepathicgrunt.the_bumblezone.worldgen.features.WebWall;
import com.telepathicgrunt.the_bumblezone.worldgen.features.configs.BiomeBasedLayerConfig;
import com.telepathicgrunt.the_bumblezone.worldgen.features.configs.FloralFillWithRootminConfig;
import com.telepathicgrunt.the_bumblezone.worldgen.features.configs.HoneyCrystalFeatureConfig;
import com.telepathicgrunt.the_bumblezone.worldgen.features.configs.ItemFrameConfig;
import com.telepathicgrunt.the_bumblezone.worldgen.features.configs.NbtFeatureConfig;
import com.telepathicgrunt.the_bumblezone.worldgen.features.configs.NbtOreConfiguration;
import com.telepathicgrunt.the_bumblezone.worldgen.features.configs.TreeDungeonFeatureConfig;
import com.telepathicgrunt.the_bumblezone.worldgen.features.configs.TwoToneSpikeFeatureConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class BzFeatures {
    public static final ResourcefulRegistry<Feature<?>> FEATURES = ResourcefulRegistries.create(BuiltInRegistries.FEATURE, Bumblezone.MODID);

    public static final RegistryEntry<Feature<NbtFeatureConfig>> HONEYCOMB_HOLE = FEATURES.register("honeycomb_holes", () -> new HoneycombHole(NbtFeatureConfig.CODEC));
    public static final RegistryEntry<Feature<NoneFeatureConfiguration>> HONEYCOMB_CAVES = FEATURES.register("honeycomb_caves", () -> new HoneycombCaves(NoneFeatureConfiguration.CODEC));
    public static final RegistryEntry<Feature<NoneFeatureConfiguration>> POLLINATED_CAVES = FEATURES.register("pollinated_caves", () -> new PollinatedCaves(NoneFeatureConfiguration.CODEC));
    public static final RegistryEntry<Feature<NoneFeatureConfiguration>> CAVE_SUGAR_WATERFALL = FEATURES.register("cave_sugar_waterfall", () -> new CaveSugarWaterfall(NoneFeatureConfiguration.CODEC));
    public static final RegistryEntry<Feature<HoneyCrystalFeatureConfig>> HONEY_CRYSTAL_FEATURE = FEATURES.register("honey_crystals_feature", () -> new HoneyCrystalFeature(HoneyCrystalFeatureConfig.CODEC));
    public static final RegistryEntry<Feature<NoneFeatureConfiguration>> GIANT_HONEY_CRYSTAL_FEATURE = FEATURES.register("giant_honey_crystal_feature", () -> new GiantHoneyCrystalFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryEntry<Feature<NoneFeatureConfiguration>> STICKY_HONEY_RESIDUE_FEATURE = FEATURES.register("sticky_honey_residue_feature", () -> new StickyHoneyResidueFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryEntry<Feature<NbtFeatureConfig>> NBT_FEATURE = FEATURES.register("nbt_feature", () -> new NbtFeature<>(NbtFeatureConfig.CODEC));
    public static final RegistryEntry<Feature<NbtFeatureConfig>> BEE_DUNGEON = FEATURES.register("bee_dungeon", () -> new BeeDungeon(NbtFeatureConfig.CODEC));
    public static final RegistryEntry<Feature<NbtFeatureConfig>> SPIDER_INFESTED_BEE_DUNGEON = FEATURES.register("spider_infested_bee_dungeon", () -> new SpiderInfestedBeeDungeon(NbtFeatureConfig.CODEC));
    public static final RegistryEntry<Feature<TreeDungeonFeatureConfig>> TREE_DUNGEON = FEATURES.register("tree_dungeon", () -> new TreeDungeon(TreeDungeonFeatureConfig.CODEC));
    public static final RegistryEntry<Feature<BiomeBasedLayerConfig>> LAYERED_BLOCK_SURFACE = FEATURES.register("layered_block_surface", () -> new LayeredBlockSurface(BiomeBasedLayerConfig.CODEC));
    public static final RegistryEntry<Feature<NoneFeatureConfiguration>> WEB_WALL = FEATURES.register("web_wall", () -> new WebWall(NoneFeatureConfiguration.CODEC));
    public static final RegistryEntry<Feature<NoneFeatureConfiguration>> HANGING_GARDEN_MOB = FEATURES.register("hanging_garden_mob", () -> new HangingGardenMob(NoneFeatureConfiguration.CODEC));
    public static final RegistryEntry<Feature<ItemFrameConfig>> ITEM_FRAME_WITH_RANDOM_ITEM = FEATURES.register("item_frame_with_random_item", () -> new ItemFrameWithRandomItem(ItemFrameConfig.CODEC));
    public static final RegistryEntry<Feature<FloralFillWithRootminConfig>> FLORAL_FILL_WITH_ROOTMIN = FEATURES.register("floral_fill_with_rootmin", () -> new FloralFillWithRootmin(FloralFillWithRootminConfig.CODEC));
    public static final RegistryEntry<Feature<TwoToneSpikeFeatureConfig>> TWO_TONE_SPIKE_FEATURE = FEATURES.register("two_tone_spike_feature", () -> new TwoToneSpikeFeature(TwoToneSpikeFeatureConfig.CODEC));
    public static final RegistryEntry<Feature<NoneFeatureConfiguration>> BEEHIVE_BEESWAX_BOUNDARIES = FEATURES.register("beehive_beeswax_boundaries", () -> new BeehiveBeewaxBoundaries(NoneFeatureConfiguration.CODEC));

    public static final RegistryEntry<Feature<NbtOreConfiguration>> BLOCKENTITY_COMBS_FEATURE = FEATURES.register("blockentity_combs", () -> new BlockEntityCombOre(NbtOreConfiguration.CODEC));
}
