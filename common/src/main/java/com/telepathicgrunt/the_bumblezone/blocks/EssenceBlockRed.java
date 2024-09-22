package com.telepathicgrunt.the_bumblezone.blocks;

import com.mojang.serialization.MapCodec;
import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.blocks.blockentities.EssenceBlockEntity;
import com.telepathicgrunt.the_bumblezone.bossbars.ServerEssenceEvent;
import com.telepathicgrunt.the_bumblezone.items.essence.EssenceOfTheBees;
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
import net.minecraft.world.BossEvent;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class EssenceBlockRed extends EssenceBlock {

    public static final MapCodec<EssenceBlockRed> CODEC = Block.simpleCodec(EssenceBlockRed::new);

    private static final float ENTITIES_TO_KILL = 100;

    public EssenceBlockRed() {
        this(Properties.of()
                .mapColor(MapColor.COLOR_RED)
                .strength(-1.0f, 3600000.8f)
                .lightLevel((blockState) -> 15)
                .noLootTable()
                .forceSolidOn()
                .isValidSpawn((blockState, blockGetter, blockPos, entityType) -> false)
                .isViewBlocking((blockState, blockGetter, blockPos) -> false)
                .pushReaction(PushReaction.BLOCK));
    }

    public EssenceBlockRed(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<? extends EssenceBlockRed> codec() {
        return CODEC;
    }

    @Override
    public ResourceLocation getArenaNbt() {
        return ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "essence/red_arena");
    }

    @Override
    public int getEventTimeFrame() {
        return 10800;
    }

    @Override
    public ServerEssenceEvent getServerEssenceEvent() {
        return (ServerEssenceEvent) new ServerEssenceEvent(
                "essence.the_bumblezone.red_essence_event",
                BossEvent.BossBarColor.RED,
                BossEvent.BossBarOverlay.NOTCHED_20
        ).setDarkenScreen(true);
    }

    @Override
    public ResourceLocation getEssenceItemReward() {
        return ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "gameplay/rewards/red_arena_victory");
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
        serverPlayer.awardStat(BzStats.RAGING_EVENT_DEFEATED_RL.get());
    }

    @Override
    public void performUniqueArenaTick(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState, EssenceBlockEntity essenceBlockEntity) {
        if (essenceBlockEntity.getPlayerInArena().size() == 0) return;

        int entitiesKilled = essenceBlockEntity.getExtraEventTrackingProgress();
        List<EssenceBlockEntity.EventEntities> eventEntitiesInArena = essenceBlockEntity.getEventEntitiesInArena();

        if (entitiesKilled != ENTITIES_TO_KILL && eventEntitiesInArena.size() < Math.min(3 + (essenceBlockEntity.getPlayerInArena().size() * 1.5), ENTITIES_TO_KILL - entitiesKilled)) {
            // spawn a mob this tick.
            int currentEntityCount = eventEntitiesInArena.size() + entitiesKilled;
            SpawnNewEnemy(serverLevel, blockPos, essenceBlockEntity, currentEntityCount, eventEntitiesInArena);
        }
        else {
            // update how many entities are alive
            for (int i = eventEntitiesInArena.size() - 1; i >= 0; i--) {
                UUID entityToCheck = eventEntitiesInArena.get(i).uuid();
                Entity entity = serverLevel.getEntity(entityToCheck);
                if (entity == null) {
                    entitiesKilled++;
                    eventEntitiesInArena.remove(i);
                }
                else {
                    if (entity instanceof NeutralMob neutralMob && !(neutralMob.getTarget() instanceof Player)) {
                        UUID playerUUID = essenceBlockEntity.getPlayerInArena().get(serverLevel.getRandom().nextInt(essenceBlockEntity.getPlayerInArena().size()));
                        Player player = serverLevel.getPlayerByUUID(playerUUID);

                        neutralMob.setRemainingPersistentAngerTime(Integer.MAX_VALUE);
                        neutralMob.setPersistentAngerTarget(playerUUID);
                        neutralMob.setTarget(player);
                    }
                    else if (entity instanceof Mob mob && !(mob.getTarget() instanceof Player)) {
                        UUID playerUUID = essenceBlockEntity.getPlayerInArena().get(serverLevel.getRandom().nextInt(essenceBlockEntity.getPlayerInArena().size()));
                        Player player = serverLevel.getPlayerByUUID(playerUUID);

                        mob.setTarget(player);
                    }

                    if (Math.abs(entity.blockPosition().getX() - blockPos.getX()) > (essenceBlockEntity.getArenaSize().getX() / 2) ||
                        Math.abs(entity.blockPosition().getY() - blockPos.getY()) > (essenceBlockEntity.getArenaSize().getY() / 2) ||
                        Math.abs(entity.blockPosition().getZ() - blockPos.getZ()) > (essenceBlockEntity.getArenaSize().getZ() / 2))
                    {
                        int yOffset = (-(essenceBlockEntity.getArenaSize().getY()) / 2) + 2;
                        BlockPos center = blockPos.offset(0, yOffset, 0);
                        entity.moveTo(center.getX(), center.getY(), center.getZ());
                    }
                }
            }
        }

        float newProgress = entitiesKilled / ENTITIES_TO_KILL;
        essenceBlockEntity.getEventBar().setProgress(1 - newProgress);
        essenceBlockEntity.setExtraEventTrackingProgress(entitiesKilled);
        if (entitiesKilled == ENTITIES_TO_KILL) {
            EssenceBlockEntity.EndEvent(serverLevel, blockPos, blockState, essenceBlockEntity, true);
        }
    }

    private static void SpawnNewEnemy(ServerLevel serverLevel, BlockPos blockPos, EssenceBlockEntity essenceBlockEntity, int currentEntityCount, List<EssenceBlockEntity.EventEntities> eventEntitiesInArena) {
        TagKey<EntityType<?>> enemyTagToUse = BzTags.ESSENCE_RAGING_ARENA_NORMAL_ENEMY;
        int entityToSpawnIndex = currentEntityCount + 1;
        if ((entityToSpawnIndex % 25) == 0 ||
            entityToSpawnIndex == 49 ||
            entityToSpawnIndex == 73 ||
            entityToSpawnIndex == 74 ||
            entityToSpawnIndex == 97 ||
            entityToSpawnIndex == 98 ||
            entityToSpawnIndex == 99)
        {
            enemyTagToUse = BzTags.ESSENCE_RAGING_ARENA_BOSS_ENEMY; // boss at 25, 49, 50, 73, 74, 75, 97, 98, 99, and 100
        }
        else if ((entityToSpawnIndex % 5) == 0) {
            enemyTagToUse = BzTags.ESSENCE_RAGING_ARENA_STRONG_ENEMY; // strong at every 5 except when boss
        }
        else if ((entityToSpawnIndex % 3) == 0) {
            enemyTagToUse = BzTags.ESSENCE_RAGING_ARENA_RANGED_ENEMY; // ranged at every 3 except when boss or strong
        }

        List<? extends EntityType<?>> entityTypeList = BuiltInRegistries.ENTITY_TYPE
                .getTag(enemyTagToUse)
                .map(holders -> holders
                        .stream()
                        .map(Holder::value)
                        .toList()
                ).orElseGet(ArrayList::new);

        EntityType<?> entityTypeToSpawn = entityTypeList.get(serverLevel.getRandom().nextInt(entityTypeList.size()));
        int yOffset = (-(essenceBlockEntity.getArenaSize().getY()) / 2) + 2;
        Entity entity = entityTypeToSpawn.spawn(serverLevel, blockPos.offset(0, yOffset, 0), MobSpawnType.TRIGGERED);
        if (entity != null) {
            eventEntitiesInArena.add(new EssenceBlockEntity.EventEntities(entity.getUUID()));

            UUID playerUUID = essenceBlockEntity.getPlayerInArena().get(serverLevel.getRandom().nextInt(essenceBlockEntity.getPlayerInArena().size()));
            Player player = serverLevel.getServer().getPlayerList().getPlayer(playerUUID);
            if (player instanceof ServerPlayer serverPlayer) {
                float maxHeart = Math.max(serverPlayer.getHealth(), serverPlayer.getMaxHealth());
                float maxArmor = serverPlayer.getArmorValue();
                float mobHealthBoost = (maxHeart / 15) + (maxArmor / 10);
                float mobAttackBoost = (maxHeart / 20) + (maxArmor / 15);

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

                    AttributeInstance livingEntityAttributeSpeed = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
                    if (livingEntityAttributeSpeed != null) {
                        livingEntityAttributeSpeed.addPermanentModifier(new AttributeModifier(
                                ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "essence_arena_speed_boost"),
                                isEssenced ? 0.05 : 0.065,
                                AttributeModifier.Operation.ADD_VALUE));
                    }

                    AttributeInstance livingEntityAttributeFlyingSpeed = livingEntity.getAttribute(Attributes.FLYING_SPEED);
                    if (livingEntityAttributeFlyingSpeed != null) {
                        livingEntityAttributeFlyingSpeed.addPermanentModifier(new AttributeModifier(
                                ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "essence_arena_flying_speed_boost"),
                                0.065,
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
            }
        }
    }

    @Override
    public void onPlayerEnter(ServerLevel serverLevel, ServerPlayer serverPlayer, EssenceBlockEntity essenceBlockEntity) {
        MusicPacketFromServer.sendToClient(serverPlayer, BzSounds.RAGING_EVENT.get().getLocation(), true);
        super.onPlayerEnter(serverLevel, serverPlayer, essenceBlockEntity);
    }

    @Override
    public void onPlayerLeave(ServerLevel serverLevel, ServerPlayer serverPlayer, EssenceBlockEntity essenceBlockEntity) {
        MusicPacketFromServer.sendToClient(serverPlayer, BzSounds.RAGING_EVENT.get().getLocation(), false);
        super.onPlayerLeave(serverLevel, serverPlayer, essenceBlockEntity);
    }
}
