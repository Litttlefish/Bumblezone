package com.telepathicgrunt.the_bumblezone.configs.fabric;

import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.configs.BzBeeAggressionConfigs;
import com.telepathicgrunt.the_bumblezone.configs.BzClientConfigs;
import com.telepathicgrunt.the_bumblezone.configs.BzDimensionConfigs;
import com.telepathicgrunt.the_bumblezone.configs.BzGeneralConfigs;
import com.telepathicgrunt.the_bumblezone.configs.BzModCompatibilityConfigs;
import com.telepathicgrunt.the_bumblezone.configs.BzWorldgenConfigs;
import com.telepathicgrunt.the_bumblezone.entities.mobs.BeehemothEntity;
import eu.midnightdust.lib.config.MidnightConfig;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("CanBeFinal")
public class BzConfig extends MidnightConfig {

    @Comment
    public static Comment beeAggressionComment;

    @Entry
    public static boolean beehemothTriggersWrath = false;

    @Entry
    public static boolean allowWrathOfTheHiveOutsideBumblezone = false;

    @Entry
    public static boolean showWrathOfTheHiveParticles = true;

    @Entry
    public static boolean aggressiveBees = true;

    @Entry(min=1, max=10000)
    public static int aggressionTriggerRadius = 64;

    @Entry(min=1, max=10000)
    public static int howLongWrathOfTheHiveLasts = 1680;

    @Entry(min=1, max=10000)
    public static int howLongProtectionOfTheHiveLasts = 6000;

    @Entry(min=1, max=100)
    public static int speedBoostLevel = 2;

    @Entry(min=1, max=100)
    public static int absorptionBoostLevel = 1;

    @Entry(min=1, max=100)
    public static int strengthBoostLevel = 1;


    @Comment
    public static Comment blockMechanicsComment;

    @Entry
    public static boolean dispensersDropGlassBottles = false;

    @Entry(min=1, max=1000)
    public static int broodBlocksBeeSpawnCapacity = 40;

    @Entry
    public static boolean pileOfPollenHyperFireSpread = false;

    @Entry
    public static boolean superCandlesBurnsMobs = true;


    @Comment
    public static Comment enchantmentsComment;

    @Entry(min=1, max=255)
    public static int neurotoxinMaxLevel = 2;

    @Entry(min=1, max=1000000)
    public static int paralyzedMaxTickDuration = 600;


    @Comment
    public static Comment crystallineFlowerComment;

    @Entry
    public static boolean crystallineFlowerConsumeItemEntities = true;

    @Entry
    public static boolean crystallineFlowerConsumeExperienceOrbEntities = true;

    @Entry
    public static boolean crystallineFlowerConsumeExperienceUI = true;

    @Entry
    public static boolean crystallineFlowerConsumeItemUI = true;

    @Entry
    public static int crystallineFlowerEnchantingPowerAllowedPerTier = 8;

    @Entry
    public static int crystallineFlowerExtraTierCost = 0;

    @Entry
    public static int crystallineFlowerExtraXpNeededForTiers = 0;


    @Comment
    public static Comment essenceComment;

    @Entry
    public static boolean repeatableEssenceEvents = true;

    @Entry
    public static int cosmicCrystalHealth = 60;

    @Entry
    public static int ragingEssenceAbilityUse = 28;

    @Entry
    public static int ragingEssenceCooldown = 36000;

    @Entry
    public static int[] ragingEssenceStrengthLevels = new int[] { 1, 2, 3, 5, 8, 14, 20 };

    @Entry
    public static int knowingEssenceAbilityUse = 1200;

    @Entry
    public static int knowingEssenceCooldown = 18000;

    @Entry
    public static boolean knowingEssenceStructureNameServer = true;

    @Entry
    public static int calmingEssenceAbilityUse = 600;

    @Entry
    public static int calmingEssenceCooldown = 12000;

    @Entry
    public static int lifeEssenceAbilityUse = 1000;

    @Entry
    public static int lifeEssenceCooldown = 12000;

    @Entry
    public static int radianceEssenceAbilityUse = 4800;

