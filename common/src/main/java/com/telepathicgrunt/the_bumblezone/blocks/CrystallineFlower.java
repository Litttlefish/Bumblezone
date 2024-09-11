package com.telepathicgrunt.the_bumblezone.blocks;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.blocks.blockentities.CrystallineFlowerBlockEntity;
import com.telepathicgrunt.the_bumblezone.blocks.datamanagers.CrystallineFlowerDataManager;
import com.telepathicgrunt.the_bumblezone.configs.BzGeneralConfigs;
import com.telepathicgrunt.the_bumblezone.entities.BeeAggression;
import com.telepathicgrunt.the_bumblezone.items.datacomponents.CrystallineFlowerData;
import com.telepathicgrunt.the_bumblezone.menus.CrystallineFlowerMenu;
import com.telepathicgrunt.the_bumblezone.mixin.entities.ExperienceOrbAccessor;
import com.telepathicgrunt.the_bumblezone.mixin.entities.LivingEntityAccessor;
import com.telepathicgrunt.the_bumblezone.modinit.BzBlockEntities;
import com.telepathicgrunt.the_bumblezone.modinit.BzBlocks;
import com.telepathicgrunt.the_bumblezone.modinit.BzDamageSources;
import com.telepathicgrunt.the_bumblezone.modinit.BzDataComponents;
import com.telepathicgrunt.the_bumblezone.modinit.BzItems;
import com.telepathicgrunt.the_bumblezone.modinit.BzParticles;
import com.telepathicgrunt.the_bumblezone.modinit.BzSounds;
import com.telepathicgrunt.the_bumblezone.modinit.BzStats;
import com.telepathicgrunt.the_bumblezone.modinit.BzTags;
import com.telepathicgrunt.the_bumblezone.utils.PlatformHooks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;


public class CrystallineFlower extends BaseEntityBlock {

    public static final MapCodec<CrystallineFlower> CODEC = Block.simpleCodec(CrystallineFlower::new);

