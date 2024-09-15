package com.telepathicgrunt.the_bumblezone.blocks;

import com.mojang.serialization.MapCodec;
import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.blocks.blockentities.EssenceBlockEntity;
import com.telepathicgrunt.the_bumblezone.bossbars.ServerEssenceEvent;
import com.telepathicgrunt.the_bumblezone.entities.nonliving.ElectricRingEntity;
import com.telepathicgrunt.the_bumblezone.items.essence.EssenceOfTheBees;
import com.telepathicgrunt.the_bumblezone.modinit.BzEntities;
import com.telepathicgrunt.the_bumblezone.modinit.BzSounds;
import com.telepathicgrunt.the_bumblezone.modinit.BzStats;
import com.telepathicgrunt.the_bumblezone.modinit.BzTags;
import com.telepathicgrunt.the_bumblezone.packets.MusicPacketFromServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class EssenceBlockYellow extends EssenceBlock {

    public static final MapCodec<EssenceBlockYellow> CODEC = Block.simpleCodec(EssenceBlockYellow::new);

    private static final float RINGS_TO_PASS = 100;

    public EssenceBlockYellow() {
        this(Properties.of()
                .mapColor(MapColor.COLOR_YELLOW)
                .strength(-1.0f, 3600000.8f)
                .lightLevel((blockState) -> 15)
                .noLootTable()
                .forceSolidOn()
                .isValidSpawn((blockState, blockGetter, blockPos, entityType) -> false)
                .isViewBlocking((blockState, blockGetter, blockPos) -> false)
                .pushReaction(PushReaction.BLOCK));
    }

    public EssenceBlockYellow(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<? extends EssenceBlockYellow> codec() {
        return CODEC;
    }

    @Override
    public ResourceLocation getArenaNbt() {
        return ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "essence/yellow_arena");
    }

    @Override
    public int getEventTimeFrame() {
        return 6000;
    }

    @Override
    public ServerEssenceEvent getServerEssenceEvent() {
        return (ServerEssenceEvent) new ServerEssenceEvent(
                "essence.the_bumblezone.yellow_essence_event",
                BossEvent.BossBarColor.YELLOW,
                BossEvent.BossBarOverlay.PROGRESS
        ).setDarkenScreen(true);
    }

    @Override
    public ResourceLocation getEssenceItemReward() {
        return ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "gameplay/rewards/yellow_arena_victory");
    }

    @Override
    public int getEssenceXpReward() {
        return 3000;
    }

    @Override
    public boolean hasMiningFatigue() {
        return true;
    }

    @Override
    public void awardPlayerWinStat(ServerPlayer serverPlayer) {
        serverPlayer.awardStat(BzStats.RADIANCE_EVENT_DEFEATED_RL.get());
    }

    @Override
    public void performUniqueArenaTick(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState, EssenceBlockEntity essenceBlockEntity) {
        if (essenceBlockEntity.getPlayerInArena().size() == 0) return;

        int ringsPassed = essenceBlockEntity.getExtraEventTrackingProgress();
        List<EssenceBlockEntity.EventEntities> eventEntitiesInArena = essenceBlockEntity.getEventEntitiesInArena();

        int ringsActive = 0;
        // update how many entities are alive
        for (int i = eventEntitiesInArena.size() - 1; i >= 0; i--) {
            UUID entityToCheck = eventEntitiesInArena.get(i).uuid();
            Entity entity = serverLevel.getEntity(entityToCheck);
            if (entity == null) {
                List<ElectricRingEntity> nearbyRings = serverLevel.getEntitiesOfClass(
                        ElectricRingEntity.class,
                        new AABB(
                                blockPos.getX() - (essenceBlockEntity.getArenaSize().getX() * 0.5f),
                                blockPos.getY() - (essenceBlockEntity.getArenaSize().getY() * 0.5f),
                                blockPos.getZ() - (essenceBlockEntity.getArenaSize().getZ() * 0.5f),
                                blockPos.getX() + (essenceBlockEntity.getArenaSize().getX() * 0.5f),
                                blockPos.getY() + (essenceBlockEntity.getArenaSize().getY() * 0.5f),
                                blockPos.getZ() + (essenceBlockEntity.getArenaSize().getZ() * 0.5f)
                        ));

                for (ElectricRingEntity nearbyRing : nearbyRings) {
                    if (nearbyRing.getUUID().equals(entityToCheck) && nearbyRing.getEssenceController().equals(essenceBlockEntity.getUUID())) {
                        entity = nearbyRing;
                        break;
                    }
                }
            }

            if (entity == null) {
                // Ring will be the one to notify us if it was passed through
                eventEntitiesInArena.remove(i);
            }
            else if (entity instanceof ElectricRingEntity electricRingEntity) {
                electricRingEntity.setEssenceController(essenceBlockEntity.getUUID());
                electricRingEntity.setEssenceControllerBlockPos(essenceBlockEntity.getBlockPos());
                electricRingEntity.setEssenceControllerDimension(serverLevel.dimension());
                ringsActive++;
            }
            else if (entity instanceof Vex vex && vex.getTarget() != null && vex.tickCount % 20 == 0) {
                Vec3 targetDirection = vex.getTarget().position().subtract(vex.position()).normalize();
                if (vex.isCharging()) {
                    vex.addDeltaMovement(targetDirection.scale(0.3));
                }
                if (vex.getRandom().nextInt(15) == 0) {
                    vex.getMoveControl().setWantedPosition(vex.getX(), vex.getY(), vex.getZ(), 1);
                }
            }
        }

        if (ringsPassed != RINGS_TO_PASS && ringsActive == 0) {
            // spawn a ring this tick.
            spawnNewRing(serverLevel, blockPos, essenceBlockEntity, ringsPassed, eventEntitiesInArena);
            if (ringsPassed >= 2) {
                spawnNewEnemy(serverLevel, blockPos, essenceBlockEntity, eventEntitiesInArena);
            }
        }

        float newProgress = ringsPassed / RINGS_TO_PASS;
        essenceBlockEntity.getEventBar().setProgress(1 - newProgress);
        essenceBlockEntity.setExtraEventTrackingProgress(ringsPassed);
        if (ringsPassed == RINGS_TO_PASS) {
            EssenceBlockEntity.EndEvent(serverLevel, blockPos, blockState, essenceBlockEntity, true);
        }
    }

    private static void spawnNewRing(ServerLevel serverLevel, BlockPos blockPos, EssenceBlockEntity essenceBlockEntity, int currentRingsPassed, List<EssenceBlockEntity.EventEntities> eventEntitiesInArena) {
        BlockPos arenaSize = essenceBlockEntity.getArenaSize();
        RandomSource random = serverLevel.getRandom();
        int x = (arenaSize.getX() / 2) - 5;
        int y = (-(arenaSize.getY() / 2) + 1);
        int z = (arenaSize.getZ() / 2) - 5;

        if (currentRingsPassed / RINGS_TO_PASS >= 0.5) {
            float randomChosen = random.nextFloat();
            if (randomChosen < 0.3) {
                randomChosen = 0;
            }
            else {
                randomChosen = (((randomChosen * 0.4F) + 0.55F) * ((currentRingsPassed * 12) / RINGS_TO_PASS));
            }
            y = (int) Math.min(y + randomChosen, (arenaSize.getY() / 2f) - 3);
        }
        else {
            y = (int) Math.min(y + (random.nextFloat() * ((currentRingsPassed * 12) / RINGS_TO_PASS)), (arenaSize.getY() / 2f) - 3);
        }

        switch (currentRingsPassed % 4) {
            case 0 -> {
                x = (int) ((x * random.nextFloat()) + 2);
                z = (int) ((z * random.nextFloat()) + 2);
            }
            case 1 -> {
                x = (int) ((x * random.nextFloat()) + 2);
                z = (int) ((z * random.nextFloat()) + 2) * -1;
            }
            case 2 -> {
                x = (int) ((x * random.nextFloat()) + 2) * -1;
                z = (int) ((z * random.nextFloat()) + 2) * -1;
            }
            case 3 -> {
                x = (int) ((x * random.nextFloat()) + 2) * -1;
                z = (int) ((z * random.nextFloat()) + 2);
            }
        }


        Vec3 centerOfRing = new Vec3(blockPos.getX() + x, blockPos.getY() + y, blockPos.getZ() + z);
        Vec3 centerOfEssence = essenceBlockEntity.getBlockPos().getCenter();
        centerOfEssence = new Vec3(centerOfEssence.x(), 0, centerOfEssence.z());
        Vec3 vectorFromEssence = centerOfEssence.subtract(centerOfRing).add(0, centerOfRing.y(), 0).normalize();

        double angle = Mth.atan2(-vectorFromEssence.x(), vectorFromEssence.z()) + Mth.HALF_PI;
        if (angle < 0) {
            angle += Mth.PI;
        }
        angle *= Mth.RAD_TO_DEG;

        ElectricRingEntity ringEntity = BzEntities.ELECTRIC_RING_ENTITY.get().create(serverLevel);

        if (ringEntity != null) {
            ringEntity.setYRot((float) angle);

            if (y > 0) {
                ringEntity.setXRot(90);
            }
            else if (y > -3) {
                ringEntity.setXRot(45);
            }

            ringEntity.setPos(centerOfRing.x, centerOfRing.y, centerOfRing.z);
            ringEntity.setOldPosAndRot();

            ringEntity.setEssenceController(essenceBlockEntity.getUUID());
            ringEntity.setEssenceControllerBlockPos(essenceBlockEntity.getBlockPos());
            ringEntity.setEssenceControllerDimension(serverLevel.dimension());
            eventEntitiesInArena.add(new EssenceBlockEntity.EventEntities(ringEntity.getUUID()));

            serverLevel.addFreshEntityWithPassengers(ringEntity);
        }
    }

    private static void spawnNewEnemy(ServerLevel serverLevel, BlockPos blockPos, EssenceBlockEntity essenceBlockEntity, List<EssenceBlockEntity.EventEntities> eventEntitiesInArena) {
        TagKey<EntityType<?>> enemyTagToUse = BzTags.ESSENCE_RADIANCE_ARENA_NORMAL_ENEMY;

        List<? extends EntityType<?>> entityTypeList = BuiltInRegistries.ENTITY_TYPE
                .getTag(enemyTagToUse)
                .map(holders -> holders
                        .stream()
                        .map(Holder::value)
                        .toList()
                ).orElseGet(ArrayList::new);

        EntityType<?> entityTypeToSpawn = entityTypeList.get(serverLevel.getRandom().nextInt(entityTypeList.size()));
        int yOffset = (essenceBlockEntity.getArenaSize().getY() - 2) / 2;
        Entity entity = entityTypeToSpawn.spawn(serverLevel, blockPos.offset(0, yOffset, 0), MobSpawnType.TRIGGERED);
        if (entity != null) {
            eventEntitiesInArena.add(new EssenceBlockEntity.EventEntities(entity.getUUID()));

            UUID playerUUID = essenceBlockEntity.getPlayerInArena().get(serverLevel.getRandom().nextInt(essenceBlockEntity.getPlayerInArena().size()));
            Player player = serverLevel.getServer().getPlayerList().getPlayer(playerUUID);
            if (player instanceof ServerPlayer serverPlayer) {
                float maxHeart = Math.max(serverPlayer.getHealth(), serverPlayer.getMaxHealth());
                float maxArmor = serverPlayer.getArmorValue();
                float mobHealthBoost = (maxHeart / 10) + (maxArmor / 2);
                float mobAttackBoost = Math.max((maxHeart / 40) + (maxArmor / 3f) - 3.5f, 0);

                boolean isEssenced = EssenceOfTheBees.hasEssence(serverPlayer);
                if (!isEssenced) {
                    mobHealthBoost *= 1.5f;
                    mobAttackBoost *= 1.5f;
                }

                if (entity instanceof LivingEntity livingEntity) {
                    AttributeInstance livingEntityAttributeHealth = livingEntity.getAttribute(Attributes.MAX_HEALTH);
                    if (livingEntityAttributeHealth != null) {
                        livingEntityAttributeHealth.addPermanentModifier(new AttributeModifier(
                                ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "essence_arena_health_boost"),
                                mobHealthBoost,
                                AttributeModifier.Operation.ADD_VALUE));
                        livingEntity.heal(mobHealthBoost);
                    }

                    AttributeInstance livingEntityAttributeAttack = livingEntity.getAttribute(Attributes.ATTACK_DAMAGE);
                    if (livingEntityAttributeAttack != null) {
                        livingEntityAttributeAttack.addPermanentModifier(new AttributeModifier(
                                ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "essence_arena_damage_boost"),
                                mobAttackBoost,
                                AttributeModifier.Operation.ADD_VALUE));
                    }

                    AttributeInstance livingEntityAttributeKnockback = livingEntity.getAttribute(Attributes.ATTACK_KNOCKBACK);
                    if (livingEntityAttributeKnockback != null) {
                        livingEntityAttributeKnockback.addPermanentModifier(new AttributeModifier(
                                ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "essence_arena_knockback_boost"),
                                isEssenced ? 0.3 : 0.6,
                                AttributeModifier.Operation.ADD_VALUE));
                    }

                    AttributeInstance livingEntityAttributeSpeed = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
                    if (livingEntityAttributeSpeed != null) {
                        livingEntityAttributeSpeed.addPermanentModifier(new AttributeModifier(
                                ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "essence_arena_speed_boost"),
                                isEssenced ? 0.065 : 0.085,
                                AttributeModifier.Operation.ADD_VALUE));
                    }

                    AttributeInstance livingEntityAttributeFlyingSpeed = livingEntity.getAttribute(Attributes.FLYING_SPEED);
                    if (livingEntityAttributeFlyingSpeed != null) {
                        livingEntityAttributeFlyingSpeed.addPermanentModifier(new AttributeModifier(
                                ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "essence_arena_flying_speed_boost"),
                                isEssenced ? 0.065 : 0.085,
                                AttributeModifier.Operation.ADD_VALUE));
                    }

                    AttributeInstance livingEntityAttributeFollowRange = livingEntity.getAttribute(Attributes.FOLLOW_RANGE);
                    if (livingEntityAttributeFollowRange != null) {
                        livingEntityAttributeFollowRange.addPermanentModifier(new AttributeModifier(
                                ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "essence_arena_sight_boost"),
                                32,
                                AttributeModifier.Operation.ADD_VALUE));
                    }
                }

                if (entity instanceof NeutralMob neutralMob) {
                    neutralMob.setRemainingPersistentAngerTime(Integer.MAX_VALUE);
                    neutralMob.setPersistentAngerTarget(playerUUID);
                    neutralMob.setTarget(serverPlayer);
                }
                else if (entity instanceof Mob mob) {
                    mob.setTarget(serverPlayer);
                    if (entity instanceof Rabbit rabbit) {
                        rabbit.setVariant(Rabbit.Variant.EVIL);
                    }
                }

                if (entity instanceof Vex vex && eventEntitiesInArena.size() > 25) {
                    vex.setSilent(true);
                }
            }
        }
    }

    @Override
    public void onPlayerEnter(ServerLevel serverLevel, ServerPlayer serverPlayer, EssenceBlockEntity essenceBlockEntity) {
        MusicPacketFromServer.sendToClient(serverPlayer, BzSounds.RADIANCE_EVENT.get().getLocation(), true);
        super.onPlayerEnter(serverLevel, serverPlayer, essenceBlockEntity);
    }

    @Override
    public void onPlayerLeave(ServerLevel serverLevel, ServerPlayer serverPlayer, EssenceBlockEntity essenceBlockEntity) {
        MusicPacketFromServer.sendToClient(serverPlayer, BzSounds.RADIANCE_EVENT.get().getLocation(), false);
        removeBonusEffectsFromPlayer(serverPlayer);
        super.onPlayerLeave(serverLevel, serverPlayer, essenceBlockEntity);
    }

    public void ringActivated(ElectricRingEntity electricRingEntity, EssenceBlockEntity essenceBlockEntity, ServerPlayer serverPlayer) {
        int ringsPassed = essenceBlockEntity.getExtraEventTrackingProgress();
        essenceBlockEntity.getEventEntitiesInArena().removeIf(e -> e.uuid().equals(electricRingEntity.getUUID()));

        if (ringsPassed + 1 >= RINGS_TO_PASS) {
            removeBonusEffectsFromPlayer(serverPlayer);
        }
        else if (EssenceOfTheBees.hasEssence(serverPlayer)) {
            serverPlayer.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SPEED,
                    essenceBlockEntity.getEventTimer(),
                    ringsPassed / 7,
                    false,
                    false));

            serverPlayer.addEffect(new MobEffectInstance(
                    MobEffects.JUMP,
                    essenceBlockEntity.getEventTimer(),
                    Math.min(ringsPassed / 7, 8),
                    false,
                    false));
        }

        ringsPassed++;
        float newProgress = ringsPassed / RINGS_TO_PASS;
        essenceBlockEntity.getEventBar().setProgress(1 - newProgress);
        essenceBlockEntity.setExtraEventTrackingProgress(ringsPassed);
    }

    private static void removeBonusEffectsFromPlayer(ServerPlayer serverPlayer) {
        if (serverPlayer.hasEffect(MobEffects.JUMP)) {
            serverPlayer.removeEffect(MobEffects.JUMP);
        }
        if (serverPlayer.hasEffect(MobEffects.MOVEMENT_SPEED)) {
            serverPlayer.removeEffect(MobEffects.MOVEMENT_SPEED);
        }
    }
}
