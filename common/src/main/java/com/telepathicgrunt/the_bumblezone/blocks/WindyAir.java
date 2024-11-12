package com.telepathicgrunt.the_bumblezone.blocks;

import com.google.common.collect.MapMaker;
import com.mojang.serialization.MapCodec;
import com.telepathicgrunt.the_bumblezone.mixin.gameplay.ServerGamePacketListenerImplAccessor;
import com.telepathicgrunt.the_bumblezone.modinit.BzItems;
import com.telepathicgrunt.the_bumblezone.modinit.BzParticles;
import com.telepathicgrunt.the_bumblezone.modinit.BzSounds;
import com.telepathicgrunt.the_bumblezone.modinit.BzStats;
import com.telepathicgrunt.the_bumblezone.modinit.BzTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;


public class WindyAir extends ProperFacingBlock {
    private static final ConcurrentMap<String, Map<Direction, Integer>> APPLIED_PUSH_FOR_ENTITY = new MapMaker().concurrencyLevel(2).weakKeys().makeMap();

    public static final MapCodec<WindyAir> CODEC = Block.simpleCodec(WindyAir::new);

    public WindyAir() {
        this(Properties.of()
                .strength(0.05f, 0)
                .air()
                .noCollission()
                .replaceable()
                .noLootTable()
                .noOcclusion()
                .pushReaction(PushReaction.DESTROY));
    }

