package com.telepathicgrunt.the_bumblezone.entities;

import com.telepathicgrunt.the_bumblezone.configs.BzBeeAggressionConfigs;
import com.telepathicgrunt.the_bumblezone.effects.WrathOfTheHiveEffect;
import com.telepathicgrunt.the_bumblezone.events.player.BzPlayerEntityInteractEvent;
import com.telepathicgrunt.the_bumblezone.items.PollenPuff;
import com.telepathicgrunt.the_bumblezone.items.StinglessBeeHelmet;
import com.telepathicgrunt.the_bumblezone.mixin.entities.BeeEntityInvoker;
import com.telepathicgrunt.the_bumblezone.modinit.BzCriterias;
import com.telepathicgrunt.the_bumblezone.modinit.BzEffects;
import com.telepathicgrunt.the_bumblezone.modinit.BzItems;
import com.telepathicgrunt.the_bumblezone.modinit.BzParticles;
import com.telepathicgrunt.the_bumblezone.modinit.BzTags;
import com.telepathicgrunt.the_bumblezone.modules.PlayerDataHandler;
import com.telepathicgrunt.the_bumblezone.utils.GeneralUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class BeeInteractivity {

    @Nullable
    public static InteractionResult onEntityInteractEvent(@Nullable InteractionResult result, BzPlayerEntityInteractEvent event) {
        Entity entity = event.entity();
        Player player = event.player();
        InteractionHand hand = event.hand();
        if (player == null || entity == null || result != null) {
            return null;
        }

        if(entity instanceof Bee beeEntity) {
            if(BeeInteractivity.beeFeeding(entity.level(), player, hand, beeEntity) == InteractionResult.SUCCESS) {
                return InteractionResult.SUCCESS;
            }
            else if(StinglessBeeHelmet.addBeePassenger(entity.level(), player, hand, beeEntity) == InteractionResult.SUCCESS) {
                return InteractionResult.SUCCESS;
            }
            else if(BeeInteractivity.beeUnpollinating(entity.level(), player, hand, beeEntity) == InteractionResult.SUCCESS) {
                return InteractionResult.SUCCESS;
            }
        }
        else if (entity instanceof Slime slimeEntity) {
            if(CreatingHoneySlime.createHoneySlime(entity.level(), player, hand, slimeEntity) == InteractionResult.SUCCESS) {
                return InteractionResult.SUCCESS;
            }
        }
        return null;
    }


    // heal bees with sugar water bottle or honey bottle
    public static InteractionResult beeFeeding(Level world, Player playerEntity, InteractionHand hand, Entity target) {
        if (target instanceof Bee beeEntity && !beeEntity.isDeadOrDying()) {

            ItemStack itemstack = playerEntity.getItemInHand(hand);
            ResourceLocation itemRL = BuiltInRegistries.ITEM.getKey(itemstack.getItem());

            if (itemstack.is(BzItems.BEE_STINGER.get())) {
                beeEntity.hasStung();
                ((BeeEntityInvoker)beeEntity).callSetHasStung(false);
                GeneralUtils.givePlayerItem(playerEntity, hand, ItemStack.EMPTY, false, true);

                if (playerEntity instanceof ServerPlayer serverPlayer) {
                    PlayerDataHandler.onBeesSaved(serverPlayer);
                }

                return InteractionResult.SUCCESS;
            }

            // Disallow all non-tagged items from being fed to bees
            if(!itemstack.is(BzTags.BEE_FEEDING_ITEMS))
                return InteractionResult.PASS;
            if(world.isClientSide())
                return InteractionResult.SUCCESS;

            boolean removedWrath;
            ItemStack itemstackOriginal = itemstack.copy();

            // Special cased items so the ActionResultType continues and make the item's behavior not lost.
            if (itemstackOriginal.getItem() == BzItems.BEE_BREAD.get()) {
                removedWrath = calmAndSpawnHearts(world, playerEntity, beeEntity, 0.3f, 3);

                if(removedWrath && playerEntity instanceof ServerPlayer serverPlayer) {
                    BzCriterias.FOOD_REMOVED_WRATH_OF_THE_HIVE_TRIGGER.get().trigger(serverPlayer, itemstackOriginal);
                }

                playerEntity.swing(hand, true);
                return InteractionResult.PASS;
            }

            if (itemstack.is(BzTags.HONEY_BUCKETS) ||
                itemstack.is(BzTags.ROYAL_JELLY_BUCKETS) ||
                itemstack.is(BzItems.ROYAL_JELLY_BOTTLE.get()))
            {
                beeEntity.heal(beeEntity.getMaxHealth() - beeEntity.getHealth());
                boolean isRoyalFed = itemstack.is(BzItems.ROYAL_JELLY_BOTTLE.get()) || itemstack.is(BzItems.ROYAL_JELLY_BUCKET.get());
                boolean isRoyalFedBucket = itemstack.is(BzItems.ROYAL_JELLY_BUCKET.get());

                removedWrath = calmAndSpawnHearts(world, playerEntity, beeEntity, isRoyalFed ? 1 : 0.8f, isRoyalFed ? 15 : 5);
                if (beeEntity.isBaby()) {
                    if (isRoyalFed || playerEntity.getRandom().nextBoolean()) {
                        beeEntity.setBaby(false);
                        if(playerEntity instanceof ServerPlayer serverPlayer) {
                            BzCriterias.HONEY_BUCKET_BEE_GROW_TRIGGER.get().trigger(serverPlayer);
                        }
                    }
                }
                else {
                    int nearbyAdultBees = 0;
                    for (Bee nearbyBee : world.getEntitiesOfClass(Bee.class, beeEntity.getBoundingBox().inflate(4), beeEntity1 -> true)) {
                        nearbyBee.setInLove(playerEntity);
                        if(!nearbyBee.isBaby()) nearbyAdultBees++;
                    }

                    if(nearbyAdultBees >= 2 && playerEntity instanceof ServerPlayer serverPlayer) {
                        BzCriterias.HONEY_BUCKET_BEE_LOVE_TRIGGER.get().trigger(serverPlayer);
                    }
                }

                if (isRoyalFed) {
                    beeEntity.addEffect(new MobEffectInstance(BzEffects.BEENERGIZED.holder(), isRoyalFedBucket ? 90000 : 20000, 3, true, true, true));
                    if (playerEntity instanceof ServerPlayer) {
                        BzCriterias.BEENERGIZED_MAXED_TRIGGER.get().trigger((ServerPlayer) playerEntity);
                    }
                }
            }
            else if(itemRL.getPath().contains("honey")) {
                beeEntity.heal(2);
                removedWrath = calmAndSpawnHearts(world, playerEntity, beeEntity, 0.3f, 3);
            }
            else {
                beeEntity.heal(1);
                removedWrath = calmAndSpawnHearts(world, playerEntity, beeEntity, 0.1f, 3);
            }

            // remove current item
            GeneralUtils.givePlayerItem(playerEntity, hand, ItemStack.EMPTY, true, true);

            if(playerEntity instanceof ServerPlayer serverPlayer) {
                PlayerDataHandler.onBeesFed(serverPlayer);

                if(removedWrath) {
                    BzCriterias.FOOD_REMOVED_WRATH_OF_THE_HIVE_TRIGGER.get().trigger(serverPlayer, itemstackOriginal);
                }
            }

            playerEntity.swing(hand, true);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }


    public static InteractionResult beeUnpollinating(Level world, Player playerEntity, InteractionHand hand, Bee beeEntity) {
        if (beeEntity.getType().is(BzTags.POLLEN_PUFF_CAN_POLLINATE)) {
            ItemStack itemstack = playerEntity.getItemInHand(hand);

            // right clicking on pollinated bee with watery items will drops pollen puff in world
            if(beeEntity.hasNectar()) {
                if(itemstack.is(BzTags.WASHING_ITEMS)) {

                    if(world.isClientSide())
                        return InteractionResult.SUCCESS;

                    ItemStack playerItem = playerEntity.getItemInHand(hand);
                    if (!playerItem.isEmpty()) {
                        playerEntity.awardStat(Stats.ITEM_USED.get(playerItem.getItem()));
                    }

                    PollenPuff.spawnItemstackEntity(world, beeEntity.getRandom(), beeEntity.blockPosition(), new ItemStack(BzItems.POLLEN_PUFF.get(), 1));
                    playerEntity.swing(hand, true);
                    ((BeeEntityInvoker)beeEntity).callSetHasNectar(false);

                    if(playerEntity instanceof ServerPlayer serverPlayer) {
                        BzCriterias.BEE_DROP_POLLEN_PUFF_TRIGGER.get().trigger(serverPlayer);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.PASS;
    }

    public static boolean calmAndSpawnHearts(Level world, Player playerEntity, LivingEntity beeEntity, float calmChance, int hearts) {
        RandomSource random = playerEntity.getRandom();
        boolean calmed = random.nextFloat() < calmChance;
        boolean removedWrath = false;
        if (calmed) {
            if(playerEntity.hasEffect(BzEffects.WRATH_OF_THE_HIVE.holder())) {
                playerEntity.removeEffect(BzEffects.WRATH_OF_THE_HIVE.holder());
                WrathOfTheHiveEffect.calmTheBees(playerEntity.level(), playerEntity);
                removedWrath = true;
            }

            playerEntity.addEffect(new MobEffectInstance(
                    BzEffects.PROTECTION_OF_THE_HIVE.holder(),
                    BzBeeAggressionConfigs.howLongProtectionOfTheHiveLasts,
                    0,
                    false,
                    false,
                    true));

            ((ServerLevel)playerEntity.level()).sendParticles(BzParticles.SPARKLE_PARTICLE.get(),
                    playerEntity.position().x(),
                    playerEntity.getEyeY() - 0.25d,
                    playerEntity.position().z(),
                    20,
                    playerEntity.getRandom().nextDouble() / 5 - 0.1d,
                    playerEntity.getRandom().nextDouble() / 5 - 0.1d,
                    playerEntity.getRandom().nextDouble() / 5 - 0.1d,
                    0.1D);

            if (playerEntity instanceof ServerPlayer serverPlayer) {
                BzCriterias.GETTING_PROTECTION_TRIGGER.get().trigger(serverPlayer);
            }
        }

        if (world instanceof ServerLevel serverLevel &&
            (beeEntity instanceof Bee bee ? (!bee.isAngry() || calmed) : calmed))
        {
            serverLevel.sendParticles(
                    ParticleTypes.HEART,
                    beeEntity.getX(),
                    beeEntity.getY(),
                    beeEntity.getZ(),
                    hearts,
                    random.nextFloat() * 0.5 - 0.25f,
                    random.nextFloat() * 0.2f + 0.2f,
                    random.nextFloat() * 0.5 - 0.25f,
                    random.nextFloat() * 0.4 + 0.2f);
        }

        return removedWrath;
    }
}