    @Entry
    public static int radianceEssenceCooldown = 12000;

    @Entry
    public static int continuityEssenceCooldown = 48000;


    @Comment
    public static Comment dimensionComment;

    @Entry
    public static boolean enableInitialWelcomeMessage = true;

    @Entry(min=0, max=100000)
    public static double fogBrightnessPercentage = 110;

    @Entry(min=0, max=100)
    public static double fogThickness = 4;

    @Entry
    public static boolean enableDimensionFog = true;

    @Entry
    public static boolean onlyOverworldHivesTeleports = false;

    @Entry
    public static boolean forceExitToOverworld = false;

    @Entry
    public static boolean warnPlayersOfWrongBlockUnderHive = true;

    @Entry
    public static boolean enableExitTeleportation = true;

    @Entry
    public static boolean enableEntranceTeleportation = true;

    @Entry
    public static boolean forceBumblezoneOriginMobToOverworldCenter = true;

    @Entry
    public static String defaultDimension = "minecraft:overworld";


    @Comment
    public static Comment dungeonsComment;

    @Entry(min=0, max=1001)
    public static int beeDungeonRarity = 1;

    @Entry(min=0, max=1001)
    public static int treeDungeonRarity = 2;

    @Entry(min=0, max=1001)
    public static int spiderInfestedBeeDungeonRarity = 5;

    @Entry(min=0, max=1)
    public static float spawnerRateSpiderBeeDungeon = 0.2f;


    @Comment
    public static Comment brewingRecipeComment;

    @Entry
    public static boolean glisteringHoneyBrewingRecipe = true;

    @Entry
    public static boolean beeStingerBrewingRecipe = true;

    @Entry
    public static boolean beeSoupBrewingRecipe = true;


    @Comment
    public static Comment generalComment;

    @Entry
    public static List<String> variantBeeTypes = Arrays.asList(
            "redtail_bee",
            "green_bee",
            "blue_bee",
            "white_bee",
            "ukraine_bee",
            "trans_bee",
            "asexual_bee",
            "agender_bee",
            "aroace_bee",
            "aromantic_bee",
            "bisexual_bee",
            "pan_bee",
            "enby_bee",
            "reverse_bee",
            "neapolitan_bee",
            "rainbow_bee");


    @Entry(min=0, max=100)
    public static double beehemothSpeed = 0.95;

    @Entry
    public static boolean beehemothFriendlyFire = true;

    @Entry(min=0, max=256)
    public static int beeQueenBonusTradeRewardMultiplier = 3;

    @Entry(min=0, max=2000000)
    public static int beeQueenBonusTradeDurationInTicks = 24000;

    @Entry(min=0, max=1000000)
    public static int beeQueenBonusTradeAmountTillSatified = 24;

    @Entry
    public static boolean beeQueenSpecialDayTrades = true;

    @Entry
    public static boolean beeQueenRespawning = true;

    @Entry
    public static boolean specialBeeSpawning = true;

    @Entry
    public static boolean beeLootInjection = true;

    @Entry
    public static boolean moddedBeeLootInjection = true;

    @Entry
    public static int nearbyBeesPerPlayerInBz = 25;


    @Comment
    public static Comment musicDiscComment;

    @Entry
    public static boolean allowWanderingTraderMusicDiscsTrades = true;


    @Comment
    public static Comment modCompatComment;

    @Entry
    public static String alternativeFluidToReplaceHoneyFluid = "";

    @Entry
    public static boolean allowFriendsAndFoesBeekeeperTradesCompat = true;

    @Entry
    public static boolean allowGoodallBottledBeesRevivingEmptyBroodBlock = true;

    @Entry
    public static boolean allowLootrCompat = true;


    @Comment
    public static Comment clientComment;

    @Entry
    public static boolean useBackupModelForVariantBee = false;

    @Entry
    public static boolean renderBeeQueenBonusTradeItem = true;

    @Entry
    public static boolean playWrathOfHiveEffectMusic = true;

    @Entry
    public static boolean playSempiternalSanctumMusic = true;

    @Entry
    public static boolean disableEssenceBlockShaders = false;


