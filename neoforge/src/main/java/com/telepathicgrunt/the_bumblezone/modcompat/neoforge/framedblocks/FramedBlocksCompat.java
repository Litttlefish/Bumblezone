package com.telepathicgrunt.the_bumblezone.modcompat.neoforge.framedblocks;

import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.blocks.AncientWax;
import com.telepathicgrunt.the_bumblezone.blocks.CarvableWax;
import com.telepathicgrunt.the_bumblezone.blocks.LuminescentWaxBase;
import com.telepathicgrunt.the_bumblezone.mixin.neoforge.block.UseOnContextAccessor;
import com.telepathicgrunt.the_bumblezone.modcompat.ModChecker;
import com.telepathicgrunt.the_bumblezone.modcompat.ModCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.camo.CamoContainerFactory;
import xfacthd.framedblocks.api.camo.block.AbstractBlockCamoContainer;
import xfacthd.framedblocks.api.util.FramedConstants;

public final class FramedBlocksCompat implements ModCompat {

    private static final DeferredRegister<CamoContainerFactory<?>> CAMO_FACTORIES = DeferredRegister.create(
            FramedConstants.CAMO_CONTAINER_FACTORY_REGISTRY_NAME,
            Bumblezone.MODID
    );
    static final DeferredHolder<CamoContainerFactory<?>, CarvableWaxBlockCamoContainerFactory> WAX_BLOCK_CAMO_FACTORY =
            CAMO_FACTORIES.register("carvable_wax", CarvableWaxBlockCamoContainerFactory::new);

    public FramedBlocksCompat(IEventBus modEventBus) {
        CAMO_FACTORIES.register(modEventBus);
        NeoForge.EVENT_BUS.addListener(FramedBlocksCompat::onItemUsedOnBlock);

        ModChecker.framedBlocksPresent = true;
    }

    /**
     * Event handler for managing custom interactions with carvable wax, ancient wax and luminescent wax when applied
     * as a camo to a framed block. To do so, the camo is retrieved from the framed block via the player's interaction
     * point on the block, the stored state is modified as necessary and then a new camo container with the modified
     * state is written back to the framed block
     */
    private static void onItemUsedOnBlock(UseItemOnBlockEvent event) {
        Level level = event.getLevel();
        Player player = event.getPlayer();
        BlockPos pos = event.getPos();
        if (player == null || !(level.getBlockEntity(pos) instanceof FramedBlockEntity be)) {
            return;
        }

        BlockHitResult hit = ((UseOnContextAccessor) event.getUseOnContext()).bz$getHitResult();
        CamoContainer<?, ?> camo = be.getCamo(hit, player);
        if (camo instanceof CarvableWaxBlockCamoContainer waxCamo && waxCamo.getState().getBlock() instanceof CarvableWax wax) {
            BlockState carvedState = wax.tryCarve(event.getItemStack(), waxCamo.getState(), level, pos, player, event.getHand());
            if (carvedState != null) {
                if (!level.isClientSide()) {
                    be.setCamo(waxCamo.copyWithState(carvedState), hit, player);
                }
                event.cancelWithResult(ItemInteractionResult.sidedSuccess(level.isClientSide()));
            }
        }
        else if (camo instanceof AbstractBlockCamoContainer<?> blockCamo) {
            if (blockCamo.getState().getBlock() instanceof LuminescentWaxBase lumiWax) {
                BlockState rotatedState = lumiWax.tryRotate(event.getItemStack(), blockCamo.getState(), level, pos, player, event.getHand());
                if (rotatedState != null) {
                    if (!level.isClientSide()) {
                        be.setCamo(blockCamo.copyWithState(rotatedState), hit, player);
                    }
                    event.cancelWithResult(ItemInteractionResult.sidedSuccess(level.isClientSide()));
                }
            }
            else if (blockCamo.getState().getBlock() instanceof AncientWax ancientWax) {
                BlockState swappedState = ancientWax.trySwap(event.getItemStack(), blockCamo.getState(), level, pos, player, event.getHand());
                if (swappedState != null) {
                    if (!level.isClientSide()) {
                        be.setCamo(blockCamo.copyWithState(swappedState), hit, player);
                    }
                    event.cancelWithResult(ItemInteractionResult.sidedSuccess(level.isClientSide()));
                }
            }
        }
    }
}
