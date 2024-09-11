package com.telepathicgrunt.the_bumblezone.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.client.armor.BeeArmorModelProvider;
import com.telepathicgrunt.the_bumblezone.client.armor.FlowerHeadwearModelProvider;
import com.telepathicgrunt.the_bumblezone.client.blockentityrenderer.EssenceBlockEntityRenderer;
import com.telepathicgrunt.the_bumblezone.client.blocks.ConnectedBlockModel;
import com.telepathicgrunt.the_bumblezone.client.dimension.BzDimensionSpecialEffects;
import com.telepathicgrunt.the_bumblezone.client.items.FlowerHeadwearColoring;
import com.telepathicgrunt.the_bumblezone.client.items.HoneyCompassItemProperty;
import com.telepathicgrunt.the_bumblezone.client.items.InfinityBarrierColoring;
import com.telepathicgrunt.the_bumblezone.client.items.PotionCandleColoring;
import com.telepathicgrunt.the_bumblezone.client.particles.DustParticle;
import com.telepathicgrunt.the_bumblezone.client.particles.HoneyParticle;
import com.telepathicgrunt.the_bumblezone.client.particles.PollenPuffParticle;
import com.telepathicgrunt.the_bumblezone.client.particles.RoyalJellyParticle;
import com.telepathicgrunt.the_bumblezone.client.particles.SparkleParticle;
import com.telepathicgrunt.the_bumblezone.client.particles.VoiceParticle;
import com.telepathicgrunt.the_bumblezone.client.particles.WindParticle;
import com.telepathicgrunt.the_bumblezone.client.rendering.HiddenEffectIconRenderer;
import com.telepathicgrunt.the_bumblezone.client.rendering.armor.BeeArmorModel;
import com.telepathicgrunt.the_bumblezone.client.rendering.armor.FlowerHeadwearModel;
import com.telepathicgrunt.the_bumblezone.client.rendering.beehemoth.BeehemothModel;
import com.telepathicgrunt.the_bumblezone.client.rendering.beehemoth.BeehemothRenderer;
import com.telepathicgrunt.the_bumblezone.client.rendering.beequeen.BeeQueenModel;
import com.telepathicgrunt.the_bumblezone.client.rendering.beequeen.BeeQueenRenderer;
import com.telepathicgrunt.the_bumblezone.client.rendering.beestinger.BeeStingerModel;
import com.telepathicgrunt.the_bumblezone.client.rendering.beestinger.BeeStingerRenderer;
import com.telepathicgrunt.the_bumblezone.client.rendering.cosmiccrystal.CosmicCrystalModel;
import com.telepathicgrunt.the_bumblezone.client.rendering.cosmiccrystal.CosmicCrystalRenderer;
import com.telepathicgrunt.the_bumblezone.client.rendering.electricring.ElectricRingModel;
import com.telepathicgrunt.the_bumblezone.client.rendering.electricring.ElectricRingRenderer;
import com.telepathicgrunt.the_bumblezone.client.rendering.essence.KnowingEssenceLootBlockOutlining;
import com.telepathicgrunt.the_bumblezone.client.rendering.honeycrystalshard.HoneyCrystalShardModel;
import com.telepathicgrunt.the_bumblezone.client.rendering.honeycrystalshard.HoneyCrystalShardRenderer;
import com.telepathicgrunt.the_bumblezone.client.rendering.honeyslime.HoneySlimeRendering;
import com.telepathicgrunt.the_bumblezone.client.rendering.pileofpollen.PileOfPollenRenderer;
import com.telepathicgrunt.the_bumblezone.client.rendering.purplespike.PurpleSpikeModel;
import com.telepathicgrunt.the_bumblezone.client.rendering.purplespike.PurpleSpikeRenderer;
import com.telepathicgrunt.the_bumblezone.client.rendering.rootmin.RootminModel;
import com.telepathicgrunt.the_bumblezone.client.rendering.rootmin.RootminRenderer;
import com.telepathicgrunt.the_bumblezone.client.rendering.sentrywatcher.SentryWatcherModel;
import com.telepathicgrunt.the_bumblezone.client.rendering.sentrywatcher.SentryWatcherRenderer;
import com.telepathicgrunt.the_bumblezone.client.rendering.stingerspear.StingerSpearModel;
import com.telepathicgrunt.the_bumblezone.client.rendering.stingerspear.StingerSpearRenderer;
import com.telepathicgrunt.the_bumblezone.client.rendering.variantbee.BackupVariantBeeModel;
import com.telepathicgrunt.the_bumblezone.client.rendering.variantbee.BackupVariantBeeRenderer;
import com.telepathicgrunt.the_bumblezone.client.rendering.variantbee.VariantBeeRenderer;
import com.telepathicgrunt.the_bumblezone.client.screens.BuzzingBriefcaseScreen;
import com.telepathicgrunt.the_bumblezone.client.screens.CrystallineFlowerScreen;
import com.telepathicgrunt.the_bumblezone.client.screens.StrictChestScreen;
import com.telepathicgrunt.the_bumblezone.client.utils.GeneralUtilsClient;
import com.telepathicgrunt.the_bumblezone.configs.BzClientConfigs;
import com.telepathicgrunt.the_bumblezone.events.client.BzBlockRenderedOnScreenEvent;
import com.telepathicgrunt.the_bumblezone.events.client.BzClientSetupEnqueuedEvent;
import com.telepathicgrunt.the_bumblezone.events.client.BzClientTickEvent;
import com.telepathicgrunt.the_bumblezone.events.client.BzKeyInputEvent;
import com.telepathicgrunt.the_bumblezone.events.client.BzRegisterArmorProviderEvent;
import com.telepathicgrunt.the_bumblezone.events.client.BzRegisterBlockColorEvent;
import com.telepathicgrunt.the_bumblezone.events.client.BzRegisterBlockEntityRendererEvent;
import com.telepathicgrunt.the_bumblezone.events.client.BzRegisterDimensionEffectsEvent;
import com.telepathicgrunt.the_bumblezone.events.client.BzRegisterEffectRenderersEvent;
import com.telepathicgrunt.the_bumblezone.events.client.BzRegisterEntityLayersEvent;
import com.telepathicgrunt.the_bumblezone.events.client.BzRegisterEntityRenderersEvent;
import com.telepathicgrunt.the_bumblezone.events.client.BzRegisterItemColorEvent;
import com.telepathicgrunt.the_bumblezone.events.client.BzRegisterItemPropertiesEvent;
import com.telepathicgrunt.the_bumblezone.events.client.BzRegisterKeyMappingEvent;
import com.telepathicgrunt.the_bumblezone.events.client.BzRegisterMenuScreenEvent;
import com.telepathicgrunt.the_bumblezone.events.client.BzRegisterParticleEvent;
import com.telepathicgrunt.the_bumblezone.events.client.BzRegisterRenderTypeEvent;
import com.telepathicgrunt.the_bumblezone.events.client.BzRegisterShaderEvent;
import com.telepathicgrunt.the_bumblezone.events.lifecycle.BzTagsUpdatedEvent;
import com.telepathicgrunt.the_bumblezone.events.player.BzPlayerTickEvent;
import com.telepathicgrunt.the_bumblezone.items.BeeCannon;
import com.telepathicgrunt.the_bumblezone.items.CrystalCannon;
import com.telepathicgrunt.the_bumblezone.items.HoneyBeeLeggings;
import com.telepathicgrunt.the_bumblezone.items.StinglessBeeHelmet;
import com.telepathicgrunt.the_bumblezone.items.datacomponents.AbilityEssenceActivityData;
import com.telepathicgrunt.the_bumblezone.items.essence.AbilityEssenceItem;
import com.telepathicgrunt.the_bumblezone.mixin.blocks.BlockEntityRenderersAccessor;
import com.telepathicgrunt.the_bumblezone.mixin.client.ClientLevelAccessor;
import com.telepathicgrunt.the_bumblezone.modinit.BzBlockEntities;
import com.telepathicgrunt.the_bumblezone.modinit.BzBlocks;
import com.telepathicgrunt.the_bumblezone.modinit.BzClientFluids;
import com.telepathicgrunt.the_bumblezone.modinit.BzDataComponents;
import com.telepathicgrunt.the_bumblezone.modinit.BzEffects;
import com.telepathicgrunt.the_bumblezone.modinit.BzEntities;
import com.telepathicgrunt.the_bumblezone.modinit.BzFluids;
import com.telepathicgrunt.the_bumblezone.modinit.BzItems;
import com.telepathicgrunt.the_bumblezone.modinit.BzMenuTypes;
import com.telepathicgrunt.the_bumblezone.modinit.BzParticles;
import earth.terrarium.athena.api.client.models.FactoryManager;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BrushableBlockRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static net.minecraft.client.renderer.RenderStateShard.NO_CULL;
import static net.minecraft.client.renderer.RenderStateShard.NO_OVERLAY;
import static net.minecraft.client.renderer.RenderStateShard.NO_TRANSPARENCY;
import static net.minecraft.client.renderer.RenderStateShard.RENDERTYPE_ENERGY_SWIRL_SHADER;
import static net.minecraft.client.renderer.RenderStateShard.TRANSLUCENT_TRANSPARENCY;

