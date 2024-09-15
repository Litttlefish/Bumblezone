package com.telepathicgrunt.the_bumblezone.utils;

import com.mojang.authlib.GameProfile;
import com.teamresourceful.resourcefullib.common.fluid.data.FluidData;
import com.telepathicgrunt.the_bumblezone.items.BzCustomBucketItem;
import com.telepathicgrunt.the_bumblezone.platform.ModInfo;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * We use @Contract(pure = true) because intellij will think that they always return the same value.
 */
public class PlatformHooks {

    @ExpectPlatform
    @Contract(pure=true)
    public static <T extends Entity> EntityType<T> createEntityType(EntityType.EntityFactory<T> entityFactory, MobCategory category, float size, int clientTrackingRange, int updateInterval, String buildName) {
        throw new NotImplementedException("PlatformHooks createEntityType is not implemented!");
    }

    @ExpectPlatform
    @Contract(pure=true)
    public static <T extends Entity> EntityType<T> createEntityType(EntityType.EntityFactory<T> entityFactory, MobCategory category, float xzSize, float ySize, int clientTrackingRange, int updateInterval, String buildName) {
        throw new NotImplementedException("PlatformHooks createEntityType 2 is not implemented!");
    }

    @ExpectPlatform
    @Contract(pure=true)
    public static <T extends Entity> EntityType<T> createEntityType(EntityType.EntityFactory<T> entityFactory, MobCategory category, float xzSize, float ySize, float eyeHeight, int clientTrackingRange, int updateInterval, String buildName) {
        throw new NotImplementedException("PlatformHooks createEntityType 3 is not implemented!");
    }

    @ExpectPlatform
    @Contract(pure = true)
    public static SpawnGroupData finalizeSpawn(Mob entity, ServerLevelAccessor world, SpawnGroupData spawnGroupData, MobSpawnType spawnReason) {
        throw new NotImplementedException("PlatformHooks canEntitySpawn is not implemented!");
    }

    @ExpectPlatform
    @Contract(pure = true)
    public static ServerPlayer getFakePlayer(ServerLevel level, GameProfile gameProfile) {
        throw new NotImplementedException("PlatformHooks getFakePlayer is not implemented!");
    }

    @ExpectPlatform
    @Contract(pure = true)
    public static boolean isFakePlayer(ServerPlayer player) {
        throw new NotImplementedException("PlatformHooks isFakePlayer is not implemented!");
    }

    @ExpectPlatform
    @Contract(pure = true)
    public static boolean isModLoaded(String modid) {
        throw new NotImplementedException("PlatformHooks isModLoaded is not implemented!");
    }

    @ExpectPlatform
    @Contract(pure = true)
    public static boolean isNeoForge() {
        throw new NotImplementedException("PlatformHooks isNeoForge is not implemented!");
    }

    @ExpectPlatform
    @Contract(pure = true)
    public static int getXpDrop(LivingEntity entity, Player attackingPlayer, int xp) {
        throw new NotImplementedException("PlatformHooks getXpDrop is not implemented!");
    }

    @ExpectPlatform
    @Contract(pure = true)
    public static ItemStack getCraftingRemainder(ItemStack stack) {
        throw new NotImplementedException("PlatformHooks getCraftingRemainder is not implemented!");
    }

    @ExpectPlatform
    @Contract(pure = true)
    public static boolean hasCraftingRemainder(ItemStack stack) {
        throw new NotImplementedException("PlatformHooks hasCraftingRemainder is not implemented!");
    }

    @ExpectPlatform
    @Contract(pure = true)
    public static Fluid getBucketFluid(BucketItem bucket) {
        throw new NotImplementedException("PlatformHooks getBucketFluid is not implemented!");
    }

    public static ModInfo getModInfo(String modid) {
        return getModInfo(modid, false);
    }

    @Nullable
    @ExpectPlatform
    @Contract(pure=true)
    public static ModInfo getModInfo(String modid, boolean qualifierIsVersion) {
        throw new NotImplementedException("PlatformHooks getModInfo is not implemented!");
    }

    @ExpectPlatform
    @Contract(pure=true)
    public static boolean sendBlockBreakEvent(Level level, BlockPos pos, BlockState state, BlockEntity entity, Player player) {
        throw new NotImplementedException("PlatformHooks sendBlockBreakEvent is not implemented!");
    }

    @ExpectPlatform
    @Contract(pure=true)
    public static void afterBlockBreakEvent(Level level, BlockPos pos, BlockState state, BlockEntity entity, Player player) {
        throw new NotImplementedException("PlatformHooks sendBlockBreakEvent is not implemented!");
    }

    @ExpectPlatform
    @Contract(pure=true)
    public static double getFluidHeight(Entity entity, TagKey<Fluid> fallback, FluidData... fluids) {
        throw new NotImplementedException("PlatformHooks getFluidHeight is not implemented!");
    }

    @ExpectPlatform
    @Contract(pure=true)
    public static boolean isEyesInNoFluid(Entity entity) {
        throw new NotImplementedException("PlatformHooks isEyesInNoFluid is not implemented!");
    }

    @ExpectPlatform
    @Contract(pure=true)
    public static InteractionResultHolder<ItemStack> performItemUse(Level world, Player user, InteractionHand hand, Fluid fluid, BzCustomBucketItem bzCustomBucketItem) {
        throw new NotImplementedException("PlatformHooks performItemUse is not implemented!");
    }

    @ExpectPlatform
    @Contract(pure=true)
    public static boolean isPermissionAllowedAtSpot(Level level, Entity entity, BlockPos pos, boolean placingBlock) {
        throw new NotImplementedException("PlatformHooks isPermissionAllowedAtSpot is not implemented!");
    }

    @ExpectPlatform
    @Contract(pure=true)
    public static boolean isDimensionAllowed(ServerPlayer serverPlayer, ResourceKey<Level> dimension) {
        throw new NotImplementedException("PlatformHooks isDimensionAllowed is not implemented!");
    }

    @ExpectPlatform
    @Contract(pure = true)
    public static boolean isItemAbility(ItemStack stack, Class<?> targetBackupClass, String... targetToolAction) {
        throw new NotImplementedException("PlatformHooks isItemAbility is not implemented!");
    }

    @ExpectPlatform
    @Contract(pure=true)
    public static void disableFlight(Player player) {
        throw new NotImplementedException("PlatformHooks disableFlight is not implemented!");
    }

    @ExpectPlatform
    @Contract(pure=true)
    public static boolean isDevEnvironment() {
        throw new NotImplementedException("PlatformHooks isDevEnvironment is not implemented!");
    }

    @ExpectPlatform
    @Contract(pure=true)
    public static boolean isClientEnvironment() {
        throw new NotImplementedException("PlatformHooks isClientEnvironment is not implemented!");
    }

    @ExpectPlatform
    @Contract(pure=true)
    public static boolean shouldMobSplit(Mob parent, List<Mob> children) {
        throw new NotImplementedException("PlatformHooks fireMobSplitEvents is not implemented!");
    }

    @ExpectPlatform
    @Contract(pure = true)
    public static Fluid getBucketItemFluid(BucketItem stack) {
        throw new NotImplementedException("PlatformHooks getBucketItemFluid is not implemented!");
    }

    @ExpectPlatform
    @Contract(pure = true)
    public static RegistryAccess getCurrentRegistryAccess() {
        throw new NotImplementedException("PlatformHooks getCurrentRegistryAccess is not implemented!");
    }
}
