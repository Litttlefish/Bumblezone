package com.telepathicgrunt.the_bumblezone.items;

import com.telepathicgrunt.the_bumblezone.entities.BeeAggression;
import com.telepathicgrunt.the_bumblezone.entities.mobs.VariantBeeEntity;
import com.telepathicgrunt.the_bumblezone.events.player.BzPlayerItemAttackBlockEvent;
import com.telepathicgrunt.the_bumblezone.menus.BuzzingBriefcaseMenuProvider;
import com.telepathicgrunt.the_bumblezone.modinit.BzCriterias;
import com.telepathicgrunt.the_bumblezone.modinit.BzDataComponents;
import com.telepathicgrunt.the_bumblezone.modinit.BzItems;
import com.telepathicgrunt.the_bumblezone.modinit.BzSounds;
import com.telepathicgrunt.the_bumblezone.modinit.BzStats;
import com.telepathicgrunt.the_bumblezone.modinit.BzTags;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BuzzingBriefcase extends Item {
    public static final String TAG_BEES = "BeesStored";
    public static final String TAG_VARANT_BEES = "VariantBeesStored";
    public static final int MAX_NUMBER_OF_BEES = 14;

    public BuzzingBriefcase(Properties properties) {
        super(properties.component(BzDataComponents.BUZZING_BRIEFCASE_DATA.get(), CustomData.EMPTY));
    }

    @Override
    public void verifyComponentsAfterLoad(ItemStack itemStack) {
        if (itemStack.get(BzDataComponents.BUZZING_BRIEFCASE_DATA.get()) == null) {
            itemStack.set(BzDataComponents.BUZZING_BRIEFCASE_DATA.get(), CustomData.EMPTY);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(stack.getItem())) {
            return new InteractionResultHolder<>(InteractionResult.PASS, stack);
        }

        if (player.isShiftKeyDown()) {
            player.openMenu(new BuzzingBriefcaseMenuProvider(stack));

            player.awardStat(BzStats.INTERACT_WITH_BUZZING_BRIEFCASE_RL.get());
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        }

        return new InteractionResultHolder<>(InteractionResult.PASS, stack);
    }

    public static InteractionResult onLeftClickBlock(BzPlayerItemAttackBlockEvent event) {
        Player player = event.user();
        ItemStack briefcaseItem = player.getItemInHand(event.hand());

        if (event.hand() != InteractionHand.MAIN_HAND ||
            !briefcaseItem.is(BzItems.BUZZING_BRIEFCASE.get()) ||
            player.getCooldowns().isOnCooldown(briefcaseItem.getItem()))
        {
            return null;
        }

        if (player.level().isClientSide()) {
            int numberOfBees = getNumberOfBees(briefcaseItem);
            return numberOfBees > 0 ? InteractionResult.SUCCESS : null;
        }

        List<Entity> releasedBees = dumpBees(player, player.isCrouching() ? -1 : 0, false);

        if(player instanceof ServerPlayer serverPlayer && !releasedBees.isEmpty()) {
            BzCriterias.BUZZING_BRIEFCASE_RELEASE_TRIGGER.get().trigger(serverPlayer);

            player.awardStat(Stats.ITEM_USED.get(briefcaseItem.getItem()));

            serverPlayer.getCooldowns().addCooldown(briefcaseItem.getItem(), 10);
            return InteractionResult.SUCCESS;
        }

        return null ;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity victim, LivingEntity attacker) {
        if (attacker instanceof Player player) {
            if (player.getCooldowns().isOnCooldown(stack.getItem())) {
                return false;
            }

            boolean isVictimBeelike = BeeAggression.isBeelikeEntity(victim);
            List<Entity> releasedBees = dumpBees(player, player.isCrouching() ? -1 : 0, false);
            for (Entity entity : releasedBees) {
                if (entity instanceof NeutralMob neutralMob && !isVictimBeelike) {
                    neutralMob.setTarget(victim);
                    neutralMob.setRemainingPersistentAngerTime(400); // 20 seconds
                    neutralMob.setPersistentAngerTarget(victim.getUUID());
                }
            }

            if(!isVictimBeelike && player instanceof ServerPlayer serverPlayer && !releasedBees.isEmpty()) {
                BzCriterias.BUZZING_BRIEFCASE_RELEASE_TRIGGER.get().trigger(serverPlayer);
                serverPlayer.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                serverPlayer.getCooldowns().addCooldown(stack.getItem(), 10);
            }
        }
        return true;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack beeCannon, Player player, LivingEntity entity, InteractionHand playerHand) {
        if (!(entity instanceof Bee bee) || bee.getType().is(BzTags.BUZZING_BRIEFCASE_DISALLOWED_BEE) || entity.isDeadOrDying()) {
            return InteractionResult.PASS;
        }

        if (player.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ItemStack briefcaseItem = player.getItemInHand(playerHand);
        if (player.getCooldowns().isOnCooldown(briefcaseItem.getItem())) {
            return InteractionResult.PASS;
        }

        boolean addedBee = tryAddBee(briefcaseItem, entity);
        if (addedBee) {
            player.awardStat(BzStats.BUZZING_BRIEFCASE_BEE_CAPTURE_RL.get());
            player.awardStat(Stats.ITEM_USED.get(briefcaseItem.getItem()));
            player.swing(playerHand, true);

            if(player instanceof ServerPlayer serverPlayer && getBeesStored(serverPlayer.level(), briefcaseItem, false).size() == MAX_NUMBER_OF_BEES) {
                BzCriterias.BUZZING_BRIEFCASE_FULL_TRIGGER.get().trigger(serverPlayer);
            }

            CompoundTag cannonTag = briefcaseItem.get(BzDataComponents.BUZZING_BRIEFCASE_DATA.get()).getUnsafe();
            int variantBeesCaught = cannonTag.getInt(TAG_VARANT_BEES);
            if (player instanceof ServerPlayer serverPlayer && variantBeesCaught > 0) {
                BzCriterias.VARIANT_BEE_BRIEFCASE_CAPTURE_TRIGGER.get().trigger(serverPlayer, variantBeesCaught);
            }

            return InteractionResult.SUCCESS;
        }
        else {
            return InteractionResult.PASS;
        }
    }

    public static List<Entity> dumpBees(Player player, int beeIndex, boolean releaseAtPlayer) {
        Level level = player.level();
        ItemStack briefcaseItem = player.getItemInHand(InteractionHand.MAIN_HAND);

        int numberOfBees = getNumberOfBees(briefcaseItem);
        if (numberOfBees > 0) {
            List<Entity> bees = new ArrayList<>();
            if (beeIndex == -1) {
                bees = getBeesStored(level, briefcaseItem, true);
            }
            else {
                bees.add(getSpecificBeesStored(level, briefcaseItem, beeIndex, true));
            }

            if (!bees.isEmpty()) {
                player.awardStat(Stats.ITEM_USED.get(briefcaseItem.getItem()));

                float maxDistance = 15;
                Vec3 playerEyePos = new Vec3(player.getX(), player.getEyeY() - 0.25f, player.getZ());
                Vec3 maxDistanceDirection = player.getLookAngle().multiply(maxDistance, maxDistance, maxDistance);
                Vec3 finalPos = playerEyePos.add(maxDistanceDirection.normalize());
                if (!releaseAtPlayer) {
                    finalPos = playerEyePos.add(maxDistanceDirection);

                    EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(
                            level,
                            player,
                            playerEyePos,
                            finalPos,
                            player.getBoundingBox().expandTowards(maxDistanceDirection),
                            entity -> !entity.isInvisibleTo(player) && !BeeAggression.isBeelikeEntity(entity),
                            0.25f);

                    if (entityHitResult != null && entityHitResult.getType() != HitResult.Type.MISS) {
                        finalPos = entityHitResult.getLocation();
                    }
                    else {
                        HitResult hitResult = level.clip(new ClipContext(
                                playerEyePos,
                                finalPos,
                                ClipContext.Block.COLLIDER,
                                ClipContext.Fluid.NONE,
                                player));

                        if (hitResult instanceof BlockHitResult blockHitResult) {
                            Vec3 locationClicked = hitResult.getLocation();
                            Vec3 offset = Vec3.atLowerCornerOf(blockHitResult.getDirection().getNormal());
                            if (blockHitResult.getDirection() == Direction.UP) {
                                offset = offset.scale(0.35d);
                            }
                            else if (blockHitResult.getDirection() != Direction.DOWN) {
                                offset = offset.scale(0.5d);
                            }
                            finalPos = locationClicked.add(offset);
                        }
                    }
                }

                Vec3 finalPos1 = finalPos;
                bees.forEach(bee -> {
                    bee.moveTo(finalPos1.x(),
                            finalPos1.y(),
                            finalPos1.z(),
                            player.getYRot(),
                            player.getXRot());
                    level.addFreshEntity(bee);
                });

                level.playSound(null, player.blockPosition(), BzSounds.BUZZING_BRIEFCASE_RELEASES.get(), SoundSource.PLAYERS, 1.0F, (player.getRandom().nextFloat() * 0.2F) + 0.6F);
                return new ArrayList<>(bees);
            }
        }
        return new ArrayList<>();
    }

    public static List<Entity> getBeesStored(Level level, ItemStack briefcaseItem, boolean removeFromList) {
        if (getNumberOfBees(briefcaseItem) > 0) {
            CompoundTag briefcaseTag = briefcaseItem.get(BzDataComponents.BUZZING_BRIEFCASE_DATA.get()).copyTag();
            ListTag beeList = briefcaseTag.getList(TAG_BEES, ListTag.TAG_COMPOUND);
            List<Entity> beesStored = new ObjectArrayList<>();
            if (removeFromList) {
                for (int i = beeList.size() - 1; i >= 0; i--) {
                    CompoundTag beeTag = beeList.getCompound(0);
                    beeList.removeFirst();
                    Entity entity = EntityType.loadEntityRecursive(beeTag, level, entityx -> entityx);

                    if (entity != null) {
                        if (entity instanceof VariantBeeEntity) {
                            briefcaseTag.putInt(TAG_VARANT_BEES, Math.max(0, briefcaseTag.getInt(TAG_VARANT_BEES) - 1));
                        }

                        if (addBeeToList(beesStored, beeTag, entity)) {
                            break;
                        }
                    }
                }
            }
            else {
                for (int i = 0 ; i < beeList.size(); i++) {
                    CompoundTag beeTag = beeList.getCompound(i);
                    Entity entity = EntityType.loadEntityRecursive(beeTag, level, entityx -> entityx);
                    if (entity != null) {
                        if (addBeeToList(beesStored, beeTag, entity)) {
                            break;
                        }
                    }
                    else {
                        beeList.remove(i);
                        i--;
                        removeFromList = true;
                    }
                }
            }
            if (removeFromList) {
                briefcaseItem.set(BzDataComponents.BUZZING_BRIEFCASE_DATA.get(), CustomData.of(briefcaseTag));
            }
            return beesStored;
        }
        return new ObjectArrayList<>();
    }

    private static boolean addBeeToList(List<Entity> beesStored, CompoundTag beeTag, Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            if (beeTag.contains("Attributes", 9)) {
                livingEntity.getAttributes().load(beeTag.getList("Attributes", 10));
            }
            if (beeTag.contains("Health", 99)) {
                livingEntity.setHealth(beeTag.getFloat("Health"));
            }
        }

        beesStored.add(entity);
        return beesStored.size() == MAX_NUMBER_OF_BEES;
    }

    public static Entity getSpecificBeesStored(Level level, ItemStack briefcaseItem, int beeIndex, boolean removeFromList) {
        if (getNumberOfBees(briefcaseItem) > 0) {
            CompoundTag briefcaseTag = briefcaseItem.get(BzDataComponents.BUZZING_BRIEFCASE_DATA.get()).copyTag();
            ListTag beeList = briefcaseTag.getList(TAG_BEES, ListTag.TAG_COMPOUND);
            if (beeIndex < beeList.size()) {
                CompoundTag beeTag = beeList.getCompound(beeIndex);
                if (removeFromList) {
                    beeList.remove(beeIndex);
                }

                Entity entity = EntityType.loadEntityRecursive(beeTag, level, entityx -> entityx);
                if (entity instanceof VariantBeeEntity && removeFromList) {
                    briefcaseTag.putInt(TAG_VARANT_BEES, Math.max(0, briefcaseTag.getInt(TAG_VARANT_BEES) - 1));
                }

                if (removeFromList) {
                    briefcaseItem.set(BzDataComponents.BUZZING_BRIEFCASE_DATA.get(), CustomData.of(briefcaseTag));
                }
                return entity;
            }
            return null;
        }
        return null;
    }

    public static void overrwriteBees(ItemStack briefcaseItem, List<Entity> bees) {
        briefcaseItem.set(BzDataComponents.BUZZING_BRIEFCASE_DATA.get(), CustomData.of(new CompoundTag()));
        bees.forEach(bee -> tryAddBee(briefcaseItem, bee));
    }

    public static boolean tryAddBee(ItemStack briefcaseItem, Entity bee) {
        if (getNumberOfBees(briefcaseItem) < MAX_NUMBER_OF_BEES) {
            String beeTypeRL = bee.getEncodeId();
            if (beeTypeRL == null) {
                return false;
            }

            CompoundTag briefcaseTag = briefcaseItem.get(BzDataComponents.BUZZING_BRIEFCASE_DATA.get()).copyTag();
            ListTag beeList = briefcaseTag.getList(TAG_BEES, ListTag.TAG_COMPOUND);
            CompoundTag beeTag = new CompoundTag();

            bee.stopRiding();
            bee.ejectPassengers();
            beeTag.putString("id", beeTypeRL);
            bee.saveWithoutId(beeTag);

            UUID uUID = bee.getUUID();
            bee.load(beeTag);
            bee.setUUID(uUID);
            beeTag.remove("UUID");
            beeList.add(beeTag);
            briefcaseTag.put(TAG_BEES, beeList);

            bee.discard();

            if (bee instanceof VariantBeeEntity) {
                briefcaseTag.putInt(TAG_VARANT_BEES, briefcaseTag.getInt(TAG_VARANT_BEES) + 1);
            }

            CustomData customData1 = CustomData.of(briefcaseTag);
            briefcaseItem.set(BzDataComponents.BUZZING_BRIEFCASE_DATA.get(), customData1);
            return true;
        }
        return false;
    }

    public static int getNumberOfBees(ItemStack briefcaseItem) {
        CompoundTag briefcaseTag = briefcaseItem.get(BzDataComponents.BUZZING_BRIEFCASE_DATA.get()).copyTag();
        if (briefcaseTag.contains(TAG_BEES)) {
            ListTag beeList = briefcaseTag.getList(TAG_BEES, ListTag.TAG_COMPOUND);
            return beeList.size();
        }
        else {
            ListTag listTag = new ListTag();
            briefcaseTag.put(TAG_BEES, listTag);
            briefcaseItem.set(BzDataComponents.BUZZING_BRIEFCASE_DATA.get(), CustomData.of(briefcaseTag));
        }
        return 0;
    }
}