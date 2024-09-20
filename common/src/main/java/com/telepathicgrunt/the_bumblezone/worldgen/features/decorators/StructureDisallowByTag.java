package com.telepathicgrunt.the_bumblezone.worldgen.features.decorators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.telepathicgrunt.the_bumblezone.modinit.BzPlacements;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class StructureDisallowByTag extends PlacementModifier {
    private final Optional<TagKey<Structure>> disallowTag;
    private final boolean piecewiseCheck;
    private final Optional<Integer> minY;
    private final Optional<Integer> maxY;

    public static final MapCodec<StructureDisallowByTag> CODEC = RecordCodecBuilder.mapCodec((configInstance) -> configInstance.group(
            TagKey.codec(Registries.STRUCTURE).optionalFieldOf("disallow_tag").forGetter(structureDisallowByTag -> structureDisallowByTag.disallowTag),
            Codec.BOOL.fieldOf("piecewise_check").forGetter(structureDisallowByTag -> structureDisallowByTag.piecewiseCheck),
            Codec.INT.optionalFieldOf("min_y").forGetter(structureDisallowByTag -> structureDisallowByTag.minY),
            Codec.INT.optionalFieldOf("max_y").forGetter(structureDisallowByTag -> structureDisallowByTag.maxY)
    ).apply(configInstance, StructureDisallowByTag::new));

    private StructureDisallowByTag(Optional<TagKey<Structure>> disallowTag,
                                   boolean piecewiseCheck,
                                   Optional<Integer> minY,
                                   Optional<Integer> maxY) {
        this.disallowTag = disallowTag;
        this.piecewiseCheck = piecewiseCheck;
        this.minY = minY;
        this.maxY = maxY;
    }

    @Override
    public PlacementModifierType<?> type() {
        return BzPlacements.STRUCTURE_DISALLOW_BY_TAG.get();
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext placementContext, RandomSource random, BlockPos blockPos) {

        if (placementContext.getLevel() instanceof WorldGenRegion worldGenRegion) {
            Registry<Structure> structureRegistry = worldGenRegion.registryAccess().registry(Registries.STRUCTURE).get();
            StructureManager structureManager = placementContext.getLevel().getLevel().structureManager();
            ChunkPos chunkPos = new ChunkPos(blockPos);

            boolean doTagCheck = isDoTagCheck(blockPos);
            if (this.disallowTag.isPresent() && doTagCheck) {
                List<StructureStart> structureStarts = structureManager.startsForStructure(chunkPos,
                        struct -> structureRegistry.getHolderOrThrow(structureRegistry.getResourceKey(struct).get()).is(this.disallowTag.get()));

                if (!structureStarts.isEmpty()) {
                    boolean validSpot = true;

                    if (this.piecewiseCheck) {
                        for (StructureStart structureStart : structureStarts) {
                            for(StructurePiece structurePiece : structureStart.getPieces()) {
                                if (structurePiece.getBoundingBox().inflatedBy(8).isInside(blockPos)) {
                                    validSpot = false;
                                    break;
                                }
                            }
                        }
                    }
                    else {
                        for (StructureStart structureStart : structureStarts) {
                            if (structureStart.isValid() && structureStart.getBoundingBox().inflatedBy(8).isInside(blockPos)) {
                                validSpot = false;
                                break;
                            }
                        }
                    }

                    if (!validSpot) {
                        return Stream.empty();
                    }
                }
            }

        }

        return Stream.of(blockPos);
    }

    private boolean isDoTagCheck(BlockPos blockPos) {
        boolean doTagCheck = false;

        if (this.minY.isPresent() && this.maxY.isPresent()) {
            if (blockPos.getY() >= minY.get() && blockPos.getY() <= maxY.get()) {
                doTagCheck = true;
            }
        }
        else if (this.minY.isPresent()) {
            if (blockPos.getY() >= minY.get()) {
                doTagCheck = true;
            }
        }
        else if (this.maxY.isPresent()) {
            if (blockPos.getY() <= maxY.get()) {
                doTagCheck = true;
            }
        }
        else {
            doTagCheck = true;
        }

        return doTagCheck;
    }
}