public class BumblezoneClient {
    public static final Function<ResourceLocation, RenderType> ENTITY_CUTOUT_EMISSIVE_RENDER_TYPE = Util.memoize((resourceLocation) -> {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
                .setTransparencyState(NO_TRANSPARENCY)
                .setCullState(NO_CULL)
                .setOverlayState(NO_OVERLAY)
                .createCompositeState(false);

        return RenderType.create(Bumblezone.MODID + ":entity_cutout_emissive",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                256,
                false,
                true,
                compositeState);
    });

    public static final Function<ResourceLocation, RenderType> ENTITY_TRANSPARENT_EMISSIVE_RENDER_TYPE = Util.memoize((resourceLocation) -> {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setCullState(NO_CULL)
                .setOverlayState(NO_OVERLAY)
                .createCompositeState(false);

        return RenderType.create(Bumblezone.MODID + ":entity_transparent_emissive",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                256,
                false,
                true,
                compositeState);
    });

    public static void init() {
        FactoryManager.register(ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "connected"), ConnectedBlockModel.FACTORY);
        BzPlayerTickEvent.CLIENT_EVENT.addListener(MusicHandler::tickMusicFader);

        BzRegisterParticleEvent.EVENT.addListener(BumblezoneClient::onParticleSetup);
        BzRegisterEntityRenderersEvent.EVENT.addListener(BumblezoneClient::registerEntityRenderers);
        BzRegisterEntityLayersEvent.EVENT.addListener(BumblezoneClient::registerEntityLayers);
        BzRegisterKeyMappingEvent.EVENT.addListener(BumblezoneClient::registerKeyBinding);
        BzRegisterDimensionEffectsEvent.EVENT.addListener(BumblezoneClient::registerDimensionEffects);
        BzRegisterShaderEvent.EVENT.addListener(BumblezoneClient::registerShaders);
        BzRegisterBlockColorEvent.EVENT.addListener(InfinityBarrierColoring::registerBlockColors);
        BzRegisterBlockColorEvent.EVENT.addListener(PotionCandleColoring::registerBlockColors);
        BzRegisterItemColorEvent.EVENT.addListener(PotionCandleColoring::registerItemColors);
        BzRegisterItemColorEvent.EVENT.addListener(FlowerHeadwearColoring::registerItemColors);
        BzClientTickEvent.EVENT.addListener(event -> {
            if (event.end()) {
                StinglessBeeHelmet.decrementHighlightingCounter(GeneralUtilsClient.getClientPlayer());
            }
        });

