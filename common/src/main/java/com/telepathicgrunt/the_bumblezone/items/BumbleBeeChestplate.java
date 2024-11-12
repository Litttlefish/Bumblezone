package com.telepathicgrunt.the_bumblezone.items;

import com.telepathicgrunt.the_bumblezone.client.LivingEntityFlyingSoundInstance;
import com.telepathicgrunt.the_bumblezone.items.datacomponents.BumbleBeeChestplateData;
import com.telepathicgrunt.the_bumblezone.mixin.gameplay.ServerGamePacketListenerImplAccessor;
import com.telepathicgrunt.the_bumblezone.mixin.entities.LivingEntityAccessor;
import com.telepathicgrunt.the_bumblezone.modinit.BzCriterias;
import com.telepathicgrunt.the_bumblezone.modinit.BzDataComponents;
import com.telepathicgrunt.the_bumblezone.modinit.BzEffects;
import com.telepathicgrunt.the_bumblezone.modinit.BzSounds;
import com.telepathicgrunt.the_bumblezone.modinit.BzStats;
import com.telepathicgrunt.the_bumblezone.packets.BumbleBeeChestplateFlyingPacket;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class BumbleBeeChestplate extends BeeArmor {

    public BumbleBeeChestplate(Holder<ArmorMaterial> material, ArmorItem.Type armorType, Properties properties, boolean transTexture, int variant) {
        super(material,
            armorType,
            properties.component(BzDataComponents.BUMBLEBEE_CHESTPLATE_DATA.get(), new BumbleBeeChestplateData()),
            variant,
            transTexture);
    }

    @Override
    public void verifyComponentsAfterLoad(ItemStack itemStack) {
        if (itemStack.get(BzDataComponents.BUMBLEBEE_CHESTPLATE_DATA.get()) == null) {
            itemStack.set(BzDataComponents.BUMBLEBEE_CHESTPLATE_DATA.get(), new BumbleBeeChestplateData());
        }
    }

    @Override
    public void bz$onArmorTick(ItemStack itemstack, Level world, Player player) {
        BumbleBeeChestplateData chestplateData = itemstack.getComponents().get(BzDataComponents.BUMBLEBEE_CHESTPLATE_DATA.get());
        if (chestplateData == null) {
            return;
        }

        boolean isFlying = chestplateData.isFlying();

        if (player.getCooldowns().isOnCooldown(itemstack.getItem())) {
            if (isFlying) {
                itemstack.set(BzDataComponents.BUMBLEBEE_CHESTPLATE_DATA.get(),
                        new BumbleBeeChestplateData(
                                false,
                                chestplateData.flyCounter(),
                                chestplateData.forcedMaxFlyingTickTime(),
                                chestplateData.requiredWearablesCountForForcedFlyingTime()));
            }
            return;
        }

        boolean finalIsFlying = chestplateData.isFlying();
        int finalFlyCounter = chestplateData.flyCounter();
        Optional<Integer> finalForcedMaxFlyingTickTime = chestplateData.forcedMaxFlyingTickTime();
        Optional<Integer> finalRequiredWearablesCountForForcedFlyingTime = chestplateData.requiredWearablesCountForForcedFlyingTime();

        int flyCounter = chestplateData.flyCounter();
        if (world.isClientSide()) {
            if (flyCounter > 0 && !player.onGround() && !player.isInWater() && ((LivingEntityAccessor)player).isJumping() && !player.getAbilities().flying && !player.isPassenger() && !player.onClimbable()) {
                if (!isFlying) {
                    LivingEntityFlyingSoundInstance.playSound(player, BzSounds.BUMBLE_BEE_CHESTPLATE_FLYING.get());
                    BumbleBeeChestplateFlyingPacket.sendToServer(true);
                    finalIsFlying = true;
                }
            }
            else if (isFlying) {
                LivingEntityFlyingSoundInstance.stopSound(player, BzSounds.BUMBLE_BEE_CHESTPLATE_FLYING.get());
                BumbleBeeChestplateFlyingPacket.sendToServer(false);
                finalIsFlying = false;
            }
        }

        int beeWearablesCount = BeeArmor.getBeeThemedWearablesCount(player);
        MobEffectInstance beenergized = player.getEffect(BzEffects.BEENERGIZED.holder());
        boolean isBeenergized = beenergized != null;

        isFlying = finalIsFlying;
        if (isFlying) {
            if (flyCounter > 0) {
                Vec3 velocity = player.getDeltaMovement();
                double additiveSpeed = velocity.y() > 0 ? velocity.y() > 0.1D ? 0.06D : 0.080D : 0.13D;
                if (isBeenergized) {
                    additiveSpeed += (beenergized.getAmplifier() + 1) * 0.0125D;
                }

                double newYSpeed = velocity.y() + additiveSpeed;
                player.setDeltaMovement(
                        velocity.x(),
                        newYSpeed,
                        velocity.z()
                );

                if (newYSpeed > -0.3) {
                    player.fallDistance = 0;
                }
                else if (newYSpeed <= -0.3) {
                    player.fallDistance = ((float) Math.abs(newYSpeed) / 0.3f) + 1.75f;
                }

                finalFlyCounter = flyCounter - 1;
                if (!world.isClientSide() && player.getRandom().nextFloat() < 0.0025f) {
                    itemstack.hurtAndBreak(1, player, EquipmentSlot.CHEST);
                }

                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.awardStat(BzStats.BUMBLE_BEE_CHESTPLATE_FLY_TIME_RL.get());
                    ((ServerGamePacketListenerImplAccessor)serverPlayer.connection).setAboveGroundTickCount(0);
                    ((ServerGamePacketListenerImplAccessor)serverPlayer.connection).setAboveGroundVehicleTickCount(0);
                }
            }
            else {
               finalIsFlying = false;
                if (beeWearablesCount >= 4 && player instanceof ServerPlayer serverPlayer) {
                    BzCriterias.BUMBLE_BEE_CHESTPLATE_MAX_FLIGHT_TRIGGER.get().trigger(serverPlayer);
                }
            }
        }

        if (player.onGround()) {
            if (chestplateData.forcedMaxFlyingTickTime().isPresent()) {
                if (chestplateData.requiredWearablesCountForForcedFlyingTime().isEmpty() || chestplateData.requiredWearablesCountForForcedFlyingTime().get() >= beeWearablesCount) {
                    finalFlyCounter = chestplateData.forcedMaxFlyingTickTime().get();
                }
                else {
                    finalFlyCounter = (int) (20 * (isBeenergized ? 1.5F : 1) * (((beeWearablesCount - 1) * 0.5F) + 1));
                }
            }
            else {
                finalFlyCounter = (int) (20 * (isBeenergized ? 1.5F : 1) * (((beeWearablesCount - 1) * 0.5F) + 1));
            }
        }

        if (chestplateData.isDifferent(finalIsFlying, finalFlyCounter, finalForcedMaxFlyingTickTime, finalRequiredWearablesCountForForcedFlyingTime)) {
            itemstack.set(BzDataComponents.BUMBLEBEE_CHESTPLATE_DATA.get(),
                    new BumbleBeeChestplateData(
                            finalIsFlying,
                            finalFlyCounter,
                            finalForcedMaxFlyingTickTime,
                            finalRequiredWearablesCountForForcedFlyingTime));
        }
    }

    public static ItemStack getEntityBeeChestplate(LivingEntity entity) {
        for (ItemStack armor : entity.getArmorSlots()) {
            if (armor.getItem() instanceof BumbleBeeChestplate) {
                return armor;
            }
        }
        return ItemStack.EMPTY;
    }
}