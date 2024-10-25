package com.telepathicgrunt.the_bumblezone.modcompat;

import com.mojang.datafixers.util.Pair;
import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.client.utils.GeneralUtilsClient;
import com.telepathicgrunt.the_bumblezone.configs.BzModCompatibilityConfigs;
import com.telepathicgrunt.the_bumblezone.entities.datamanagers.queentrades.QueensTradeManager;
import com.telepathicgrunt.the_bumblezone.entities.datamanagers.queentrades.WeightedTradeResult;
import com.telepathicgrunt.the_bumblezone.items.recipes.PotionCandleRecipe;
import com.telepathicgrunt.the_bumblezone.modcompat.recipecategories.MainTradeRowInput;
import com.telepathicgrunt.the_bumblezone.modcompat.recipecategories.RandomizeTradeRowInput;
import com.telepathicgrunt.the_bumblezone.modcompat.recipecategories.jei.JEIQueenRandomizerTradesInfo;
import com.telepathicgrunt.the_bumblezone.modcompat.recipecategories.jei.JEIQueenTradesInfo;
import com.telepathicgrunt.the_bumblezone.modcompat.recipecategories.jei.QueenRandomizeTradesJEICategory;
import com.telepathicgrunt.the_bumblezone.modcompat.recipecategories.jei.QueenTradesJEICategory;
import com.telepathicgrunt.the_bumblezone.modinit.BzCreativeTabs;
import com.telepathicgrunt.the_bumblezone.modinit.BzFluids;
import com.telepathicgrunt.the_bumblezone.modinit.BzItems;
import com.telepathicgrunt.the_bumblezone.modinit.BzTags;
import com.telepathicgrunt.the_bumblezone.utils.GeneralUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@JeiPlugin
public class JEIIntegration implements IModPlugin {

    public static final RecipeType<JEIQueenTradesInfo> QUEEN_TRADES = RecipeType.create(Bumblezone.MODID, "queen_trades", JEIQueenTradesInfo.class);
    public static final RecipeType<JEIQueenRandomizerTradesInfo> QUEEN_RANDOMIZE_TRADES = RecipeType.create(Bumblezone.MODID, "queen_color_randomizer_trades", JEIQueenRandomizerTradesInfo.class);

    private static void addInfo(IRecipeRegistration registration, Item item) {
        registration.addIngredientInfo(
                new ItemStack(item),
                VanillaTypes.ITEM_STACK,
                Component.translatable(Bumblezone.MODID + "." + BuiltInRegistries.ITEM.getKey(item).getPath() + ".description"));
    }

    private static void addInfo(IRecipeRegistration registration, Fluid fluid) {
        addFluidData(registration, fluid, registration.getJeiHelpers().getPlatformFluidHelper());
    }

    private static <T> void addFluidData(IRecipeRegistration registration, Fluid fluid, IPlatformFluidHelper<T> platformFluidHelper) {
        registration.addIngredientInfo(
                platformFluidHelper.create(fluid.builtInRegistryHolder(), 1000),
                platformFluidHelper.getFluidIngredientType(),
                Component.translatable(Bumblezone.MODID + "." + BuiltInRegistries.FLUID.getKey(fluid).getPath() + ".description"));
    }

    private static void registerExtraRecipes(RecipeHolder<?> baseRecipe, IRecipeRegistration registration, boolean oneRecipeOnly) {
        if (baseRecipe.value() instanceof PotionCandleRecipe potionCandleRecipe) {
            List<CraftingRecipe> extraRecipes = FakePotionCandleRecipeCreator.constructFakeRecipes(potionCandleRecipe, oneRecipeOnly);
            List<RecipeHolder<CraftingRecipe>> holders = new ArrayList<>(extraRecipes.size());
            for (int i = 0; i < extraRecipes.size(); i++) {
                holders.add(new RecipeHolder<>(ResourceLocation.fromNamespaceAndPath(baseRecipe.id().getNamespace(), baseRecipe.id().getPath() + "_" + i), extraRecipes.get(i)));
            }
            registration.addRecipes(RecipeTypes.CRAFTING, holders);
        }
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "jei_plugin");
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        BzCreativeTabs.CUSTOM_CREATIVE_TAB_ITEMS.forEach(item -> addInfo(registration, item.get()));
        addInfo(registration, BzFluids.SUGAR_WATER_FLUID.get());
        addInfo(registration, BzFluids.ROYAL_JELLY_FLUID.get());
        if (BzModCompatibilityConfigs.alternativeFluidToReplaceHoneyFluid.isEmpty()) {
            addInfo(registration, BzFluids.HONEY_FLUID.get());
        }

