package com.telepathicgrunt.the_bumblezone.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.blocks.CrystallineFlower;
import com.telepathicgrunt.the_bumblezone.client.utils.GeneralUtilsClient;
import com.telepathicgrunt.the_bumblezone.configs.BzGeneralConfigs;
import com.telepathicgrunt.the_bumblezone.menus.CrystallineFlowerMenu;
import com.telepathicgrunt.the_bumblezone.menus.EnchantmentSkeleton;
import com.telepathicgrunt.the_bumblezone.packets.CrystallineFlowerClickedEnchantmentButtonPacket;
import com.telepathicgrunt.the_bumblezone.platform.ModInfo;
import com.telepathicgrunt.the_bumblezone.utils.EnchantmentUtils;
import com.telepathicgrunt.the_bumblezone.utils.GeneralUtils;
import com.telepathicgrunt.the_bumblezone.utils.PlatformHooks;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CrystallineFlowerScreen extends AbstractContainerScreen<CrystallineFlowerMenu> {
    private static final ResourceLocation CONTAINER_BACKGROUND = ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "textures/gui/container/crystallized_flower.png");
    private static final Pattern SPLIT_WITH_COMBINING_CHARS = Pattern.compile("(\\p{M}+|\\P{M}\\p{M}*)"); // {M} is any kind of 'mark' http://stackoverflow.com/questions/29110887/detect-any-combining-character-in-java/29111105

    private static final int MENU_HEIGHT = 126;

    private static final int INVENTORY_LABEL_Y_OFFSET = -60;
    private static final int TITLE_LABEL_Y_OFFSET = -1;

    private static final int ENCHANTMENT_AREA_X_OFFSET = 76;
    private static final int ENCHANTMENT_AREA_Y_OFFSET = 52;
    private static final int ENCHANTMENT_SECTION_WIDTH = 88;
    private static final int ENCHANTMENT_SECTION_HEIGHT = 19;

    private static final int ENCHANTMENT_SCROLLBAR_X_OFFSET = 164;
    private static final int ENCHANTMENT_SCROLLBAR_Y_OFFSET = 50;
    private static final int ENCHANTMENT_SCROLLBAR_Y_RANGE = 50;
    private static final float ENCHANTMENT_SCROLLBAR_U_TEXTURE = 230.0F;
    private static final float ENCHANTMENT_SCROLLBAR_V_TEXTURE = 182.0F;

    private static final int ENCHANTMENT_SORT_X_OFFSET = 163;
    private static final int ENCHANTMENT_SORT_Y_OFFSET = 41;
    private static final float ENCHANTMENT_SORT_U_OFFSET = 92.0F;
    private static final float ENCHANTMENT_SORT_V_OFFSET = 197.0F;

    private static final float ENCHANTMENT_SELECTED_U_TEXTURE = 0F;
    private static final float ENCHANTMENT_SELECTED_V_TEXTURE = 197.0F;
    private static final float ENCHANTMENT_UNSELECTED_U_TEXTURE = 0F;
    private static final float ENCHANTMENT_UNSELECTED_V_TEXTURE = 216.0F;
    private static final float ENCHANTMENT_HIGHLIGHTED_U_TEXTURE = 0F;
    private static final float ENCHANTMENT_HIGHLIGHTED_V_TEXTURE = 235.0F;

    private static final int XP_BAR_X_OFFSET = 11;
    private static final int XP_BAR_Y_OFFSET = 99;
    private static final float XP_BAR_U_TEXTURE = 176.0F;
    private static final float XP_BAR_V_TEXTURE = 187.0F;

    private static final int XP_CONSUME_1_X_OFFSET = 46;
    private static final int XP_CONSUME_1_Y_OFFSET = 14;
    private static final int XP_CONSUME_2_X_OFFSET = 46;
    private static final int XP_CONSUME_2_Y_OFFSET = 34;
    private static final int XP_CONSUME_3_X_OFFSET = 46;
    private static final int XP_CONSUME_3_Y_OFFSET = 54;

    private static final float XP_CONSUME_1_U_OFFSET = 108.0F;
    private static final float XP_CONSUME_1_V_OFFSET = 197.0F;
    private static final float XP_CONSUME_2_U_OFFSET = 126.0F;
    private static final float XP_CONSUME_2_V_OFFSET = 197.0F;
    private static final float XP_CONSUME_3_U_OFFSET = 144.0F;
    private static final float XP_CONSUME_3_V_OFFSET = 197.0F;

    private static final int CONSUME_CONFIRMATION_X_OFFSET = 25;
    private static final int CONSUME_CONFIRMATION_Y_OFFSET = 62;
    private static final float CONSUME_CONFIRMATION_U_OFFSET = 162.0F;
    private static final float CONSUME_CONFIRMATION_V_OFFSET = 197.0F;
    private static final int CONSUME_ARROW_X_OFFSET = 26;
    private static final int CONSUME_ARROW_Y_OFFSET = 82;
    private static final float CONSUME_ARROW_U_OFFSET = 180.0F;
    private static final float CONSUME_ARROW_V_OFFSET = 197.0F;

    private static final int TIER_X_OFFSET = 11;
    private static final int TIER_Y_OFFSET = 15;
    private static final float TIER_FLOWER_U_TEXTURE = 195.0F;
    private static final float TIER_FLOWER_V_TEXTURE = 197.0F;
    private static final float TIER_BODY_U_TEXTURE = 195.0F;
    private static final float TIER_BODY_V_TEXTURE = 207.0F;
    private static final float TIER_BLOCK_U_TEXTURE = 195.0F;
    private static final float TIER_BLOCK_V_TEXTURE = 217.0F;

    private static final int BUTTON_PRESSED_TIMER_VISUAL = 25;

    private float scrollOff;
    private boolean scrolling;
    private int startIndex;
    private int pressedXp1Timer = 0;
    private int pressedXp2Timer = 0;
    private int pressedXp3Timer = 0;
    private int pressedConsumeTimer = 0;
    private int pressedSortTimer = 0;

    private List<Boolean> cachedObstructions = new ArrayList<>();
    private int cachedObstructionsTimer = 0;
    private int prevXpTier = 0;
    private boolean prevBookSlotEmpty = true;

    public static Map<ResourceLocation, EnchantmentSkeleton> enchantmentsAvailable = new HashMap<>();
    public static List<ResourceLocation> enchantmentsAvailableSortedList = new ArrayList<>();
    public static SORT_STATE sortState = SORT_STATE.ALPHABETICAL;

    public enum SORT_STATE {
        ALPHABETICAL(0, 0, "sort_alphabetically"),
        MODID(8, 0, "sort_namespace"),
        TREASURE_AND_CURSE(0, 24, "sort_treasure_curse"),
        LEVEL(8, 24, "sort_level");

        private static final SORT_STATE[] vals = values();
        private final int offsetU;
        private final int offsetV;
        private final String langKey;

        SORT_STATE(int offsetU, int offsetV, String langKey) {
            this.offsetU = offsetU;
            this.offsetV = offsetV;
            this.langKey = langKey;
        }

        public SORT_STATE next() {
            return vals[(this.ordinal() + 1) % vals.length];
        }
    }

    public CrystallineFlowerScreen(CrystallineFlowerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        inventoryLabelY = imageHeight + INVENTORY_LABEL_Y_OFFSET;
        titleLabelY += TITLE_LABEL_Y_OFFSET;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        RenderSystem.enableDepthTest();

        ItemStack book = this.menu.bookSlot.getItem();
        if (book.isEmpty() != prevBookSlotEmpty || this.menu.xpTier.get() != prevXpTier) {
            populateAvailableEnchants();
            prevXpTier = this.menu.xpTier.get();
            prevBookSlotEmpty = book.isEmpty();
        }

        int startX = (this.width - this.imageWidth) / 2;
        int startY = (this.height - this.imageHeight) / 2;
        final int rowStartX = startX + ENCHANTMENT_AREA_X_OFFSET;
        final int rowStartY = startY + ENCHANTMENT_AREA_Y_OFFSET;

        renderScroller(guiGraphics, startX + ENCHANTMENT_SCROLLBAR_X_OFFSET, startY + ENCHANTMENT_SCROLLBAR_Y_OFFSET);

        handleEnchantmentAreaRow(mouseX, mouseY,
            (Integer selectedIndex) -> {
                if (selectedIndex > enchantmentsAvailableSortedList.size()) {
                    return false;
                }

                ResourceLocation selectedEnchant = enchantmentsAvailableSortedList.get(selectedIndex);
                EnchantmentSkeleton enchantmentSkeleton = enchantmentsAvailable.get(selectedEnchant);
                boolean isCurse = enchantmentSkeleton.isCurse;
                boolean isTreasure = enchantmentSkeleton.isTreasure;
                int row = enchantmentsAvailableSortedList.indexOf(selectedEnchant) - this.startIndex;
                if (ResourceLocation.fromNamespaceAndPath(enchantmentSkeleton.namespace, enchantmentSkeleton.path).equals(this.menu.selectedEnchantment)) {
                    RenderSystem.enableDepthTest();
                    guiGraphics.blit(CONTAINER_BACKGROUND, rowStartX - 2, rowStartY - 2 + row * ENCHANTMENT_SECTION_HEIGHT, ENCHANTMENT_SELECTED_U_TEXTURE, ENCHANTMENT_SELECTED_V_TEXTURE, ENCHANTMENT_SECTION_WIDTH + 1, ENCHANTMENT_SECTION_HEIGHT, 256, 256);
                    drawEnchantmentText(
                            guiGraphics,
                            rowStartX,
                            rowStartY + row * ENCHANTMENT_SECTION_HEIGHT,
                            enchantmentSkeleton,
                            isCurse ? 0x990000 : isTreasure ? 0xFFF000 : 0xFFD000,
                            0xC0FF00,
                            true
                    );
                }
                else {
                    RenderSystem.enableDepthTest();
                    guiGraphics.blit(CONTAINER_BACKGROUND, rowStartX - 2, rowStartY - 2 + row * ENCHANTMENT_SECTION_HEIGHT, ENCHANTMENT_HIGHLIGHTED_U_TEXTURE, ENCHANTMENT_HIGHLIGHTED_V_TEXTURE, ENCHANTMENT_SECTION_WIDTH + 1, ENCHANTMENT_SECTION_HEIGHT, 256, 256);
                    drawEnchantmentText(
                            guiGraphics,
                            rowStartX,
                            rowStartY + row * 19,
                            enchantmentSkeleton,
                            isCurse ? 0x800000 : isTreasure ? 0xFFFF50 : 0x402020,
                            0x304000,
                            false
                    );
                }
                return true;
            },
            (Integer selectedIndex) -> {
                if (selectedIndex > enchantmentsAvailableSortedList.size()) {
                    return;
                }

                ResourceLocation selectedEnchant = enchantmentsAvailableSortedList.get(selectedIndex);
                EnchantmentSkeleton enchantmentEntry = enchantmentsAvailable.get(selectedEnchant);
                boolean isCurse = enchantmentEntry.isCurse;
                boolean isTreasure = enchantmentEntry.isTreasure;
                int row = selectedIndex - this.startIndex;
                if (selectedEnchant.equals(this.menu.selectedEnchantment)) {
                    RenderSystem.enableDepthTest();
                    guiGraphics.blit(CONTAINER_BACKGROUND, rowStartX - 2, rowStartY - 2 + row * ENCHANTMENT_SECTION_HEIGHT, ENCHANTMENT_SELECTED_U_TEXTURE, ENCHANTMENT_SELECTED_V_TEXTURE, ENCHANTMENT_SECTION_WIDTH + 1, ENCHANTMENT_SECTION_HEIGHT, 256, 256);
                    drawEnchantmentText(
                            guiGraphics,
                            rowStartX,
                            rowStartY + row * ENCHANTMENT_SECTION_HEIGHT,
                            enchantmentEntry,
                            isCurse ? 0x990000 : isTreasure ? 0xFFF000 : 0xFFD000,
                            0xC0FF00,
                            true
                    );
                }
                else {
                    RenderSystem.enableDepthTest();
                    guiGraphics.blit(CONTAINER_BACKGROUND, rowStartX - 2, rowStartY - 2 + row * ENCHANTMENT_SECTION_HEIGHT, ENCHANTMENT_UNSELECTED_U_TEXTURE, ENCHANTMENT_UNSELECTED_V_TEXTURE, ENCHANTMENT_SECTION_WIDTH + 1, ENCHANTMENT_SECTION_HEIGHT, 256, 256);
                    drawEnchantmentText(
                            guiGraphics,
                            rowStartX,
                            rowStartY + row * ENCHANTMENT_SECTION_HEIGHT,
                            enchantmentEntry,
                            isCurse ? 0xFF2000 : isTreasure ? 0xFFF000 : 0xD0B0F0,
                            0x00DD40,
                            true
                    );
                }
            });


        if (this.menu.tooManyEnchantmentsOnInput.get() == 1) {
            MutableComponent mutableComponent = Component.translatable("container.the_bumblezone.crystalline_flower.too_many_enchants").withStyle(ChatFormatting.BOLD);
            guiGraphics.drawCenteredString(font, mutableComponent, rowStartX + 45, rowStartY - 36, 0xD03010);
        }
        else if (this.menu.selectedEnchantment != null && this.menu.enchantedSlot.hasItem()) {
            EnchantmentSkeleton enchantment = enchantmentsAvailable.get(this.menu.selectedEnchantment);
            int tierCost = EnchantmentUtils.getEnchantmentTierCost(enchantment.level, enchantment.minCost, enchantment.isTreasure, enchantment.isCurse);
            MutableComponent mutableComponent = Component.translatable("container.the_bumblezone.crystalline_flower.tier_cost_arrow", tierCost).withStyle(ChatFormatting.BOLD);
            guiGraphics.drawCenteredString(font, mutableComponent, rowStartX + 45, rowStartY - 36, 0xD03010);
        }

        drawPushableButtons(guiGraphics, startX, startY, mouseX, mouseY);
        drawTierState(guiGraphics, startX, startY);
        renderXPBar(guiGraphics, startX, startY);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void drawTierState(GuiGraphics guiGraphics, int startX, int startY) {
        int xOffset = startX + TIER_X_OFFSET;
        int yOffset = startY + TIER_Y_OFFSET;

        if (cachedObstructionsTimer <= 0) {
            BlockPos flowerPos = new BlockPos(
                GeneralUtils.merge(this.menu.bottomBlockPosXUpper.get(), this.menu.bottomBlockPosXLower.get()),
                GeneralUtils.merge(this.menu.bottomBlockPosYUpper.get(), this.menu.bottomBlockPosYLower.get()),
                GeneralUtils.merge(this.menu.bottomBlockPosZUpper.get(), this.menu.bottomBlockPosZLower.get()));

            cachedObstructions = CrystallineFlower.getObstructions(7, this.minecraft.player.level(), flowerPos);
            cachedObstructionsTimer = 100;
        }
        cachedObstructionsTimer--;

        for (int i = 0; i < 7; i++) {
            if (i >= this.menu.xpTier.get()) {
                if (i < cachedObstructions.size() && cachedObstructions.get(i)) {
                    RenderSystem.enableDepthTest();
                    guiGraphics.blit(CONTAINER_BACKGROUND, xOffset, yOffset + (72 - (i * 12)), TIER_BLOCK_U_TEXTURE, TIER_BLOCK_V_TEXTURE, 10, 10, 256, 256);
                }
                continue;
            }

            float textureU = TIER_BODY_U_TEXTURE;
            float textureV = TIER_BODY_V_TEXTURE;
            if (i + 1 == this.menu.xpTier.get()) {
                textureU = TIER_FLOWER_U_TEXTURE;
                textureV = TIER_FLOWER_V_TEXTURE;
            }
            RenderSystem.enableDepthTest();
            guiGraphics.blit(CONTAINER_BACKGROUND, xOffset, yOffset + (72 - (i * 12)), textureU, textureV, 10, 10, 256, 256);
        }
    }

    private void drawPushableButtons(GuiGraphics guiGraphics, int startX, int startY, int mouseX, int mouseY) {

        if (pressedSortTimer > 0) {
            pressedSortTimer--;
            RenderSystem.enableDepthTest();
            guiGraphics.blit(CONTAINER_BACKGROUND, startX + ENCHANTMENT_SORT_X_OFFSET, startY + ENCHANTMENT_SORT_Y_OFFSET, ENCHANTMENT_SORT_U_OFFSET + sortState.offsetU, ENCHANTMENT_SORT_V_OFFSET + sortState.offsetV + 8, 8, 8, 256, 256);
        }
        else {
            int xOffset = startX + ENCHANTMENT_SORT_X_OFFSET;
            int yOffset = startY + ENCHANTMENT_SORT_Y_OFFSET;
            if (mouseX - xOffset >= 0.0D && mouseX - xOffset < 8.0D && mouseY - yOffset >= 0.0D && mouseY - yOffset < 8.0D) {
                RenderSystem.enableDepthTest();
                guiGraphics.blit(CONTAINER_BACKGROUND, xOffset, yOffset, ENCHANTMENT_SORT_U_OFFSET + sortState.offsetU, ENCHANTMENT_SORT_V_OFFSET + sortState.offsetV + 16, 8, 8, 256, 256);
            }
            else {
                RenderSystem.enableDepthTest();
                guiGraphics.blit(CONTAINER_BACKGROUND, xOffset, yOffset, ENCHANTMENT_SORT_U_OFFSET + sortState.offsetU, ENCHANTMENT_SORT_V_OFFSET + sortState.offsetV, 8, 8, 256, 256);
            }
        }

        if (BzGeneralConfigs.crystallineFlowerConsumeExperienceUI) {
            if (pressedXp1Timer > 0 ||
                    this.menu.xpTier.get() == 7 ||
                    isPathObstructed(1) ||
                    !canPlayerBuyTier(1)) {
                pressedXp1Timer--;
                RenderSystem.enableDepthTest();
                guiGraphics.blit(CONTAINER_BACKGROUND, startX + XP_CONSUME_1_X_OFFSET, startY + XP_CONSUME_1_Y_OFFSET, XP_CONSUME_1_U_OFFSET, XP_CONSUME_1_V_OFFSET + 18, 18, 18, 256, 256);
            }
            else {
                int xOffset = startX + XP_CONSUME_1_X_OFFSET;
                int yOffset = startY + XP_CONSUME_1_Y_OFFSET;
                if (mouseX - xOffset >= 0.0D && mouseX - xOffset < 18.0D && mouseY - yOffset >= 0.0D && mouseY - yOffset < 18.0D) {
                    RenderSystem.enableDepthTest();
                    guiGraphics.blit(CONTAINER_BACKGROUND, xOffset, yOffset, XP_CONSUME_1_U_OFFSET, XP_CONSUME_1_V_OFFSET + 36, 18, 18, 256, 256);
                }
                else {
                    RenderSystem.enableDepthTest();
                    guiGraphics.blit(CONTAINER_BACKGROUND, xOffset, yOffset, XP_CONSUME_1_U_OFFSET, XP_CONSUME_1_V_OFFSET, 18, 18, 256, 256);
                }
            }
        }

        if (BzGeneralConfigs.crystallineFlowerConsumeExperienceUI) {
            if (pressedXp2Timer > 0 ||
                    this.menu.xpTier.get() == 7 ||
                    isPathObstructed(2) ||
                    !canPlayerBuyTier(2)) {
                pressedXp2Timer--;
                RenderSystem.enableDepthTest();
                guiGraphics.blit(CONTAINER_BACKGROUND, startX + XP_CONSUME_2_X_OFFSET, startY + XP_CONSUME_2_Y_OFFSET, XP_CONSUME_2_U_OFFSET, XP_CONSUME_2_V_OFFSET + 18, 18, 18, 256, 256);
            }
            else {
                int xOffset = startX + XP_CONSUME_2_X_OFFSET;
                int yOffset = startY + XP_CONSUME_2_Y_OFFSET;
                if (mouseX - xOffset >= 0.0D && mouseX - xOffset < 18.0D && mouseY - yOffset >= 0.0D && mouseY - yOffset < 18.0D) {
                    RenderSystem.enableDepthTest();
                    guiGraphics.blit(CONTAINER_BACKGROUND, xOffset, yOffset, XP_CONSUME_2_U_OFFSET, XP_CONSUME_2_V_OFFSET + 36, 18, 18, 256, 256);
                }
                else {
                    RenderSystem.enableDepthTest();
                    guiGraphics.blit(CONTAINER_BACKGROUND, xOffset, yOffset, XP_CONSUME_2_U_OFFSET, XP_CONSUME_2_V_OFFSET, 18, 18, 256, 256);
                }
            }
        }

        if (BzGeneralConfigs.crystallineFlowerConsumeExperienceUI) {
            if (pressedXp3Timer > 0 ||
                    this.menu.xpTier.get() == 7 ||
                    isPathObstructed(3) ||
                    !canPlayerBuyTier(3))
            {
                pressedXp3Timer--;
                RenderSystem.enableDepthTest();
                guiGraphics.blit(CONTAINER_BACKGROUND, startX + XP_CONSUME_3_X_OFFSET, startY + XP_CONSUME_3_Y_OFFSET, XP_CONSUME_3_U_OFFSET, XP_CONSUME_3_V_OFFSET + 18, 18, 18, 256, 256);
            }
            else {
                int xOffset = startX + XP_CONSUME_3_X_OFFSET;
                int yOffset = startY + XP_CONSUME_3_Y_OFFSET;
                if (mouseX - xOffset >= 0.0D && mouseX - xOffset < 18.0D && mouseY - yOffset >= 0.0D && mouseY - yOffset < 18.0D) {
                    RenderSystem.enableDepthTest();
                    guiGraphics.blit(CONTAINER_BACKGROUND, xOffset, yOffset, XP_CONSUME_3_U_OFFSET, XP_CONSUME_3_V_OFFSET + 36, 18, 18, 256, 256);
                }
                else {
                    RenderSystem.enableDepthTest();
                    guiGraphics.blit(CONTAINER_BACKGROUND, xOffset, yOffset, XP_CONSUME_3_U_OFFSET, XP_CONSUME_3_V_OFFSET, 18, 18, 256, 256);
                }
            }
        }

        if (!BzGeneralConfigs.crystallineFlowerConsumeExperienceUI) {
            int xOffset = startX + 26;
            int yOffset = startY + 14;
            RenderSystem.enableDepthTest();
            guiGraphics.blit(CONTAINER_BACKGROUND, xOffset, yOffset, 176, 0, 48, 58, 256, 256);
        }

        if (pressedConsumeTimer > 0) {
            pressedConsumeTimer--;
        }
        if (this.menu.consumeSlotFullyObstructed.get() != 1 && BzGeneralConfigs.crystallineFlowerConsumeItemUI) {
            if (pressedConsumeTimer > 0) {
                RenderSystem.enableDepthTest();
                guiGraphics.blit(CONTAINER_BACKGROUND, startX + CONSUME_CONFIRMATION_X_OFFSET, startY + CONSUME_CONFIRMATION_Y_OFFSET, CONSUME_CONFIRMATION_U_OFFSET, CONSUME_CONFIRMATION_V_OFFSET + 18, 18, 18, 256, 256);
                guiGraphics.blit(CONTAINER_BACKGROUND, startX + CONSUME_ARROW_X_OFFSET, startY + CONSUME_ARROW_Y_OFFSET, CONSUME_ARROW_U_OFFSET, CONSUME_ARROW_V_OFFSET + 18, 15, 11, 256, 256);
            }
            else if (this.menu.consumeSlot.hasItem() && this.menu.xpTier.get() < 7) {
                int xOffset = startX + CONSUME_CONFIRMATION_X_OFFSET;
                int yOffset = startY + CONSUME_CONFIRMATION_Y_OFFSET;
                if (mouseX - xOffset >= 0.0D && mouseX - xOffset < 18.0D && mouseY - yOffset >= 0.0D && mouseY - yOffset < 18.0D) {
                    RenderSystem.enableDepthTest();
                    guiGraphics.blit(CONTAINER_BACKGROUND, startX + CONSUME_CONFIRMATION_X_OFFSET, startY + CONSUME_CONFIRMATION_Y_OFFSET, CONSUME_CONFIRMATION_U_OFFSET, CONSUME_CONFIRMATION_V_OFFSET + 36, 18, 18, 256, 256);
                }
                else {
                    RenderSystem.enableDepthTest();
                    guiGraphics.blit(CONTAINER_BACKGROUND, startX + CONSUME_CONFIRMATION_X_OFFSET, startY + CONSUME_CONFIRMATION_Y_OFFSET, CONSUME_CONFIRMATION_U_OFFSET, CONSUME_CONFIRMATION_V_OFFSET, 18, 18, 256, 256);
                }
                RenderSystem.enableDepthTest();
                guiGraphics.blit(CONTAINER_BACKGROUND, startX + CONSUME_ARROW_X_OFFSET, startY + CONSUME_ARROW_Y_OFFSET, CONSUME_ARROW_U_OFFSET, CONSUME_ARROW_V_OFFSET, 15, 11, 256, 256);
            }
        }

        if (!BzGeneralConfigs.crystallineFlowerConsumeItemUI) {
            int xOffset = startX + 26;
            int yOffset = startY + 78;
            RenderSystem.enableDepthTest();
            guiGraphics.blit(CONTAINER_BACKGROUND, xOffset, yOffset, 176, 59, 48, 19, 256, 256);
        }
    }

    private void drawEnchantmentText(GuiGraphics guiGraphics, int rowStartX, int currentRowStartY, EnchantmentSkeleton enchantmentEntry, int enchantmentNameColor, int enchantmentLevelColor, boolean shadow) {
        String translatedEnchantmentName = getTruncatedString(
                enchantmentEntry.namespace,
                enchantmentEntry.path,
                88);

        MutableComponent mutableComponent = Component.literal(translatedEnchantmentName);
        MutableComponent mutableComponent2 = Component.translatable("container.the_bumblezone.crystalline_flower.level", enchantmentEntry.level);
        if (enchantmentEntry.isMaxLevel) {
            mutableComponent2.append(Component.translatable("container.the_bumblezone.crystalline_flower.level_star"));
        }

        guiGraphics.drawString(this.font, mutableComponent, rowStartX, currentRowStartY, enchantmentNameColor, shadow);
        guiGraphics.drawString(this.font, mutableComponent2, rowStartX + 5, currentRowStartY + 8, enchantmentLevelColor, shadow);
    }

    @NotNull
    private String getTruncatedString(String namespace, String path, int maxSize) {
        StringBuilder translatedEnchantmentName = new StringBuilder(Language.getInstance().getOrDefault("""
                enchantment.%s.%s""".formatted(namespace, path)));

        String originalNameOutput = translatedEnchantmentName.toString();
        if (originalNameOutput.contains("enchantment.")) {
            translatedEnchantmentName = new StringBuilder(Arrays.stream(path
                    .split("_"))
                    .map(word -> word.substring(0, 1).toUpperCase(Locale.ROOT) + word.substring(1).toLowerCase(Locale.ROOT))
                    .collect(Collectors.joining(" ")));
        }

        boolean hasTruncated = false;
        while (font.width(translatedEnchantmentName.toString()) > maxSize) {
            int nameLength = translatedEnchantmentName.length();
            if (hasTruncated) {
                translatedEnchantmentName.delete(nameLength - 3, nameLength);
            }
            nameLength = translatedEnchantmentName.length();

            Matcher matcher = SPLIT_WITH_COMBINING_CHARS.matcher(translatedEnchantmentName);
            if (matcher.find()) {
                List<MatchResult> matchResults = matcher.results().toList();
                MatchResult match = matchResults.get(matchResults.size() - 1);
                String lastCharacter = match.group();
                if (translatedEnchantmentName.toString().endsWith(lastCharacter)) {
                    translatedEnchantmentName.delete(nameLength - lastCharacter.length(), nameLength);
                }
            }
            else {
                break;
            }

            translatedEnchantmentName.append("...");
            hasTruncated = true;
        }
        return translatedEnchantmentName.toString();
    }

    private void populateAvailableEnchants() {
        ItemStack book = this.menu.bookSlot.getItem();
        if (!book.isEmpty() && this.menu.xpTier.get() > 1 && this.menu.tooManyEnchantmentsOnInput.get() != 1) {
            ItemStack tempBook = book.copy();
            tempBook.setCount(1);
        }
        else {
            enchantmentsAvailable.clear();
            enchantmentsAvailableSortedList.clear();
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialtick, int x, int y) {
        int startX = (width - imageWidth) / 2;
        int startY = (height - imageHeight) / 2;
        RenderSystem.enableDepthTest();
        guiGraphics.blit(CONTAINER_BACKGROUND, startX, startY, 0, 0, imageWidth, MENU_HEIGHT);
        guiGraphics.blit(CONTAINER_BACKGROUND, startX, startY + MENU_HEIGHT, 0, 126, imageWidth, 71);
    }

    private void renderXPBar(GuiGraphics guiGraphics, int startX, int startY) {
        if (this.menu.xpTier.get() == 7) {
            RenderSystem.enableDepthTest();
            guiGraphics.blit(CONTAINER_BACKGROUND, startX + XP_BAR_X_OFFSET, startY + XP_BAR_Y_OFFSET, XP_BAR_U_TEXTURE, XP_BAR_V_TEXTURE - 5, 54, 5, 256, 256);
        }
        else {
            RenderSystem.enableDepthTest();
            guiGraphics.blit(CONTAINER_BACKGROUND, startX + XP_BAR_X_OFFSET, startY + XP_BAR_Y_OFFSET, XP_BAR_U_TEXTURE, XP_BAR_V_TEXTURE, 54, 5, 256, 256);
            if (this.menu.xpBarPercent.get() > 0) {
                RenderSystem.enableDepthTest();
                guiGraphics.blit(CONTAINER_BACKGROUND, startX + XP_BAR_X_OFFSET, startY + XP_BAR_Y_OFFSET, XP_BAR_U_TEXTURE, XP_BAR_V_TEXTURE + 5, (int) (54 * (this.menu.xpBarPercent.get() / 100f)), 5, 256, 256);
            }
        }
    }

    private void renderScroller(GuiGraphics guiGraphics, int posX, int posY) {
        int rowCount = enchantmentsAvailableSortedList.size() + 1 - 3;
        if (rowCount > 1) {
            if (startIndex > rowCount) {
                scrollOff = 1.0F;
            }
            startIndex = (int)((double)(this.scrollOff * (float)this.getOffscreenRows()) + 0.5D);
            int scrollPosition = (int) (scrollOff * 42);
            RenderSystem.enableDepthTest();
            guiGraphics.blit(CONTAINER_BACKGROUND, posX, posY + scrollPosition, ENCHANTMENT_SCROLLBAR_U_TEXTURE, ENCHANTMENT_SCROLLBAR_V_TEXTURE, 6, 17, 256, 256);
        }
        else {
            RenderSystem.enableDepthTest();
            guiGraphics.blit(CONTAINER_BACKGROUND, posX, posY, ENCHANTMENT_SCROLLBAR_U_TEXTURE + 6.0F, ENCHANTMENT_SCROLLBAR_V_TEXTURE, 6, 17, 256, 256);
        }
    }

    private boolean canScroll(int numOffers) {
        return numOffers > 3;
    }

    private boolean handleEnchantmentAreaRow(double mouseX, double mouseY, Function<Integer, Boolean> targetedSectionTask, Consumer<Integer> untargetedSectionTask) {
        int startX = this.leftPos + ENCHANTMENT_AREA_X_OFFSET - 2;
        int startY = this.topPos + ENCHANTMENT_AREA_Y_OFFSET - 2;
        int selectableSections = this.startIndex + Math.min(enchantmentsAvailableSortedList.size(), 3);
        boolean targetedSectionTaskSuccess = false;
        for(int currentSection = this.startIndex; currentSection < selectableSections; ++currentSection) {
            int sectionOffset = currentSection - this.startIndex;
            double sectionMouseX = mouseX - (double)(startX);
            double sectionMouseY = mouseY - (double)(startY + sectionOffset * ENCHANTMENT_SECTION_HEIGHT);
            if (sectionMouseX >= 0.0D && sectionMouseX < ENCHANTMENT_SECTION_WIDTH && sectionMouseY >= 0.0D && sectionMouseY < ENCHANTMENT_SECTION_HEIGHT) {
                targetedSectionTaskSuccess = targetedSectionTask.apply(currentSection);
            }
            else {
                untargetedSectionTask.accept(currentSection);
            }
        }
        return targetedSectionTaskSuccess;
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);

        // Sort button
        int xOffset = this.leftPos + ENCHANTMENT_SORT_X_OFFSET;
        int yOffset = this.topPos + ENCHANTMENT_SORT_Y_OFFSET;
        if (x - xOffset >= 0.0D && x - xOffset < 8.0D && y - yOffset >= 0.0D && y - yOffset < 8.0D) {
            guiGraphics.renderTooltip(
                    this.font,
                    List.of(Component.translatable("container.the_bumblezone.crystalline_flower." + sortState.langKey)),
                    Optional.empty(),
                    x,
                    y);
            return;
        }

        // Enchantment rows:
        int startX = this.leftPos + ENCHANTMENT_AREA_X_OFFSET - 2;
        int startY = this.topPos + ENCHANTMENT_AREA_Y_OFFSET - 2;
        int selectableSections = this.startIndex + Math.min(enchantmentsAvailableSortedList.size(), 3);
        for(int currentSection = this.startIndex; currentSection < selectableSections; ++currentSection) {
            if (currentSection >= enchantmentsAvailableSortedList.size()) continue;

            int sectionOffset = currentSection - this.startIndex;
            double sectionMouseX = x - (double)(startX);
            double sectionMouseY = y - (double)(startY + sectionOffset * ENCHANTMENT_SECTION_HEIGHT);
            if (sectionMouseX >= 0.0D && sectionMouseX < ENCHANTMENT_SECTION_WIDTH && sectionMouseY >= 0.0D && sectionMouseY < ENCHANTMENT_SECTION_HEIGHT) {
                EnchantmentSkeleton enchantment = enchantmentsAvailable.get(enchantmentsAvailableSortedList.get(currentSection));
                int tierCost = EnchantmentUtils.getEnchantmentTierCost(enchantment.level, enchantment.minCost, enchantment.isTreasure, enchantment.isCurse);

                String translatedEnchantmentName = Language.getInstance().getOrDefault("""
                    enchantment.%s.%s""".formatted(enchantment.namespace, enchantment.path));

                if (translatedEnchantmentName.contains("enchantment.")) {
                    translatedEnchantmentName = Arrays.stream(enchantment.path
                            .split("_"))
                            .map(word -> word.substring(0, 1).toUpperCase(Locale.ROOT) + word.substring(1).toLowerCase(Locale.ROOT))
                            .collect(Collectors.joining(" "));
                }

                MutableComponent mutableComponent = Component.literal(translatedEnchantmentName)
                        .withStyle(ChatFormatting.GOLD);

                MutableComponent mutableComponent2 = Component.translatable("container.the_bumblezone.crystalline_flower.level", enchantment.level)
                        .withStyle(ChatFormatting.GREEN);

                if (enchantment.isMaxLevel) {
                    mutableComponent2.append(Component.translatable("container.the_bumblezone.crystalline_flower.level_star"));
                }

                MutableComponent mutableComponent3 = Component.translatable("container.the_bumblezone.crystalline_flower.tier_cost", tierCost)
                        .withStyle(ChatFormatting.RED);

                MutableComponent mutableComponent4;

                if (GeneralUtilsClient.isAdvancedToolTipActive()) {
                    mutableComponent4 = Component.literal(enchantment.namespace + ":" + enchantment.path)
                            .withStyle(ChatFormatting.DARK_GRAY);
                }
                else {
                    ModInfo info = PlatformHooks.getModInfo(enchantment.namespace);
                    if (info == null) {
                        String formattedModid = Arrays.stream(enchantment.namespace
                                .split("_"))
                                .map(word -> word.substring(0, 1).toUpperCase(Locale.ROOT) + word.substring(1).toLowerCase(Locale.ROOT))
                                .collect(Collectors.joining(" "));

                        mutableComponent4 = Component.literal(formattedModid)
                                .withStyle(ChatFormatting.BLUE);
                    }
                    else {
                        mutableComponent4 = Component.literal(info.displayName())
                                .withStyle(ChatFormatting.BLUE)
                                .withStyle(ChatFormatting.ITALIC);
                    }
                }

                guiGraphics.renderTooltip(
                        this.font,
                        List.of(mutableComponent, mutableComponent2, mutableComponent3, mutableComponent4),
                        Optional.empty(),
                        x,
                        y);
                return;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.scrolling = false;

        if (handleEnchantmentAreaRow(mouseX, mouseY, (Integer sectionId) -> {
            if (this.menu.clickMenuEnchantment(this.minecraft.player, CrystallineFlowerScreen.enchantmentsAvailableSortedList.get(sectionId))) {
                sendButtonPressToMenu(sectionId);
                return true;
            }
            return false;
        }, (i) -> {})) {
            return true;
        }

        int startY;
        int startX;

        startX = this.leftPos + ENCHANTMENT_SCROLLBAR_X_OFFSET;
        startY = this.topPos + ENCHANTMENT_SCROLLBAR_Y_OFFSET;
        if (mouseX >= startX &&
            mouseX < startX + 6 &&
            mouseY >= startY &&
            mouseY < startY + ENCHANTMENT_SCROLLBAR_Y_RANGE)
        {
            this.scrolling = true;
        }

        if (mouseX >= this.leftPos + ENCHANTMENT_SORT_X_OFFSET &&
            mouseX < this.leftPos + ENCHANTMENT_SORT_X_OFFSET + 8 &&
            mouseY >= this.topPos + ENCHANTMENT_SORT_Y_OFFSET &&
            mouseY < this.topPos + ENCHANTMENT_SORT_Y_OFFSET + 8)
        {
            pressedSortTimer = BUTTON_PRESSED_TIMER_VISUAL;
            sortState = sortState.next();
            SortAndAssignAvailableEnchants();
            startIndex = 0;
            scrollOff = 0;
        }

        if (this.menu.xpTier.get() != 7)
        {
            if (BzGeneralConfigs.crystallineFlowerConsumeExperienceUI &&
                canPlayerBuyTier(1) &&
                !isPathObstructed(1) &&
                mouseX >= this.leftPos + XP_CONSUME_1_X_OFFSET &&
                mouseX < this.leftPos + XP_CONSUME_1_X_OFFSET + 18 &&
                mouseY >= this.topPos + XP_CONSUME_1_Y_OFFSET &&
                mouseY < this.topPos + XP_CONSUME_1_Y_OFFSET + 18)
            {
                pressedXp1Timer = BUTTON_PRESSED_TIMER_VISUAL;
                sendButtonPressToMenu(-2);
            }
            else if (BzGeneralConfigs.crystallineFlowerConsumeExperienceUI &&
                    canPlayerBuyTier(2) &&
                    !isPathObstructed(2) &&
                    mouseX >= this.leftPos + XP_CONSUME_2_X_OFFSET &&
                    mouseX < this.leftPos + XP_CONSUME_2_X_OFFSET + 18 &&
                    mouseY >= this.topPos + XP_CONSUME_2_Y_OFFSET &&
                    mouseY < this.topPos + XP_CONSUME_2_Y_OFFSET + 18)
            {
                pressedXp2Timer = BUTTON_PRESSED_TIMER_VISUAL;
                sendButtonPressToMenu(-3);
            }
            else if (BzGeneralConfigs.crystallineFlowerConsumeExperienceUI &&
                    canPlayerBuyTier(3) &&
                    !isPathObstructed(3) &&
                    mouseX >= this.leftPos + XP_CONSUME_3_X_OFFSET &&
                    mouseX < this.leftPos + XP_CONSUME_3_X_OFFSET + 18 &&
                    mouseY >= this.topPos + XP_CONSUME_3_Y_OFFSET &&
                    mouseY < this.topPos + XP_CONSUME_3_Y_OFFSET + 18)
            {
                pressedXp3Timer = BUTTON_PRESSED_TIMER_VISUAL;
                sendButtonPressToMenu(-4);
            }
            else if (BzGeneralConfigs.crystallineFlowerConsumeItemUI &&
                    this.menu.consumeSlotFullyObstructed.get() != 1 &&
                    mouseX >= this.leftPos + CONSUME_CONFIRMATION_X_OFFSET &&
                    mouseX < this.leftPos + CONSUME_CONFIRMATION_X_OFFSET + 18 &&
                    mouseY >= this.topPos + CONSUME_CONFIRMATION_Y_OFFSET &&
                    mouseY < this.topPos + CONSUME_CONFIRMATION_Y_OFFSET + 18)
            {
                pressedConsumeTimer = BUTTON_PRESSED_TIMER_VISUAL;
                sendButtonPressToMenu(-5);
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private Boolean canPlayerBuyTier(int xpTiersToCheck) {
        return xpTiersToCheck <= this.menu.playerHasXPForTier.get();
    }

    private Boolean isPathObstructed(int xpTiersToCheck) {
        for (int i = 0; i < xpTiersToCheck; i++) {
            if (this.menu.xpTier.get() + i < cachedObstructions.size() &&
                cachedObstructions.get(this.menu.xpTier.get() + i))
            {
                return true;
            }
        }

        return false;
    }

    private void sendButtonPressToMenu(Integer sectionId) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
        if (sectionId >= 0 && sectionId < enchantmentsAvailableSortedList.size()) {
            CrystallineFlowerClickedEnchantmentButtonPacket.sendToServer(this.menu.containerId, enchantmentsAvailableSortedList.get(sectionId));
        }
        else {
            this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, sectionId);
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.scrolling && canScroll(enchantmentsAvailableSortedList.size())) {
            int topY = this.topPos + ENCHANTMENT_SCROLLBAR_Y_OFFSET;
            int bottomY = topY + ENCHANTMENT_SCROLLBAR_Y_RANGE;
            this.scrollOff = ((float)mouseY - (float)topY - 7.5F) / ((float)(bottomY - topY) - 15.0F);
            this.scrollOff = Mth.clamp(this.scrollOff, 0.0F, 1.0F);
            this.startIndex = (int)((double)(this.scrollOff * (float)this.getOffscreenRows()) + 0.5D);
            return true;
        }
        else {
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double delta) {
        if (canScroll(enchantmentsAvailableSortedList.size())) {
            int offscreenRows = this.getOffscreenRows();
            float percentage = (float)delta / (float)offscreenRows;
            this.scrollOff = Mth.clamp(this.scrollOff - percentage, 0.0F, 1.0F);
            this.startIndex = (int)((double)(this.scrollOff * (float)offscreenRows) + 0.5D);
        }

        return true;
    }

    protected int getOffscreenRows() {
        return Math.max(enchantmentsAvailableSortedList.size() - 3, 0);
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int button) {
        return mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.imageWidth) || mouseY >= (double)(top + this.imageHeight + 32);
    }

    @Override
    public void onClose() {
        CrystallineFlowerScreen.enchantmentsAvailable.clear();
        CrystallineFlowerScreen.enchantmentsAvailableSortedList.clear();
        super.onClose();
    }

    private static final Comparator<Map.Entry<ResourceLocation, EnchantmentSkeleton>> compareByNamespace = Comparator.comparing(e -> e.getKey().getNamespace());
    private static final Comparator<Map.Entry<ResourceLocation, EnchantmentSkeleton>> compareByLang = Comparator.comparing(e ->
        Language.getInstance().getOrDefault(Util.makeDescriptionId("enchantment", e.getKey()), e.getKey().getPath().replace("_", " ")),
        String.CASE_INSENSITIVE_ORDER);
    private static final Comparator<Map.Entry<ResourceLocation, EnchantmentSkeleton>> compareByLevel = Comparator.comparingInt(e -> e.getValue().level);
    private static final Comparator<Map.Entry<ResourceLocation, EnchantmentSkeleton>> compareByTreasure = Comparator.comparing(e -> !(e.getValue().isTreasure && !e.getValue().isCurse)); // reverse it so treasures are first
    private static final Comparator<Map.Entry<ResourceLocation, EnchantmentSkeleton>> compareByCurse = Comparator.comparing(e -> !e.getValue().isCurse); // reverse it so curses are first
    private static final Comparator<Map.Entry<ResourceLocation, EnchantmentSkeleton>> compareByNamespaceAndLang = compareByNamespace.thenComparing(compareByLang);
    private static final Comparator<Map.Entry<ResourceLocation, EnchantmentSkeleton>> compareByLevelAndLang = compareByLevel.reversed().thenComparing(compareByLang);
    private static final Comparator<Map.Entry<ResourceLocation, EnchantmentSkeleton>> compareByTreasureCurseAndLang = compareByTreasure.thenComparing(compareByCurse).thenComparing(compareByLang);

    public static void SortAndAssignAvailableEnchants() {
        enchantmentsAvailableSortedList = enchantmentsAvailable.entrySet().stream().sorted((e1, e2) -> {
            switch (sortState){
                case MODID -> {
                    return compareByNamespaceAndLang.compare(e1, e2);
                }
                case TREASURE_AND_CURSE -> {
                    return compareByTreasureCurseAndLang.compare(e1, e2);
                }
                case LEVEL -> {
                    return compareByLevelAndLang.compare(e1, e2);
                }
                default -> {
                    return compareByLang.compare(e1, e2);
                }
            }
        })
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
    }
}