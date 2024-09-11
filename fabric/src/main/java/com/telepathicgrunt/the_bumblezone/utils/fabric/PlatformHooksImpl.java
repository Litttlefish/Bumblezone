package com.telepathicgrunt.the_bumblezone.utils.fabric;

import com.mojang.authlib.GameProfile;
import com.teamresourceful.resourcefullib.common.fluid.data.FluidData;
import com.telepathicgrunt.the_bumblezone.client.utils.GeneralUtilsClient;
import com.telepathicgrunt.the_bumblezone.items.BzCustomBucketItem;
import com.telepathicgrunt.the_bumblezone.mixin.fabric.entity.EntityAccessor;
import com.telepathicgrunt.the_bumblezone.mixin.fabric.item.BucketItemAccessor;
import com.telepathicgrunt.the_bumblezone.mixin.items.ItemAccessor;
import com.telepathicgrunt.the_bumblezone.modcompat.ModChecker;
import com.telepathicgrunt.the_bumblezone.modcompat.fabric.RestrictedPortalsCompat;
import com.telepathicgrunt.the_bumblezone.platform.ModInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Contract;

import java.util.List;

public class PlatformHooksImpl {

    public static <T extends Mob> EntityType<T> createEntityType(EntityType.EntityFactory<T> entityFactory, MobCategory category, float size, boolean scalable, int clientTrackingRange, int updateInterval, String buildName) {
        return FabricEntityTypeBuilder
                .<T>createMob()
                .spawnGroup(category)
                .entityFactory(entityFactory)
                .dimensions(scalable ? EntityDimensions.scalable(size, size) : EntityDimensions.fixed(size, size))
                .trackRangeChunks(clientTrackingRange)
                .trackedUpdateRate(updateInterval)
                .build();
    }

    public static <T extends Mob> EntityType<T> createEntityType(EntityType.EntityFactory<T> entityFactory, MobCategory category, float xzSize, float ySize, boolean scalable, int clientTrackingRange, int updateInterval, String buildName) {
        return FabricEntityTypeBuilder
                .<T>createMob()
                .spawnGroup(category)
                .entityFactory(entityFactory)
                .dimensions(scalable ? EntityDimensions.scalable(xzSize, ySize) : EntityDimensions.fixed(xzSize, ySize))
                .trackRangeChunks(clientTrackingRange)
                .trackedUpdateRate(updateInterval)
                .build();
    }

    public static <T extends Mob> EntityType<T> createEntityType(EntityType.EntityFactory<T> entityFactory, MobCategory category, float xzSize, float ySize, float eyeHeight, boolean scalable, int clientTrackingRange, int updateInterval, String buildName) {
        return FabricEntityTypeBuilder
                .<T>createMob()
                .spawnGroup(category)
                .entityFactory(entityFactory)
                .dimensions(scalable ? EntityDimensions.scalable(xzSize, ySize).withEyeHeight(eyeHeight) : EntityDimensions.fixed(xzSize, ySize).withEyeHeight(eyeHeight))
                .trackRangeChunks(clientTrackingRange)
                .trackedUpdateRate(updateInterval)
                .build();
    }

    public static ModInfo getModInfo(String modid, boolean qualifierIsVersion) {
        return FabricLoader.getInstance()
                .getModContainer(modid)
                .map(container -> new FabricModInfo(container.getMetadata()))
                .orElse(null);
    }

    @Contract(pure = true)
    public static Fluid getBucketFluid(BucketItem bucket) {
        Fluid fluid = ((BucketItemAccessor) bucket).bz$getContents();
        return fluid == null ? Fluids.EMPTY : fluid;
    }

    @Contract(pure = true)
    public static boolean hasCraftingRemainder(ItemStack stack) {
        return stack.getItem().hasCraftingRemainingItem();
    }