        Level level = GeneralUtilsClient.getClientLevel();
        if (level == null)
            return;

        level.getRecipeManager().byKey(ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "potion_candle/from_super_candles"))
                .ifPresent(recipe -> registerExtraRecipes(recipe, registration, true));

        level.getRecipeManager().byKey(ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "potion_candle/from_string_and_carvable_wax"))
                .ifPresent(recipe -> registerExtraRecipes(recipe, registration, false));

        List<JEIQueenTradesInfo> trades = new LinkedList<>();
        if (!QueensTradeManager.QUEENS_TRADE_MANAGER.recipeViewerMainTrades.isEmpty()) {
            for (Pair<MainTradeRowInput, WeightedRandomList<WeightedTradeResult>> trade : QueensTradeManager.QUEENS_TRADE_MANAGER.recipeViewerMainTrades) {
                for (WeightedTradeResult weightedTradeResult : trade.getSecond().unwrap()) {
                    trades.add(new JEIQueenTradesInfo(trade.getFirst(), weightedTradeResult));
                }
            }
        }
        registration.addRecipes(QUEEN_TRADES, trades);

        List<JEIQueenRandomizerTradesInfo> randomizerTrades = new LinkedList<>();
        if (!QueensTradeManager.QUEENS_TRADE_MANAGER.recipeViewerRandomizerTrades.isEmpty()) {
            Map<TagKey<Item>, TagData> cacheJeiData = new Object2ObjectOpenHashMap<>();

            for (RandomizeTradeRowInput tradeEntry : QueensTradeManager.QUEENS_TRADE_MANAGER.recipeViewerRandomizerTrades) {
                TagKey<Item> itemTagKey = tradeEntry.tagKey().get();
                TagData tagData = cacheJeiData.getOrDefault(itemTagKey, null);
                if (tagData == null) {
                    List<ItemStack> randomizeStack = tradeEntry.getWantItems().stream().map(e -> e.value().getDefaultInstance()).toList();
                    tagData = new TagData(randomizeStack.size(), randomizeStack, Ingredient.of(itemTagKey));
                    cacheJeiData.put(itemTagKey, tagData);
                }

                randomizerTrades.add(new JEIQueenRandomizerTradesInfo(
                        itemTagKey,
                        tagData.jeiIngredient(),
                        tagData.listSize(),
                        tagData.jeiItems()));
            }
        }
        registration.addRecipes(QUEEN_RANDOMIZE_TRADES, randomizerTrades);

        List<ItemStack> hangingGardensFlowers = GeneralUtils.convertBlockTagsToItemStacks(BzTags.HANGING_GARDEN_ALLOWED_FLOWERS_BLOCKS, BzTags.HANGING_GARDEN_FORCED_DISALLOWED_FLOWERS_BLOCKS);
        hangingGardensFlowers.addAll(GeneralUtils.convertBlockTagsToItemStacks(BzTags.HANGING_GARDEN_ALLOWED_TALL_FLOWERS_BLOCKS, BzTags.HANGING_GARDEN_FORCED_DISALLOWED_TALL_FLOWERS_BLOCKS));

        addComplexBlockTagInfo(registration,
                Pair.of(".hanging_gardens_flowers.description", hangingGardensFlowers),
                Pair.of(".crystalline_flower_can_be_placed_on.description",
                        GeneralUtils.convertBlockTagsToItemStacks(BzTags.CRYSTALLINE_FLOWER_CAN_SURVIVE_ON, null))
        );
    }

    @SafeVarargs
    private static void addComplexBlockTagInfo(@NotNull IRecipeRegistration registration, Pair<String, List<ItemStack>>... structureInfo) {
        for (Pair<String, List<ItemStack>> predicatePair : structureInfo) {
            registration.addIngredientInfo(
                    predicatePair.getSecond(),
                    VanillaTypes.ITEM_STACK,
                    Component.translatable(Bumblezone.MODID + predicatePair.getFirst()));
        }
    }

    record TagData(int listSize, List<ItemStack> jeiItems, Ingredient jeiIngredient){}

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new QueenTradesJEICategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new QueenRandomizeTradesJEICategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(BzItems.BEE_QUEEN_SPAWN_EGG.get().getDefaultInstance(), QUEEN_TRADES);
        registration.addRecipeCatalyst(BzItems.BEE_QUEEN_SPAWN_EGG.get().getDefaultInstance(), QUEEN_RANDOMIZE_TRADES);
    }
}