    @Comment
    public static Comment knowingEssenceComment;

    @Entry
    public static boolean knowingEssenceHighlightBosses = true;

    @Entry
    public static boolean knowingEssenceHighlightMonsters = true;

    @Entry
    public static boolean knowingEssenceHighlightTamed = true;

    @Entry
    public static boolean knowingEssenceHighlightLivingEntities = true;

    @Entry
    public static boolean knowingEssenceHighlightCommonItems = true;

    @Entry
    public static boolean knowingEssenceHighlightUncommonItems = true;

    @Entry
    public static boolean knowingEssenceHighlightRareItems = true;

    @Entry
    public static boolean knowingEssenceHighlightEpicItems = true;

    @Entry
    public static boolean knowingEssenceStructureNameClient = true;

    @Entry
    public static int knowingEssenceStructureNameXCoord = 4;

    @Entry
    public static int knowingEssenceStructureNameYCoord = 16;


    @Comment
    public static Comment radianceEssenceComment;

    @Entry
    public static boolean radianceEssenceArmorDurability = true;

    @Entry
    public static int radianceEssenceArmorDurabilityXCoord = 4;

    @Entry
    public static int radianceEssenceArmorDurabilityYCoord = 16;


    @Comment
    public static Comment essenceItemsClientComment;

    @Entry
    public static int essenceItemHUDVisualEffectLayers = 3;

    @Entry
    public static float essenceItemHUDVisualEffectSpeed = 1;


    @ApiStatus.Internal
    public static void setup() {
        MidnightConfig.init(Bumblezone.MODID, BzConfig.class);
        copyConfigsToCommon();
    }

