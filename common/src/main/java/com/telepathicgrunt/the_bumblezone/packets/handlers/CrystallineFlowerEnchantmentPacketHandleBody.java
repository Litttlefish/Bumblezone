package com.telepathicgrunt.the_bumblezone.packets.handlers;

import com.telepathicgrunt.the_bumblezone.client.screens.CrystallineFlowerScreen;
import com.telepathicgrunt.the_bumblezone.client.utils.GeneralUtilsClient;
import com.telepathicgrunt.the_bumblezone.menus.CrystallineFlowerMenu;
import com.telepathicgrunt.the_bumblezone.menus.EnchantmentSkeleton;
import com.telepathicgrunt.the_bumblezone.packets.CrystallineFlowerEnchantmentPacket;
import net.minecraft.locale.Language;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CrystallineFlowerEnchantmentPacketHandleBody {
    public static void handle(CrystallineFlowerEnchantmentPacket message) {
        if (GeneralUtilsClient.getClientPlayer() != null && GeneralUtilsClient.getClientPlayer().containerMenu.containerId == message.containerId()) {
            if (GeneralUtilsClient.getClientPlayer().containerMenu instanceof CrystallineFlowerMenu crystallineFlowerMenu) {
                Map<ResourceLocation, EnchantmentSkeleton> map = new HashMap<>();
                for (EnchantmentSkeleton enchantmentSkeleton : message.enchantmentSkeletons()) {
                    map.put(ResourceLocation.fromNamespaceAndPath(enchantmentSkeleton.namespace, enchantmentSkeleton.path), enchantmentSkeleton);
                }
                crystallineFlowerMenu.selectedEnchantment = null;
                CrystallineFlowerScreen.enchantmentsAvailable = map;

                Language language = Language.getInstance();
                CrystallineFlowerScreen.enchantmentsAvailableSortedList = map.keySet().stream().sorted((r1, r2) -> {
                    String s1 = language.getOrDefault("enchantment."+r1.getNamespace()+"."+r1.getPath(), r1.getPath().replace("_", " "));
                    String s2 = language.getOrDefault("enchantment."+r2.getNamespace()+"."+r2.getPath(), r2.getPath().replace("_", " "));
                    return s1.compareToIgnoreCase(s2);
                }).collect(Collectors.toList());

                crystallineFlowerMenu.selectedEnchantment = message.selectedResourceLocation().equals(ResourceLocation.fromNamespaceAndPath("minecraft", "empty")) ? null : message.selectedResourceLocation();
                if (!CrystallineFlowerScreen.enchantmentsAvailable.containsKey(crystallineFlowerMenu.selectedEnchantment)) {
                    crystallineFlowerMenu.selectedEnchantment = CrystallineFlowerScreen.enchantmentsAvailable.keySet().stream().findFirst().orElse(null);
                }
            }
        }
    }
}