    public WindyAir(Properties properties) {
        super(properties);

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Override
    public MapCodec<? extends WindyAir> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add().add(FACING);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return collisionContext.isHoldingItem(BzItems.WINDY_AIR.get()) ? Shapes.block() : Shapes.empty();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return Shapes.empty();
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public float getShadeBrightness(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return 1.0f;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    @Override
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        if (entity instanceof Player player) {
            if ((player.isCreative() && player.getAbilities().flying) || player.isSpectator()) {
                return;
            }
        }
        else if (entity.getType().is(BzTags.WINDY_AIR_IMMUNE)) {
            return;
        }

        if (APPLIED_PUSH_FOR_ENTITY.size() >= 200) {
            APPLIED_PUSH_FOR_ENTITY.clear();
        }

        if (!APPLIED_PUSH_FOR_ENTITY.containsKey(entity.getStringUUID())) {
            APPLIED_PUSH_FOR_ENTITY.put(entity.getStringUUID(), new HashMap<>());
        }

        Direction windDirection = blockState.getValue(FACING);
        if (APPLIED_PUSH_FOR_ENTITY.get(entity.getStringUUID()).getOrDefault(windDirection, -1) == entity.tickCount) {
            return;
        }

        double strength = windDirection == Direction.UP ? 0.089D : 0.0275D;
        double size = entity.getBoundingBox().getSize();
        if (size <= 1) {
            strength = strength * (1 / (size / 2 + 0.5D));
        }
        else {
            strength = strength * (1 / (size * 2));
        }

        if (entity instanceof ItemEntity) {
            strength *= windDirection == Direction.UP ? 0.9f : 0.7f;
        }
        else if (entity instanceof Mob) {
            strength *= windDirection == Direction.UP ? 2.0f : 0.7f;
        }

        Vec3 pushPower = Vec3.atLowerCornerOf(windDirection.getNormal()).scale(strength);
        Vec3 newVelocity = entity.getDeltaMovement();
        if (entity instanceof ItemEntity) {
            newVelocity = newVelocity.add(newVelocity.scale(-0.15f));
        }

        newVelocity = newVelocity.add(pushPower);
        if (!entity.onGround() && newVelocity.y() < 0 && windDirection != Direction.DOWN) {
            newVelocity = newVelocity.add(0, -newVelocity.y() + 0.04F, 0);
        }

        if (windDirection == Direction.UP && newVelocity.y() > -0.05) {
            entity.fallDistance = 0;
        }

        entity.setDeltaMovement(newVelocity);
        APPLIED_PUSH_FOR_ENTITY.get(entity.getStringUUID()).put(windDirection, entity.tickCount);

        if (entity instanceof Player player) {
            player.awardStat(BzStats.WINDY_AIR_TIME_RL.get());
        }

        if (windDirection != Direction.DOWN && entity instanceof ServerPlayer serverPlayer) {
            ((ServerGamePacketListenerImplAccessor)serverPlayer.connection).setAboveGroundTickCount(0);
            ((ServerGamePacketListenerImplAccessor)serverPlayer.connection).setAboveGroundVehicleTickCount(0);
        }
    }

    @Deprecated
    public void onExplosionHit(BlockState blockState, Level level, BlockPos blockPos, Explosion explosion, BiConsumer<ItemStack, BlockPos> biConsumer) {
        if (explosion.getBlockInteraction() == Explosion.BlockInteraction.TRIGGER_BLOCK) {
            return;
        }
        Block block = blockState.getBlock();
        boolean bl = explosion.getIndirectSourceEntity() instanceof Player;
        if (block.dropFromExplosion(explosion) && level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            BlockEntity blockEntity = blockState.hasBlockEntity() ? level.getBlockEntity(blockPos) : null;
            LootParams.Builder builder = new LootParams.Builder(serverLevel).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockPos)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity).withOptionalParameter(LootContextParams.THIS_ENTITY, explosion.getDirectSourceEntity());
            if (explosion.getBlockInteraction() == Explosion.BlockInteraction.DESTROY_WITH_DECAY) {
                builder.withParameter(LootContextParams.EXPLOSION_RADIUS, Float.valueOf(explosion.radius()));
            }
            blockState.spawnAfterBreak(serverLevel, blockPos, ItemStack.EMPTY, bl);
            blockState.getDrops(builder).forEach(itemStack -> biConsumer.accept(itemStack, blockPos));
        }
        level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
        block.wasExploded(level, blockPos, explosion);
    }

    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
        if (randomSource.nextFloat() < 0.25f) {
            Direction windDirection = blockState.getValue(FACING);
            double strength = 0.1D;
            Vec3 pushPower = Vec3.atLowerCornerOf(windDirection.getNormal()).scale(strength);

            level.addParticle(
                    BzParticles.WIND_PARTICLE.get(),
                    (double)blockPos.getX() + randomSource.nextDouble(),
                    (double)blockPos.getY() + randomSource.nextDouble(),
                    (double)blockPos.getZ() + randomSource.nextDouble(),
                    pushPower.x() + (randomSource.nextGaussian() * 0.003d),
                    pushPower.y() + (randomSource.nextGaussian() * 0.003d),
                    pushPower.z() + (randomSource.nextGaussian() * 0.003d));

            Player nearestPlayer = level.getNearestPlayer(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 3, true);
            if (nearestPlayer != null) {
                level.playSound(null, blockPos, BzSounds.WINDY_AIR_BLOWS.get(), SoundSource.AMBIENT, (randomSource.nextFloat() * 0.05F) + 0.5F, (randomSource.nextFloat() * 0.1F) + 0.8F);
            }
        }
    }

    @Override
    protected void spawnDestroyParticles(Level level, Player player, BlockPos blockPos, BlockState blockState) {
        VoxelShape voxelshape = Shapes.block();
        voxelshape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> {
            double d1 = Math.min(1.0, x2 - x1);
            double d2 = Math.min(1.0, y2 - y1);
            double d3 = Math.min(1.0, z2 - z1);
            int i = Math.max(2, Mth.ceil(d1 / 0.25));
            int j = Math.max(2, Mth.ceil(d2 / 0.25));
            int k = Math.max(2, Mth.ceil(d3 / 0.25));

            for(int x = 0; x < i; ++x) {
                for(int y = 0; y < j; ++y) {
                    for(int z = 0; z < k; ++z) {
                        double d4 = ((double)x + 0.5) / (double)i;
                        double d5 = ((double)y + 0.5) / (double)j;
                        double d6 = ((double)z + 0.5) / (double)k;
                        double d7 = d4 * d1 + x1;
                        double d8 = d5 * d2 + y1;
                        double d9 = d6 * d3 + z1;
                        level.addParticle(
                            new BlockParticleOption(ParticleTypes.BLOCK, blockState),
                            (double)blockPos.getX() + d7,
                            (double)blockPos.getY() + d8,
                            (double)blockPos.getZ() + d9,
                            d4 - 0.5,
                            d5 - 0.5,
                            d6 - 0.5
                        );
                    }
                }
            }
        });
    }
}