    /**
     * This is used to have a 'common' config in the common project but custom configs on both sides.
     */
    @ApiStatus.Internal
    public static void copyConfigsToCommon() {

        //Aggression
        BzBeeAggressionConfigs.aggressiveBees = aggressiveBees;
        BzBeeAggressionConfigs.aggressionTriggerRadius = aggressionTriggerRadius;
        BzBeeAggressionConfigs.howLongWrathOfTheHiveLasts = howLongWrathOfTheHiveLasts;
        BzBeeAggressionConfigs.howLongProtectionOfTheHiveLasts = howLongProtectionOfTheHiveLasts;
        BzBeeAggressionConfigs.speedBoostLevel = speedBoostLevel;
        BzBeeAggressionConfigs.absorptionBoostLevel = absorptionBoostLevel;
        BzBeeAggressionConfigs.strengthBoostLevel = strengthBoostLevel;
        BzBeeAggressionConfigs.beehemothTriggersWrath = beehemothTriggersWrath;
        BzBeeAggressionConfigs.allowWrathOfTheHiveOutsideBumblezone = allowWrathOfTheHiveOutsideBumblezone;
        BzBeeAggressionConfigs.showWrathOfTheHiveParticles = showWrathOfTheHiveParticles;

        //Block Mechanics
        BzGeneralConfigs.dispensersDropGlassBottles = dispensersDropGlassBottles;
        BzGeneralConfigs.broodBlocksBeeSpawnCapacity = broodBlocksBeeSpawnCapacity;
        BzGeneralConfigs.pileOfPollenHyperFireSpread = pileOfPollenHyperFireSpread;
        BzGeneralConfigs.superCandlesBurnsMobs = superCandlesBurnsMobs;

        //Enchantment Mechanics
        BzGeneralConfigs.neurotoxinMaxLevel = neurotoxinMaxLevel;
        BzGeneralConfigs.paralyzedMaxTickDuration = paralyzedMaxTickDuration;

        //Crystalline Flower
        BzGeneralConfigs.crystallineFlowerConsumeItemEntities = crystallineFlowerConsumeItemEntities;
        BzGeneralConfigs.crystallineFlowerConsumeExperienceOrbEntities = crystallineFlowerConsumeExperienceOrbEntities;
        BzGeneralConfigs.crystallineFlowerConsumeExperienceUI = crystallineFlowerConsumeExperienceUI;
        BzGeneralConfigs.crystallineFlowerConsumeItemUI = crystallineFlowerConsumeItemUI;
        BzGeneralConfigs.crystallineFlowerEnchantingPowerAllowedPerTier = crystallineFlowerEnchantingPowerAllowedPerTier;
        BzGeneralConfigs.crystallineFlowerExtraTierCost = crystallineFlowerExtraTierCost;
        BzGeneralConfigs.crystallineFlowerExtraXpNeededForTiers = crystallineFlowerExtraXpNeededForTiers;

        //Dimension
        BzDimensionConfigs.enableInitialWelcomeMessage = enableInitialWelcomeMessage;
        BzDimensionConfigs.fogBrightnessPercentage = fogBrightnessPercentage;
        BzDimensionConfigs.fogThickness = fogThickness;
        BzDimensionConfigs.enableDimensionFog = enableDimensionFog;
        BzDimensionConfigs.onlyOverworldHivesTeleports = onlyOverworldHivesTeleports;
        BzDimensionConfigs.forceExitToOverworld = forceExitToOverworld;
        BzDimensionConfigs.warnPlayersOfWrongBlockUnderHive = warnPlayersOfWrongBlockUnderHive;
        BzDimensionConfigs.enableExitTeleportation = enableExitTeleportation;
        BzDimensionConfigs.enableEntranceTeleportation = enableEntranceTeleportation;
        BzDimensionConfigs.forceBumblezoneOriginMobToOverworldCenter = forceBumblezoneOriginMobToOverworldCenter;
        BzDimensionConfigs.defaultDimension = defaultDimension;

        //Dungeon Config
        BzWorldgenConfigs.beeDungeonRarity = beeDungeonRarity;
        BzWorldgenConfigs.treeDungeonRarity = treeDungeonRarity;
        BzWorldgenConfigs.spiderInfestedBeeDungeonRarity = spiderInfestedBeeDungeonRarity;
        BzWorldgenConfigs.spawnerRateSpiderBeeDungeon = spawnerRateSpiderBeeDungeon;

        //General
        if (BzGeneralConfigs.beehemothSpeed != beehemothSpeed) {
            BzGeneralConfigs.beehemothSpeed = beehemothSpeed;
            BeehemothEntity.beehemothSpeedConfigChanged = true;
        }

        BzGeneralConfigs.variantBeeTypes = variantBeeTypes;
        BzGeneralConfigs.beehemothFriendlyFire = beehemothFriendlyFire;
        BzGeneralConfigs.beeQueenBonusTradeRewardMultiplier = beeQueenBonusTradeRewardMultiplier;
        BzGeneralConfigs.beeQueenBonusTradeDurationInTicks = beeQueenBonusTradeDurationInTicks;
        BzGeneralConfigs.beeQueenBonusTradeAmountTillSatified = beeQueenBonusTradeAmountTillSatified;
        BzGeneralConfigs.beeQueenSpecialDayTrades = beeQueenSpecialDayTrades;
        BzGeneralConfigs.beeQueenRespawning = beeQueenRespawning;
        BzGeneralConfigs.specialBeeSpawning = specialBeeSpawning;
        BzGeneralConfigs.beeLootInjection = beeLootInjection;
        BzGeneralConfigs.moddedBeeLootInjection = moddedBeeLootInjection;
        BzGeneralConfigs.glisteringHoneyBrewingRecipe = glisteringHoneyBrewingRecipe;
        BzGeneralConfigs.beeStingerBrewingRecipe = beeStingerBrewingRecipe;
        BzGeneralConfigs.beeSoupBrewingRecipe = beeSoupBrewingRecipe;
        BzGeneralConfigs.nearbyBeesPerPlayerInBz = nearbyBeesPerPlayerInBz;
        BzGeneralConfigs.allowWanderingTraderMusicDiscsTrades = allowWanderingTraderMusicDiscsTrades;
        BzGeneralConfigs.repeatableEssenceEvents = repeatableEssenceEvents;
        BzGeneralConfigs.cosmicCrystalHealth = cosmicCrystalHealth;
        BzGeneralConfigs.ragingEssenceAbilityUse = ragingEssenceAbilityUse;
        BzGeneralConfigs.ragingEssenceCooldown = ragingEssenceCooldown;
        BzGeneralConfigs.ragingEssenceStrengthLevels = ragingEssenceStrengthLevels;
        BzGeneralConfigs.knowingEssenceAbilityUse = knowingEssenceAbilityUse;
        BzGeneralConfigs.knowingEssenceCooldown = knowingEssenceCooldown;
        BzGeneralConfigs.knowingEssenceStructureNameServer = knowingEssenceStructureNameServer;
        BzGeneralConfigs.calmingEssenceAbilityUse = calmingEssenceAbilityUse;
        BzGeneralConfigs.calmingEssenceCooldown = calmingEssenceCooldown;
        BzGeneralConfigs.radianceEssenceAbilityUse = radianceEssenceAbilityUse;
        BzGeneralConfigs.radianceEssenceCooldown = radianceEssenceCooldown;
        BzGeneralConfigs.lifeEssenceAbilityUse = lifeEssenceAbilityUse;
        BzGeneralConfigs.lifeEssenceCooldown = lifeEssenceCooldown;
        BzGeneralConfigs.continuityEssenceCooldown = continuityEssenceCooldown;

        //Compat
        BzModCompatibilityConfigs.alternativeFluidToReplaceHoneyFluid = alternativeFluidToReplaceHoneyFluid;
        BzModCompatibilityConfigs.allowFriendsAndFoesBeekeeperTradesCompat = allowFriendsAndFoesBeekeeperTradesCompat;
        BzModCompatibilityConfigs.allowGoodallBottledBeesRevivingEmptyBroodBlock = allowGoodallBottledBeesRevivingEmptyBroodBlock;
        BzModCompatibilityConfigs.allowLootrCompat = allowLootrCompat;

        //Client
        BzClientConfigs.useBackupModelForVariantBee = useBackupModelForVariantBee;
        BzClientConfigs.playWrathOfHiveEffectMusic = playWrathOfHiveEffectMusic;
        BzClientConfigs.playSempiternalSanctumMusic = playSempiternalSanctumMusic;
        BzClientConfigs.renderBeeQueenBonusTradeItem = renderBeeQueenBonusTradeItem;
        BzClientConfigs.disableEssenceBlockShaders = disableEssenceBlockShaders;
        BzClientConfigs.knowingEssenceHighlightBosses = knowingEssenceHighlightBosses;
        BzClientConfigs.knowingEssenceHighlightMonsters = knowingEssenceHighlightMonsters;
        BzClientConfigs.knowingEssenceHighlightTamed = knowingEssenceHighlightTamed;
        BzClientConfigs.knowingEssenceHighlightLivingEntities = knowingEssenceHighlightLivingEntities;
        BzClientConfigs.knowingEssenceHighlightCommonItems = knowingEssenceHighlightCommonItems;
        BzClientConfigs.knowingEssenceHighlightUncommonItems = knowingEssenceHighlightUncommonItems;
        BzClientConfigs.knowingEssenceHighlightRareItems = knowingEssenceHighlightRareItems;
        BzClientConfigs.knowingEssenceHighlightEpicItems = knowingEssenceHighlightEpicItems;
        BzClientConfigs.knowingEssenceStructureNameClient = knowingEssenceStructureNameClient;
        BzClientConfigs.knowingEssenceStructureNameXCoord = knowingEssenceStructureNameXCoord;
        BzClientConfigs.knowingEssenceStructureNameYCoord = knowingEssenceStructureNameYCoord;
        BzClientConfigs.radianceEssenceArmorDurability = radianceEssenceArmorDurability;
        BzClientConfigs.radianceEssenceArmorDurabilityXCoord = radianceEssenceArmorDurabilityXCoord;
        BzClientConfigs.radianceEssenceArmorDurabilityYCoord = radianceEssenceArmorDurabilityYCoord;
        BzClientConfigs.essenceItemHUDVisualEffectLayers = essenceItemHUDVisualEffectLayers;
        BzClientConfigs.essenceItemHUDVisualEffectSpeed = essenceItemHUDVisualEffectSpeed;
    }

}