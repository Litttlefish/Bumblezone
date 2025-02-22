package com.telepathicgrunt.the_bumblezone.items.dispenserbehavior;

import com.telepathicgrunt.the_bumblezone.blocks.HoneycombBrood;
import com.telepathicgrunt.the_bumblezone.fluids.HoneyFluidBlock;
import com.telepathicgrunt.the_bumblezone.mixin.blocks.DefaultDispenseItemBehaviorInvoker;
import com.telepathicgrunt.the_bumblezone.modinit.BzBlocks;
import com.telepathicgrunt.the_bumblezone.modinit.BzFluids;
import com.telepathicgrunt.the_bumblezone.modinit.BzItems;
import com.telepathicgrunt.the_bumblezone.utils.PlatformHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;


public class GlassBottleDispenseBehavior extends DefaultDispenseItemBehavior {
    public static DispenseItemBehavior DEFAULT_GLASS_BOTTLE_DISPENSE_BEHAVIOR;
    public static final DefaultDispenseItemBehavior DROP_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior();

    /**
     * Dispense the specified stack, play the dispense sound and spawn particles.
     */
    @Override
    public ItemStack execute(BlockSource source, ItemStack stack) {
        ServerLevel world = source.level();
        Position dispensePosition = DispenserBlock.getDispensePosition(source);
        BlockPos dispenseBlockPos = BlockPos.containing(dispensePosition);
        BlockState blockstate = world.getBlockState(dispenseBlockPos);

        if (blockstate.getBlock() == BzBlocks.HONEYCOMB_BROOD.get()) {
            // spawn bee if at final stage and front isn't blocked off
            boolean deniedBeeSpawn = false;
            int stage = blockstate.getValue(HoneycombBrood.STAGE);
            if (stage == 3) {
                // the front of the block
                BlockPos.MutableBlockPos blockpos = new BlockPos.MutableBlockPos().set(dispenseBlockPos);
                blockpos.move(blockstate.getValue(HoneycombBrood.FACING).getOpposite());

                // do nothing if front is blocked off
                if (!world.getBlockState(blockpos).isSolid()) {
                    Mob beeEntity = EntityType.BEE.create(world);
                    beeEntity.moveTo(blockpos.getX() + 0.5f, blockpos.getY(), blockpos.getZ() + 0.5f, beeEntity.getRandom().nextFloat() * 360.0F, 0.0F);
                    beeEntity.finalizeSpawn(world, world.getCurrentDifficultyAt(BlockPos.containing(beeEntity.position())), MobSpawnType.TRIGGERED, null);
                    beeEntity.setBaby(true);

                    PlatformHooks.finalizeSpawn(beeEntity, world, null, MobSpawnType.DISPENSER);
                    deniedBeeSpawn = !world.addFreshEntity(beeEntity);
                }
            }

            if (!deniedBeeSpawn) {
                // kill the brood block
                world.setBlockAndUpdate(dispenseBlockPos, BzBlocks.EMPTY_HONEYCOMB_BROOD.get().defaultBlockState()
                        .setValue(BlockStateProperties.FACING, blockstate.getValue(BlockStateProperties.FACING)));
                stack.shrink(1);

                if (!stack.isEmpty())
                    addHoneyBottleToDispenser(source, Items.HONEY_BOTTLE);
                else
                    stack = new ItemStack(Items.HONEY_BOTTLE);
            }
        }
        // remove honey
        else if (blockstate.getBlock() == BzBlocks.FILLED_POROUS_HONEYCOMB.get()) {
            world.setBlockAndUpdate(dispenseBlockPos, BzBlocks.POROUS_HONEYCOMB.get().defaultBlockState());
            stack.shrink(1);

            if (!stack.isEmpty())
                addHoneyBottleToDispenser(source, Items.HONEY_BOTTLE);
            else
                stack = new ItemStack(Items.HONEY_BOTTLE);
        }
        //pick up sugar water
        else if (blockstate.getBlock() == BzFluids.SUGAR_WATER_BLOCK.get() ||
                (blockstate.getBlock() == BzBlocks.HONEY_CRYSTAL.get() && blockstate.getValue(BlockStateProperties.WATERLOGGED))) {
            stack.shrink(1);
            if(!stack.isEmpty())
                addHoneyBottleToDispenser(source, BzItems.SUGAR_WATER_BOTTLE.get());
            else
                stack = new ItemStack(BzItems.SUGAR_WATER_BOTTLE.get());
        }
        //pick up honey fluid
        else if (blockstate.getBlock() == BzFluids.HONEY_FLUID_BLOCK.get() && blockstate.getFluidState().isSource()) {
            world.setBlockAndUpdate(dispenseBlockPos, BzFluids.HONEY_FLUID_FLOWING.get().defaultFluidState().createLegacyBlock().setValue(HoneyFluidBlock.LEVEL, 5));
            stack.shrink(1);
            if(!stack.isEmpty())
                addHoneyBottleToDispenser(source, Items.HONEY_BOTTLE);
            else
                stack = new ItemStack(Items.HONEY_BOTTLE);
        }
        else {
            // If it instanceof DefaultDispenseItemBehavior, call dispenseStack directly to avoid
            // playing particles and sound twice due to dispense method having that by default.
            if(DEFAULT_GLASS_BOTTLE_DISPENSE_BEHAVIOR instanceof DefaultDispenseItemBehavior) {
                return ((DefaultDispenseItemBehaviorInvoker)DEFAULT_GLASS_BOTTLE_DISPENSE_BEHAVIOR).invokeExecute(source, stack);
            }
            else {
                // Fallback to dispense as someone chose to make a custom class without dispenseStack.
                return DEFAULT_GLASS_BOTTLE_DISPENSE_BEHAVIOR.dispense(source, stack);
            }
        }

        return stack;
    }


    /**
     * Play the dispense sound from the specified block.
     */
    @Override
    protected void playSound(BlockSource source) {
        source.level().levelEvent(1002, source.pos(), 0);
    }


    /**
     * Adds honey bottle to dispenser or if no room, dispense it
     */
    private static void addHoneyBottleToDispenser(BlockSource source, Item item) {
        if (source.blockEntity() instanceof DispenserBlockEntity) {
            DispenserBlockEntity dispenser = source.blockEntity();
            ItemStack honeyBottle = new ItemStack(item);
            if (!HopperBlockEntity.addItem(null, dispenser, honeyBottle, null).isEmpty()) {
                DROP_ITEM_BEHAVIOR.dispense(source, honeyBottle);
            }
        }
    }
}