    public static final BooleanProperty FLOWER =  BooleanProperty.create("flower");
    protected final VoxelShape shapeFlower = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D);
    protected final VoxelShape shapeBody = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

    private static final Component CONTAINER_TITLE = Component.translatable("container." + Bumblezone.MODID + ".crystalline_flower");
    private static final Component OCCUPIED_CRYSTALLINE_FLOWER_TEXT = Component.translatable("system.the_bumblezone.occupied_crystalline_flower");

    public CrystallineFlower() {
        this(Properties.of()
                .mapColor(MapColor.TERRACOTTA_YELLOW)
                .lightLevel((blockState) -> blockState.getValue(FLOWER) ? 7 : 0)
                .noCollission()
                .noOcclusion()
                .strength(0.4F, 0.01F)
                .pushReaction(PushReaction.DESTROY)
                .sound(BzSounds.HONEY_CRYSTALS_TYPE));
    }


    public CrystallineFlower(Properties properties) {
        super(properties);

        this.registerDefaultState(this.stateDefinition.any().setValue(FLOWER, false));
    }

    @Override
    public MapCodec<? extends CrystallineFlower> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState blockstate, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return (blockstate.hasProperty(FLOWER) && blockstate.getValue(FLOWER)) ? shapeFlower : shapeBody;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add().add(FLOWER);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return BzBlockEntities.CRYSTALLINE_FLOWER.get().create(blockPos, blockState);
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        VoxelShape voxelShape = getShape(state, level, pos, null).move(pos.getX(), pos.getY(), pos.getZ());
        if (!Shapes.joinIsNotEmpty(voxelShape, Shapes.create(entity.getBoundingBox()), BooleanOp.AND) || level.isClientSide()) {
            return;
        }

        if (entity instanceof LivingEntity livingEntity && !BeeAggression.isBeelikeEntity(livingEntity)) {
            float fallDistance = entity.fallDistance;
            livingEntity.makeStuckInBlock(state, new Vec3(0.95F, 2, 0.95F));
            entity.fallDistance = fallDistance;

            if (!level.isClientSide() &&
                (livingEntity.xOld != livingEntity.getX() ||
                livingEntity.yOld != livingEntity.getY() ||
                livingEntity.zOld != livingEntity.getZ()))
            {
                double xDiff = Math.abs(livingEntity.getX() - livingEntity.xOld);
                double yDiff = Math.abs(livingEntity.getY() - livingEntity.yOld);
                double zDiff = Math.abs(livingEntity.getZ() - livingEntity.zOld);
                if (xDiff >= (double)0.001F || yDiff >= (double)0.75F || zDiff >= (double)0.001F) {
                    livingEntity.hurt(level.damageSources().source(BzDamageSources.CRYSTALLINE_FLOWER_TYPE), 1.5f);

                    if (livingEntity.isDeadOrDying() &&
                        !livingEntity.wasExperienceConsumed() &&
                        !((LivingEntityAccessor)livingEntity).callIsAlwaysExperienceDropper() &&
                        livingEntity.shouldDropExperience() &&
                        level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT))
                    {
                        BlockEntity blockEntity = level.getBlockEntity(pos);
                        if (blockEntity instanceof CrystallineFlowerBlockEntity crystallineFlowerBlockEntity && !crystallineFlowerBlockEntity.isMaxTier()) {
                            int reward = PlatformHooks.getXpDrop(livingEntity, null, livingEntity.getExperienceReward((ServerLevel) level, null));
                            ExperienceOrb.award((ServerLevel) level, livingEntity.position(), reward);
                        }
                    }
                }
            }
        }
        else if (entity instanceof ItemEntity itemEntity && BzGeneralConfigs.crystallineFlowerConsumeItemEntities) {
            ItemStack stack = itemEntity.getItem();
            if (stack.is(BzTags.CAN_BE_ENCHANTED_ITEMS) || CrystallineFlowerDataManager.CRYSTALLINE_FLOWER_DATA_MANAGER.disallowConsume.contains(stack.getItem()) || stack.is(BzItems.HONEY_CRYSTAL_SHARDS.get())) {
                return;
            }

            int bottomY = CrystallineFlower.flowerHeightBelow(level, pos);
            BlockPos bottomPos = pos.below(bottomY);
            BlockEntity blockEntity = level.getBlockEntity(bottomPos);
            if (blockEntity instanceof CrystallineFlowerBlockEntity crystallineFlowerBlockEntity && !crystallineFlowerBlockEntity.isMaxTier()) {
                int tiersToMax = 7 - crystallineFlowerBlockEntity.getXpTier();
                int topBlock = CrystallineFlower.flowerHeightAbove(level, crystallineFlowerBlockEntity.getBlockPos());
                List<Boolean> obstructedAbove = CrystallineFlower.getObstructions(tiersToMax, level, crystallineFlowerBlockEntity.getBlockPos().above(topBlock + 1));

                int xpPerCount = getXpPerItem(stack);
                int itemCount = stack.getCount();
                int xpForStack = itemCount * xpPerCount;

                int xpToHighestAvailableTier = getXpToHighestAvailableTier(crystallineFlowerBlockEntity, tiersToMax, obstructedAbove);
                int xpGranted = Math.min(xpToHighestAvailableTier, xpForStack);
                int consumedItemCount = (int) Math.ceil(xpGranted / (float)xpPerCount);
                if (consumedItemCount == 0) {
                    return;
                }

                crystallineFlowerBlockEntity.addXpAndTier(xpGranted);
                stack.shrink(consumedItemCount);
                if (consumedItemCount >= itemCount) {
                    itemEntity.discard();
                }

                if (level instanceof ServerLevel serverLevel) {
                    spawnConsumeParticles(serverLevel, itemEntity.position(), level.random, (consumedItemCount / 3) + 5);
                }
            }
        }
        else if (entity instanceof ExperienceOrb experienceOrb && BzGeneralConfigs.crystallineFlowerConsumeExperienceOrbEntities) {
            int bottomY = CrystallineFlower.flowerHeightBelow(level, pos);
            BlockPos bottomPos = pos.below(bottomY);
            BlockEntity blockEntity = level.getBlockEntity(bottomPos);
            if (blockEntity instanceof CrystallineFlowerBlockEntity crystallineFlowerBlockEntity && !crystallineFlowerBlockEntity.isMaxTier()) {
                int tiersToMax = 7 - crystallineFlowerBlockEntity.getXpTier();
                int topBlock = CrystallineFlower.flowerHeightAbove(level, crystallineFlowerBlockEntity.getBlockPos());
                List<Boolean> obstructedAbove = CrystallineFlower.getObstructions(tiersToMax, level, crystallineFlowerBlockEntity.getBlockPos().above(topBlock + 1));

                int xpToHighestAvailableTier = getXpToHighestAvailableTier(crystallineFlowerBlockEntity, tiersToMax, obstructedAbove);
                int xpGranted = Math.min(xpToHighestAvailableTier, experienceOrb.getValue());

                crystallineFlowerBlockEntity.addXpAndTier(xpGranted);
                ((ExperienceOrbAccessor)experienceOrb).setValue(experienceOrb.getValue() - xpGranted);
                if (experienceOrb.getValue() <= 0) {
                    experienceOrb.discard();
                }

                if (level instanceof ServerLevel serverLevel) {
                    spawnConsumeParticles(serverLevel, experienceOrb.position(), level.random, 3);
                }
            }
        }
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof CrystallineFlowerBlockEntity crystallineFlowerBlockEntity) {
            if (level.players().stream().noneMatch(p ->
                p.containerMenu instanceof CrystallineFlowerMenu crystallineFlowerMenu &&
                crystallineFlowerMenu.crystallineFlowerBlockEntity != null &&
                crystallineFlowerMenu.crystallineFlowerBlockEntity.getUUID().equals(crystallineFlowerBlockEntity.getUUID())))
            {
                if (level.isClientSide) {
                    return InteractionResult.SUCCESS;
                }

                player.openMenu(state.getMenuProvider(level, pos));
                player.awardStat(BzStats.INTERACT_WITH_CRYSTALLINE_FLOWER_RL.get());
                return InteractionResult.CONSUME;
            }
            else if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.displayClientMessage(OCCUPIED_CRYSTALLINE_FLOWER_TEXT, true);
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof CrystallineFlowerBlockEntity crystallineFlowerBlockEntity) {
            crystallineFlowerBlockEntity.setPillar(crystallineFlowerBlockEntity.getXpTier());
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (context.getLevel().getBlockState(context.getClickedPos().below()).is(BzBlocks.CRYSTALLINE_FLOWER.get())) {
            return null;
        }

        BlockState defaultState = super.getStateForPlacement(context);
        return defaultState == null ? null : defaultState.setValue(FLOWER, isFlowerSpot(context.getLevel(), context.getClickedPos()));
    }

    @Override
    public void neighborChanged(BlockState blockstate, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (canSurvive(blockstate, level, pos)) {
            boolean flowerSpot = isFlowerSpot(level, pos);
            if (flowerSpot != blockstate.getValue(FLOWER)) {
                level.setBlock(pos, blockstate.setValue(FLOWER, flowerSpot), 3);
            }
        }
        else {
            level.destroyBlock(pos, blockstate.hasProperty(FLOWER) && blockstate.getValue(FLOWER), null, 1);
        }

        super.neighborChanged(blockstate, level, pos, block, fromPos, notify);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState belowState = level.getBlockState(pos.below());
        boolean isBelowValid =
                (belowState.is(BzTags.CRYSTALLINE_FLOWER_CAN_SURVIVE_ON) && belowState.isFaceSturdy(level, pos, Direction.UP)) ||
                        belowState.is(BzBlocks.CRYSTALLINE_FLOWER.get());

        int flowerBlockAbove = flowerHeightAbove(level, pos);
        int flowerBlockBelow = flowerHeightBelow(level, pos);

        BlockEntity operatingBE = level.getBlockEntity(pos.below(flowerBlockBelow));
        if (operatingBE instanceof CrystallineFlowerBlockEntity crystallineFlowerBlockEntity) {
            return isBelowValid && flowerBlockBelow + flowerBlockAbove + 1 == crystallineFlowerBlockEntity.getXpTier();
        }
        else {
            return isBelowValid && flowerBlockBelow + flowerBlockAbove + 1 <= 7;
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        if (state.hasProperty(FLOWER) && state.getValue(FLOWER)) {
            return super.getDrops(state, builder);
        }
        return new ArrayList<>();
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
        int flowerBlockBelow = flowerHeightBelow(level, pos);
        BlockPos bottomPos = pos.below(flowerBlockBelow);
        BlockEntity blockEntity = level.getBlockEntity(bottomPos);
        if (blockEntity instanceof CrystallineFlowerBlockEntity crystallineFlowerBlockEntity) {
            return crystallineFlowerBlockEntity.getXpTier();
        }

        return 0;
    }

    public static boolean isFlowerSpot(Level level, BlockPos pos) {
        return !level.getBlockState(pos.above()).is(BzBlocks.CRYSTALLINE_FLOWER.get());
    }

    public static boolean isBottomSpot(Level level, BlockPos pos) {
        return !level.getBlockState(pos.below()).is(BzBlocks.CRYSTALLINE_FLOWER.get());
    }

    public static int flowerTotalHeight(LevelReader level, BlockPos pos) {
        return flowerHeightBelow(level, pos) + flowerHeightAbove(level, pos) + 1;
    }

    public static int flowerHeightBelow(LevelReader level, BlockPos pos) {
        int i = 0;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        BlockState currentState = level.getBlockState(mutable.set(pos.below()));
        while (currentState.is(BzBlocks.CRYSTALLINE_FLOWER.get()) && mutable.getY() >= level.getMinBuildHeight()) {
            i++;
            mutable.move(Direction.DOWN);
            currentState = level.getBlockState(mutable);
        }
        return i;
    }

    public static int flowerHeightAbove(LevelReader level, BlockPos pos) {
        int i = 0;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        BlockState currentState = level.getBlockState(mutable.set(pos.above()));
        while (currentState.is(BzBlocks.CRYSTALLINE_FLOWER.get()) && mutable.getY() < level.getMaxBuildHeight()) {
            i++;
            mutable.move(Direction.UP);
            currentState = level.getBlockState(mutable);
        }
        return i;
    }

    public static List<Boolean> getObstructions(int scanArea, Level level, BlockPos pos) {
        List<Boolean> obstructions = new ArrayList<>();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        mutable.set(pos);

        for (int i = 0; i < scanArea; i++) {
            if (mutable.getY() >= level.getMaxBuildHeight()) {
                obstructions.add(true);
            }
            else {
                BlockState currentState = level.getBlockState(mutable);
                if (!(currentState.isAir() || currentState.is(BzTags.AIR_LIKE)) && !currentState.is(BzBlocks.CRYSTALLINE_FLOWER.get())) {
                    obstructions.add(true);
                }
                else {
                    obstructions.add(false);
                }
            }

            mutable.move(Direction.UP);
        }

        return obstructions;
    }

    public static int getXpPerItem(ItemStack stack) {
        if (CrystallineFlowerDataManager.CRYSTALLINE_FLOWER_DATA_MANAGER.disallowConsume.contains(stack.getItem())) {
            return 0;
        }

        Pair<Integer, Boolean> xpValue = CrystallineFlowerDataManager.CRYSTALLINE_FLOWER_DATA_MANAGER.itemToXp.get(stack.getItem());
        if (xpValue != null) {
            return xpValue.getSecond() ? Integer.MAX_VALUE : xpValue.getFirst();
        }

        return CrystallineFlowerDataManager.CRYSTALLINE_FLOWER_DATA_MANAGER.allowNormalConsumption ? 1 : 0;
    }

    public static int getXpToHighestAvailableTier(CrystallineFlowerBlockEntity crystallineFlowerBlockEntity, int tiersToMax, List<Boolean> obstructedAbove) {
        int xpToHighestAvailableTier = 0;
        for (int i = 1; i <= tiersToMax; i++) {
            int xpToDesiredTier = crystallineFlowerBlockEntity.getXpForNextTiers(i);
            if (i - 1 < obstructedAbove.size() && obstructedAbove.get(i - 1)) {
                xpToHighestAvailableTier = xpToDesiredTier - 1;
                break;
            }
            xpToHighestAvailableTier = xpToDesiredTier;
        }
        return xpToHighestAvailableTier;
    }

    @Override
    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        int flowerBlockBelow = flowerHeightBelow(level, pos);
        BlockPos bottomPlant = pos.below(flowerBlockBelow);
        BlockEntity blockEntity = level.getBlockEntity(bottomPlant);

        CrystallineFlowerBlockEntity finalCrystallineFlowerBlockEntity = blockEntity instanceof CrystallineFlowerBlockEntity ? (CrystallineFlowerBlockEntity) blockEntity : null;
        return new SimpleMenuProvider(
                (containerId, inventory, player) -> new CrystallineFlowerMenu(
                        containerId,
                        inventory,
                        ContainerLevelAccess.create(level, pos.below(flowerBlockBelow)),
                        finalCrystallineFlowerBlockEntity
                ), CONTAINER_TITLE);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(itemStack, tooltipContext, tooltip, flag);
        CrystallineFlowerData flowerData = itemStack.getOrDefault(BzDataComponents.CRYSTALLINE_FLOWER_DATA.get(), new CrystallineFlowerData());
        if (flowerData.uuid().compareTo(CrystallineFlowerData.DEFAULT_UUID) != 0 && flowerData.tier() != 0) {
            tooltip.add(Component.translatable("item.the_bumblezone.crystalline_flower_info_1", flowerData.tier()).withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.ITALIC));
            tooltip.add(Component.translatable("item.the_bumblezone.crystalline_flower_info_2", flowerData.experience()).withStyle(ChatFormatting.DARK_PURPLE).withStyle(ChatFormatting.ITALIC));
        }
    }

    private void spawnConsumeParticles(Level world, Vec3 position, RandomSource random, int particleCount) {
        ((ServerLevel)world).sendParticles(ParticleTypes.HAPPY_VILLAGER,
                position.x(),
                position.y() + 0.5d,
                position.z(),
                particleCount,
                random.nextDouble() / 8 + 0.2d,
                random.nextDouble() / 8 + 0.2d,
                random.nextDouble() / 8 + 0.2d,
                0.1D);
    }

    @Override
    public void animateTick(BlockState blockState, Level world, BlockPos position, RandomSource random) {
        boolean flower = blockState.getValue(FLOWER);
        if (random.nextFloat() < (flower ? 0.15f : 0.05f)) {
            this.spawnSparkleParticles(world, position, random, flower);
        }

        if (flower) {
            world.addParticle(
                    BzParticles.POLLEN_PARTICLE.get(),
                    (double)position.getX() + 0.5d,
                    (double)position.getY() + 0.1d,
                    (double)position.getZ() + 0.5d,
                    random.nextGaussian() * 0.005d,
                    random.nextGaussian() * 0.005d + 0.005d,
                    random.nextGaussian() * 0.005d);
        }
    }

    private void spawnSparkleParticles(Level world, BlockPos position, RandomSource random, boolean flower) {
        int min = flower ? 1 : 3;
        int max = flower ? 15 : 13;
        double minRatio = min / 16d;
        double x;
        double y;
        double z;
        if (flower) {
            int chosenFace = random.nextInt(3);
            boolean xB = random.nextBoolean();
            boolean yB = random.nextBoolean();
            boolean zB = random.nextBoolean();
            x = random.nextDouble() + (xB ? min : max) * (chosenFace != 0 ? random.nextDouble() * (1 - minRatio) : 1) + (chosenFace != 0 ? min : 0);
            y = random.nextDouble() + (yB ? 0 : 9) * (chosenFace != 1 ? random.nextDouble() : 1);
            z = random.nextDouble() + (zB ? min : max) * (chosenFace != 2 ? random.nextDouble() * (1 - minRatio) : 1) + (chosenFace != 2 ? min : 0);
        }
        else {
            int chosenFace = random.nextInt(2);
            boolean xB = random.nextBoolean();
            boolean zB = random.nextBoolean();
            x = random.nextDouble() + (xB ? min : max) * (chosenFace != 0 ? random.nextDouble() * (1 - minRatio) : 1) + (chosenFace != 0 ? min : 0);
            y = random.nextDouble() * 16;
            z = random.nextDouble() + (zB ? min : max) * (chosenFace != 1 ? random.nextDouble() * (1 - minRatio) : 1) + (chosenFace != 1 ? min : 0);
        }

        world.addParticle(BzParticles.SPARKLE_PARTICLE.get(),
                (x / 16) + position.getX(),
                (y / 16) + position.getY(),
                (z / 16) + position.getZ(),
                0.0D,
                0.0D,
                0.0D);
    }
}