        BzClientSetupEnqueuedEvent.EVENT.addListener(BumblezoneClient::clientSetup);
        BzBlockRenderedOnScreenEvent.EVENT.addListener(PileOfPollenRenderer::pileOfPollenOverlay);
        BzKeyInputEvent.EVENT.addListener(BeehemothControls::keyInput);
        BzRegisterMenuScreenEvent.EVENT.addListener(BumblezoneClient::registerScreens);
        BzRegisterItemPropertiesEvent.EVENT.addListener(BumblezoneClient::registerItemProperties);
        BzRegisterRenderTypeEvent.EVENT.addListener(BumblezoneClient::registerRenderTypes);
        BzRegisterArmorProviderEvent.EVENT.addListener(BumblezoneClient::registerArmorProviders);
        BzRegisterEffectRenderersEvent.EVENT.addListener(BumblezoneClient::registerEffectRenderers);
        BzRegisterBlockEntityRendererEvent.EVENT.addListener(BumblezoneClient::registerBlockEntityRenderers);
        BzTagsUpdatedEvent.EVENT.addListener((tagsUpdatedEvent) -> KnowingEssenceLootBlockOutlining.resetTargetBlockCache());

        BzClientFluids.CLIENT_FLUIDS.init();
    }

    public static void clientSetup(BzClientSetupEnqueuedEvent event) {
        Set<Item> particleMarkerBlocks = new HashSet<>(ClientLevelAccessor.getMARKER_PARTICLE_ITEMS());
        particleMarkerBlocks.add(BzItems.HEAVY_AIR.get());
        ClientLevelAccessor.setMARKER_PARTICLE_ITEMS(particleMarkerBlocks);
    }

    public static void registerBlockEntityRenderers(BzRegisterBlockEntityRendererEvent<?> event) {
        BlockEntityRenderersAccessor.bumblezone$callRegister(BzBlockEntities.ESSENCE_BLOCK.get(), EssenceBlockEntityRenderer::new);
        BlockEntityRenderersAccessor.bumblezone$callRegister(BzBlockEntities.STATE_FOCUSED_BRUSHABLE_BLOCK_ENTITY.get(), BrushableBlockRenderer::new);
    }

    public static void registerEffectRenderers(BzRegisterEffectRenderersEvent event) {
        event.register(BzEffects.HIDDEN.holder(), new HiddenEffectIconRenderer());
    }

    public static void registerArmorProviders(BzRegisterArmorProviderEvent event) {
        event.register(BzItems.FLOWER_HEADWEAR.get(), FlowerHeadwearModelProvider::new);
        event.register(BzItems.STINGLESS_BEE_HELMET_1.get(), BeeArmorModelProvider::new);
        event.register(BzItems.STINGLESS_BEE_HELMET_2.get(), BeeArmorModelProvider::new);
        event.register(BzItems.BUMBLE_BEE_CHESTPLATE_1.get(), BeeArmorModelProvider::new);
        event.register(BzItems.BUMBLE_BEE_CHESTPLATE_2.get(), BeeArmorModelProvider::new);
        event.register(BzItems.TRANS_BUMBLE_BEE_CHESTPLATE_1.get(), BeeArmorModelProvider::new);
        event.register(BzItems.TRANS_BUMBLE_BEE_CHESTPLATE_2.get(), BeeArmorModelProvider::new);
        event.register(BzItems.HONEY_BEE_LEGGINGS_1.get(), BeeArmorModelProvider::new);
        event.register(BzItems.HONEY_BEE_LEGGINGS_2.get(), BeeArmorModelProvider::new);
        event.register(BzItems.CARPENTER_BEE_BOOTS_1.get(), BeeArmorModelProvider::new);
        event.register(BzItems.CARPENTER_BEE_BOOTS_2.get(), BeeArmorModelProvider::new);
    }

    public static void registerKeyBinding(BzRegisterKeyMappingEvent event) {
        event.register(BeehemothControls.KEY_BIND_BEEHEMOTH_UP);
        event.register(BeehemothControls.KEY_BIND_BEEHEMOTH_DOWN);
    }

    private static void registerScreens(BzRegisterMenuScreenEvent event) {
        event.register(BzMenuTypes.STRICT_9x1.get(), StrictChestScreen::new);
        event.register(BzMenuTypes.STRICT_9x2.get(), StrictChestScreen::new);
        event.register(BzMenuTypes.STRICT_9x3.get(), StrictChestScreen::new);
        event.register(BzMenuTypes.STRICT_9x4.get(), StrictChestScreen::new);
        event.register(BzMenuTypes.STRICT_9x5.get(), StrictChestScreen::new);
        event.register(BzMenuTypes.STRICT_9x6.get(), StrictChestScreen::new);
        event.register(BzMenuTypes.CRYSTALLINE_FLOWER.get(), CrystallineFlowerScreen::new);
        event.register(BzMenuTypes.BUZZING_BRIEFCASE.get(), BuzzingBriefcaseScreen::new);
    }

    @SuppressWarnings("ConstantConditions")
    private static void registerItemProperties(BzRegisterItemPropertiesEvent event) {
        // Allows shield to use the blocking json file for offset
        event.register(
                BzItems.HONEY_CRYSTAL_SHIELD.get(),
                ResourceLocation.withDefaultNamespace("blocking"),
                (itemStack, world, livingEntity, integer) ->
                        livingEntity != null &&
                                livingEntity.isUsingItem() &&
                                livingEntity.getUseItem() == itemStack ? 1.0F : 0.0F
        );

        // Correct model when about to throw
        event.register(
                BzItems.STINGER_SPEAR.get(),
                ResourceLocation.withDefaultNamespace("throwing"),
                (itemStack, world, livingEntity, integer) ->
                        livingEntity != null &&
                                livingEntity.isUsingItem() &&
                                livingEntity.getUseItem() == itemStack ? 1.0F : 0.0F
        );

        // Allows honey compass to render the correct texture
        event.register(
                BzItems.HONEY_COMPASS.get(),
                ResourceLocation.withDefaultNamespace("angle"),
                HoneyCompassItemProperty.getClampedItemPropertyFunction());

        // Correct model when about to fire
        event.register(
                BzItems.BEE_CANNON.get(),
                ResourceLocation.withDefaultNamespace("primed"),
                (itemStack, world, livingEntity, int1) ->
                        livingEntity != null &&
                                livingEntity.isUsingItem() &&
                                livingEntity.getUseItem() == itemStack ? 1.0F : 0.0F
        );

        event.register(
                BzItems.CRYSTAL_CANNON.get(),
                ResourceLocation.withDefaultNamespace("primed"),
                (itemStack, world, livingEntity, int1) ->
                        livingEntity != null &&
                                livingEntity.isUsingItem() &&
                                livingEntity.getUseItem() == itemStack ? 1.0F : 0.0F
        );

        // Correct model based on bees
        event.register(
                BzItems.BEE_CANNON.get(),
                ResourceLocation.withDefaultNamespace("bee_count"),
                (itemStack, world, livingEntity, int1) ->
                        BeeCannon.getNumberOfBees(itemStack) / 10f
        );

        // Correct model based on crystals
        event.register(
                BzItems.CRYSTAL_CANNON.get(),
                ResourceLocation.withDefaultNamespace("crystal_count"),
                (itemStack, world, livingEntity, int1) ->
                        CrystalCannon.getNumberOfCrystals(itemStack) / 10f
        );


        // Correct model based on crystals
        event.register(
                BzItems.CRYSTAL_CANNON.get(),
                ResourceLocation.withDefaultNamespace("crystal_count"),
                (itemStack, world, livingEntity, int1) ->
                        CrystalCannon.getNumberOfCrystals(itemStack) / 10f
        );


        // Show different stage for creative menu icon
        event.register(
                BzItems.HONEYCOMB_BROOD.get(),
                ResourceLocation.withDefaultNamespace("is_creative_tab_icon"),
                (itemStack, world, livingEntity, integer) ->
                        itemStack.getComponents().has(DataComponents.CUSTOM_DATA) &&
                        itemStack.getComponents().get(DataComponents.CUSTOM_DATA).contains("isCreativeTabIcon") &&
                        itemStack.getComponents().get(DataComponents.CUSTOM_DATA).getUnsafe().getBoolean("isCreativeTabIcon") ? 1.0F : 0.0F
        );


        // Correct model based on pollen on leggings
        event.register(
                BzItems.HONEY_BEE_LEGGINGS_1.get(),
                ResourceLocation.withDefaultNamespace("pollen"),
                (itemStack, world, livingEntity, int1) ->
                        HoneyBeeLeggings.isPollinated(itemStack) ? 1f : 0f
        );


        // Correct model based on pollen on leggings
        event.register(
                BzItems.HONEY_BEE_LEGGINGS_2.get(),
                ResourceLocation.withDefaultNamespace("pollen"),
                (itemStack, world, livingEntity, int1) ->
                        HoneyBeeLeggings.isPollinated(itemStack) ? 1f : 0f
        );


        // Show state of essence
        registerEssenceItemProperty(event, BzItems.ESSENCE_RAGING.get());
        registerEssenceItemProperty(event, BzItems.ESSENCE_KNOWING.get());
        registerEssenceItemProperty(event, BzItems.ESSENCE_CALMING.get());
        registerEssenceItemProperty(event, BzItems.ESSENCE_LIFE.get());
        registerEssenceItemProperty(event, BzItems.ESSENCE_RADIANCE.get());
        registerEssenceItemProperty(event, BzItems.ESSENCE_CONTINUITY.get());
    }

    private static void registerEssenceItemProperty(BzRegisterItemPropertiesEvent event, Item item) {
        event.register(
            item,
            ResourceLocation.withDefaultNamespace("state"),
            (itemStack, world, livingEntity, integer) -> {
                if (itemStack.getItem() instanceof AbilityEssenceItem abilityEssenceItem) {
                    AbilityEssenceActivityData abilityEssenceActivityData = itemStack.get(BzDataComponents.ABILITY_ESSENCE_ACTIVITY_DATA.get());
                    if (!abilityEssenceActivityData.isInInventory()) {
                        return 0.0F;
                    }
                    else if (abilityEssenceActivityData.isActive()) {
                        return abilityEssenceItem.getAbilityUseRemaining(itemStack) == abilityEssenceItem.getMaxAbilityUseAmount() ?
                              0.2F : 0.25F;
                    }
                    else if (abilityEssenceActivityData.isLocked() || itemStack.get(BzDataComponents.ABILITY_ESSENCE_COOLDOWN_DATA.get()).forcedCooldown()) {
                        return 0.3F;
                    }
                    else {
                        return abilityEssenceItem.getAbilityUseRemaining(itemStack) == abilityEssenceItem.getMaxAbilityUseAmount() ?
                                0.1F : 0.15F;
                    }
                }
                return 0.0F;
            }
        );
    }

    private static void registerRenderTypes(BzRegisterRenderTypeEvent event) {
        event.register(RenderType.translucent(),
                BzFluids.SUGAR_WATER_FLUID.get(),
                BzFluids.SUGAR_WATER_FLUID_FLOWING.get(),
                BzFluids.HONEY_FLUID.get(),
                BzFluids.HONEY_FLUID_FLOWING.get(),
                BzFluids.ROYAL_JELLY_FLUID.get(),
                BzFluids.ROYAL_JELLY_FLUID_FLOWING.get()
        );

        event.register(RenderType.cutout(),
                BzBlocks.STICKY_HONEY_REDSTONE.get(),
                BzBlocks.STICKY_HONEY_RESIDUE.get(),
                BzBlocks.HONEY_WEB.get(),
                BzBlocks.REDSTONE_HONEY_WEB.get(),
                BzBlocks.SUPER_CANDLE_WICK.get(),
                BzBlocks.SUPER_CANDLE_WICK_SOUL.get(),
                BzBlocks.POTION_BASE_CANDLE.get(),
                BzBlocks.CRYSTALLINE_FLOWER.get(),
                BzBlocks.POROUS_HONEYCOMB.get(),
                BzBlocks.EMPTY_HONEYCOMB_BROOD.get(),
                BzBlocks.INFINITY_BARRIER.get()
        );

        BzBlocks.CURTAINS.stream().map(RegistryEntry::get).forEach(block -> event.register(RenderType.cutout(), block));

        event.register(RenderType.translucent(),
                BzBlocks.HONEY_CRYSTAL.get(),
                BzBlocks.GLISTERING_HONEY_CRYSTAL.get(),
                BzBlocks.ROYAL_JELLY_BLOCK.get(),
                BzBlocks.ESSENCE_BLOCK_RED.get(),
                BzBlocks.ESSENCE_BLOCK_PURPLE.get(),
                BzBlocks.ESSENCE_BLOCK_BLUE.get(),
                BzBlocks.ESSENCE_BLOCK_GREEN.get(),
                BzBlocks.ESSENCE_BLOCK_YELLOW.get(),
                BzBlocks.ESSENCE_BLOCK_WHITE.get()
        );
    }

    public static void registerEntityLayers(BzRegisterEntityLayersEvent event) {
        if (BzClientConfigs.useBackupModelForVariantBee) {
            event.register(BackupVariantBeeModel.LAYER_LOCATION, BackupVariantBeeModel::createBodyLayer);
        }

        event.register(BeehemothModel.LAYER_LOCATION, BeehemothModel::createBodyLayer);
        event.register(BeeQueenModel.LAYER_LOCATION, BeeQueenModel::createBodyLayer);
        event.register(SentryWatcherModel.LAYER_LOCATION, SentryWatcherModel::createBodyLayer);
        event.register(RootminModel.LAYER_LOCATION, RootminModel::createBodyLayer);
        event.register(StingerSpearModel.LAYER_LOCATION, StingerSpearModel::createLayer);
        event.register(BeeStingerModel.LAYER_LOCATION, BeeStingerModel::createLayer);
        event.register(HoneyCrystalShardModel.LAYER_LOCATION, HoneyCrystalShardModel::createLayer);
        event.register(BeeArmorModel.VARIANT_1_LAYER_LOCATION, BeeArmorModel::createVariant1);
        event.register(BeeArmorModel.VARIANT_2_LAYER_LOCATION, BeeArmorModel::createVariant2);
        event.register(FlowerHeadwearModel.FLOWER_HEADWEAR_LAYER_LOCATION, FlowerHeadwearModel::createBodyLayer);
        event.register(ElectricRingModel.LAYER_LOCATION, ElectricRingModel::createBodyLayer);
        event.register(PurpleSpikeModel.LAYER_LOCATION, PurpleSpikeModel::createBodyLayer);
        event.register(CosmicCrystalModel.LAYER_LOCATION, CosmicCrystalModel::createBodyLayer);
    }

    public static void registerEntityRenderers(BzRegisterEntityRenderersEvent event) {
        if (BzClientConfigs.useBackupModelForVariantBee) {
            event.register(BzEntities.VARIANT_BEE.get(), BackupVariantBeeRenderer::new);
        }
        else {
            event.register((EntityType) BzEntities.VARIANT_BEE.get(), VariantBeeRenderer::new);
        }

        event.register(BzEntities.HONEY_SLIME.get(), HoneySlimeRendering::new);
        event.register(BzEntities.BEEHEMOTH.get(), BeehemothRenderer::new);
        event.register(BzEntities.BEE_QUEEN.get(), BeeQueenRenderer::new);
        event.register(BzEntities.ROOTMIN.get(), RootminRenderer::new);
        event.register(BzEntities.SENTRY_WATCHER.get(), SentryWatcherRenderer::new);
        event.register(BzEntities.POLLEN_PUFF_ENTITY.get(), ThrownItemRenderer::new);
        event.register(BzEntities.DIRT_PELLET_ENTITY.get(), ThrownItemRenderer::new);
        event.register(BzEntities.THROWN_STINGER_SPEAR_ENTITY.get(), StingerSpearRenderer::new);
        event.register(BzEntities.BEE_STINGER_ENTITY.get(), BeeStingerRenderer::new);
        event.register(BzEntities.HONEY_CRYSTAL_SHARD.get(), HoneyCrystalShardRenderer::new);
        event.register(BzEntities.ELECTRIC_RING_ENTITY.get(), ElectricRingRenderer::new);
        event.register(BzEntities.PURPLE_SPIKE_ENTITY.get(), PurpleSpikeRenderer::new);
        event.register(BzEntities.COSMIC_CRYSTAL_ENTITY.get(), CosmicCrystalRenderer::new);
    }

    public static void onParticleSetup(BzRegisterParticleEvent event) {
        event.register(BzParticles.POLLEN_PARTICLE.get(), PollenPuffParticle.Factory::new);
        event.register(BzParticles.SPARKLE_PARTICLE.get(), SparkleParticle.Factory::new);
        event.register(BzParticles.HONEY_PARTICLE.get(), HoneyParticle.Factory::new);
        event.register(BzParticles.ROYAL_JELLY_PARTICLE.get(), RoyalJellyParticle.Factory::new);
        event.register(BzParticles.DUST_PARTICLE.get(), DustParticle.Factory::new);
        event.register(BzParticles.WIND_PARTICLE.get(), (spriteSet) -> new WindParticle.Factory(spriteSet, false));
        event.register(BzParticles.MOVING_WIND_PARTICLE.get(), (spriteSet) -> new WindParticle.Factory(spriteSet, true));
        event.register(BzParticles.ANGRY_PARTICLE.get(), VoiceParticle.Factory::new);
        event.register(BzParticles.CURIOUS_PARTICLE.get(), VoiceParticle.Factory::new);
        event.register(BzParticles.CURSING_PARTICLE.get(), VoiceParticle.Factory::new);
        event.register(BzParticles.EMBARRASSED_PARTICLE.get(), VoiceParticle.Factory::new);
        event.register(BzParticles.SHOCK_PARTICLE.get(), VoiceParticle.Factory::new);
    }

    public static void registerDimensionEffects(BzRegisterDimensionEffectsEvent event) {
        event.register(ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "dimension_special_effects"), new BzDimensionSpecialEffects());
    }

    public static void registerShaders(BzRegisterShaderEvent event) {
        event.register(
            ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "rendertype_bumblezone_essence"),
            EssenceBlockEntityRenderer.POSITION_COLOR_NORMAL,
            (safeShader) -> EssenceBlockEntityRenderer.SAFE_SHADER_INSTANCE = safeShader
        );
    }
}