    @Contract(pure = true)
    public static ItemStack getCraftingRemainder(ItemStack stack) {
        final Item item = stack.getItem().getCraftingRemainingItem();
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    @Contract(pure = true)
    public static int getXpDrop(LivingEntity entity, Player attackingPlayer, int xp) {
        return xp;
    }

    @Contract(pure = true)
    public static boolean isModLoaded(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }

    @Contract(pure = true)
    public static boolean isNeoForge() {
        return false;
    }

    @Contract(pure = true)
    public static boolean isFakePlayer(ServerPlayer player) {
        //Crude way of doing it but it should work for almost all cases.
        return player != null && player.getClass() != ServerPlayer.class;
    }

    @Contract(pure = true)
    public static ServerPlayer getFakePlayer(ServerLevel level, GameProfile gameProfile) {
        if (gameProfile == null) {
            return FakePlayer.get(level);
        }
        return FakePlayer.get(level, gameProfile);
    }

    @Contract(pure = true)
    public static SpawnGroupData finalizeSpawn(Mob entity, ServerLevelAccessor world, SpawnGroupData spawnGroupData, MobSpawnType spawnReason) {
        return entity.finalizeSpawn(
                world,
                world.getCurrentDifficultyAt(entity.blockPosition()),
                spawnReason,
                spawnGroupData);
    }

    public static boolean sendBlockBreakEvent(Level level, BlockPos pos, BlockState state, BlockEntity entity, Player player) {
        boolean result = PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(level, player, pos, state, entity);
        if (!result) {
            PlayerBlockBreakEvents.CANCELED.invoker().onBlockBreakCanceled(level, player, pos, state, entity);
            return true;
        }
        return false;
    }

    public static void afterBlockBreakEvent(Level level, BlockPos pos, BlockState state, BlockEntity entity, Player player) {
        PlayerBlockBreakEvents.AFTER.invoker().afterBlockBreak(level, player, pos, state, entity);
    }

    public static double getFluidHeight(Entity entity, TagKey<Fluid> fallback, FluidData... fluids) {
        return entity.getFluidHeight(fallback);
    }

    public static boolean isEyesInNoFluid(Entity entity) {
        return ((EntityAccessor)entity).getFluidOnEyes().isEmpty();
    }

    public static  InteractionResultHolder<ItemStack> performItemUse(Level world, Player user, InteractionHand hand, Fluid fluid, BzCustomBucketItem bzCustomBucketItem) {
        ItemStack itemStack = user.getItemInHand(hand);
        BlockHitResult blockHitResult = ItemAccessor.bumblezone$callGetPlayerPOVHitResult(world, user, fluid == Fluids.EMPTY ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);
        if (blockHitResult.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemStack);
        }
        else if (blockHitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(itemStack);
        }
        else {
            BlockPos blockPos = blockHitResult.getBlockPos();
            Direction direction = blockHitResult.getDirection();
            BlockPos blockPos2 = blockPos.relative(direction);
            if (world.mayInteract(user, blockPos) && user.mayUseItemAt(blockPos2, direction, itemStack)) {
                BlockState blockState;
                if (fluid == Fluids.EMPTY) {
                    blockState = world.getBlockState(blockPos);
                    if (blockState.getBlock() instanceof BucketPickup fluidDrainable) {
                        ItemStack itemStack2 = fluidDrainable.pickupBlock(user, world, blockPos, blockState);
                        if (!itemStack2.isEmpty()) {
                            user.awardStat(Stats.ITEM_USED.get(bzCustomBucketItem));
                            fluidDrainable.getPickupSound().ifPresent((sound) -> user.playSound(sound, 1.0F, 1.0F));
                            world.gameEvent(user, GameEvent.FLUID_PICKUP, blockPos);
                            ItemStack itemStack3 = ItemUtils.createFilledResult(itemStack, user, itemStack2);
                            if (user instanceof ServerPlayer serverPlayer) {
                                CriteriaTriggers.FILLED_BUCKET.trigger(serverPlayer, itemStack2);
                            }

                            return InteractionResultHolder.sidedSuccess(itemStack3, world.isClientSide());
                        }
                    }

                    return InteractionResultHolder.fail(itemStack);
                }
                else {
                    blockState = world.getBlockState(blockPos);
                    BlockPos blockPos3 = blockState.getBlock() instanceof LiquidBlockContainer && fluid.is(FluidTags.WATER) ? blockPos : blockPos2;
                    if (bzCustomBucketItem.emptyContents(user, world, blockPos3, blockHitResult)) {
                        bzCustomBucketItem.checkExtraContent(user, world, itemStack, blockPos3);
                        if (user instanceof ServerPlayer serverPlayer) {
                            CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, blockPos3, itemStack);
                        }

                        user.awardStat(Stats.ITEM_USED.get(bzCustomBucketItem));
                        return InteractionResultHolder.sidedSuccess(BucketItem.getEmptySuccessItem(itemStack, user), world.isClientSide());
                    }
                    else {
                        return InteractionResultHolder.fail(itemStack);
                    }
                }
            }
            else {
                return InteractionResultHolder.fail(itemStack);
            }
        }
    }
    public static boolean isPermissionAllowedAtSpot(Level level, Entity entity, BlockPos pos, boolean placingBlock) {
        if (placingBlock) {
            if (entity instanceof Player player) {
                return player.mayInteract(level, pos) && PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(level, player, pos, level.getBlockState(pos), null);
            }
        }
        else if (entity instanceof Player player) {
            return player.mayInteract(level, pos) && PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(level, player, pos, level.getBlockState(pos), null);
        }
        return true;
    }

    public static boolean isDimensionAllowed(ServerPlayer serverPlayer, ResourceKey<Level> dimension) {
        if (ModChecker.restrictedPortalsPresent) {
            return !RestrictedPortalsCompat.isDimensionDisallowed(serverPlayer, dimension);
        }

        return true;
    }

    public static boolean isItemAbility(ItemStack stack, Class<?> targetBackupClass, String... targetToolAction) {
        return targetBackupClass != null && targetBackupClass.isInstance(stack.getItem());
    }

    public static void disableFlight(Player player) {
        player.getAbilities().flying = false;
        player.getAbilities().mayfly = false;

        if (player.level().isClientSide()) {
            return;
        }

        // Sync on server only
        player.onUpdateAbilities();
    }

    public static boolean isDevEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    public static boolean isClientEnvironment() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    public static boolean shouldMobSplit(Mob parent, List<Mob> children) { return true; }

    public static Fluid getBucketItemFluid(BucketItem stack) {
        return ((BucketItemAccessor)stack).bz$getContents();
    }

    public static MinecraftServer currentMinecraftServer = null;
    public static RegistryAccess getCurrentRegistryAccess() {
        try {
            if (currentMinecraftServer == null || !currentMinecraftServer.isSameThread()) {
                return GeneralUtilsClient.getClientRegistryAccess();
            }
        }
        catch (Throwable ignored) {}

        return currentMinecraftServer.registryAccess();
    }
}
