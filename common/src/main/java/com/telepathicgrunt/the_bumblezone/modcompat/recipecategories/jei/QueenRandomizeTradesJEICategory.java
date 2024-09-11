package com.telepathicgrunt.the_bumblezone.modcompat.recipecategories.jei;

import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.modcompat.JEIIntegration;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class QueenRandomizeTradesJEICategory implements IRecipeCategory<JEIQueenRandomizerTradesInfo> {

    public static final int RECIPE_WIDTH = 136;
    public static final int RECIPE_HEIGHT = 28;

    private final IDrawable background;
    private final IDrawable icon;
    private final Component localizedName;
    private final IDrawable tagIcon;

    public QueenRandomizeTradesJEICategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "textures/gui/queen_randomizer_trades_layout.png"), 0, 0, RECIPE_WIDTH, RECIPE_HEIGHT);
        this.localizedName = Component.translatable("the_bumblezone.recipe_viewers.bee_queen_color_randomizing_trades");

        DrawableBuilder iconBuilder = new DrawableBuilder(ResourceLocation.fromNamespaceAndPath("the_bumblezone", "textures/gui/bee_queen_randomize_trades.png"), 0, 0, 16, 16);
        iconBuilder.setTextureSize(16, 16);
        this.icon = iconBuilder.build();

        DrawableBuilder tagIconBuilder = new DrawableBuilder(ResourceLocation.fromNamespaceAndPath("the_bumblezone", "textures/gui/tag_icon.png"), 0, 0, 16, 16);
        tagIconBuilder.setTextureSize(16, 16);
        this.tagIcon = tagIconBuilder.build();
    }

    @Override
    public RecipeType<JEIQueenRandomizerTradesInfo> getRecipeType() {
        return JEIIntegration.QUEEN_RANDOMIZE_TRADES;
    }

    @Override
    public Component getTitle() {
        return this.localizedName;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public void draw(JEIQueenRandomizerTradesInfo recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("the_bumblezone.recipe_viewers.queen_trade_colors", recipe.tagSize()), 86, 10, 0xFF808080, false);

        if (recipe.tagInAndOut() != null) {
            tagIcon.draw(guiGraphics, 11, 11);
        }

        if (recipe.tagInAndOut() != null) {
            tagIcon.draw(guiGraphics, 69, 11);
        }
    }

    @Override
    public List<Component> getTooltipStrings(JEIQueenRandomizerTradesInfo recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        return IRecipeCategory.super.getTooltipStrings(recipe, recipeSlotsView, mouseX, mouseY);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, JEIQueenRandomizerTradesInfo recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 6, 6).addIngredients(recipe.ingredientInAndOut());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 64, 6).addIngredients(recipe.ingredientInAndOut());
    }
}