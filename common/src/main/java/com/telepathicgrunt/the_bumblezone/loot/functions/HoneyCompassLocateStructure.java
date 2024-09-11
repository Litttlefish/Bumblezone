package com.telepathicgrunt.the_bumblezone.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.telepathicgrunt.the_bumblezone.items.HoneyCompass;
import com.telepathicgrunt.the_bumblezone.items.datacomponents.HoneyCompassBaseData;
import com.telepathicgrunt.the_bumblezone.items.datacomponents.HoneyCompassStateData;
import com.telepathicgrunt.the_bumblezone.items.datacomponents.HoneyCompassTargetData;
import com.telepathicgrunt.the_bumblezone.modinit.BzDataComponents;
import com.telepathicgrunt.the_bumblezone.modinit.BzItems;
import com.telepathicgrunt.the_bumblezone.modinit.BzLootFunctionTypes;
import com.telepathicgrunt.the_bumblezone.utils.ThreadExecutor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class HoneyCompassLocateStructure extends LootItemConditionalFunction {
    public static final int DEFAULT_SEARCH_RADIUS = 50;
    public static final boolean DEFAULT_SKIP_EXISTING = true;
    final TagKey<Structure> destination;
    final int searchRadius;
    final boolean skipKnownStructures;

    public static final MapCodec<HoneyCompassLocateStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> HoneyCompassLocateStructure.commonFields(instance).and(
            instance.group(
                    TagKey.codec(Registries.STRUCTURE).fieldOf("destination").forGetter(honeyCompassLocateStructure -> honeyCompassLocateStructure.destination),
                    Codec.intRange(0, 1000000).orElse(DEFAULT_SEARCH_RADIUS).fieldOf("search_radius").forGetter(honeyCompassLocateStructure -> honeyCompassLocateStructure.searchRadius),
                    Codec.BOOL.orElse(DEFAULT_SKIP_EXISTING).fieldOf("skip_existing_chunks").forGetter(honeyCompassLocateStructure -> honeyCompassLocateStructure.skipKnownStructures)
            )
    ).apply(instance, HoneyCompassLocateStructure::new));

    public HoneyCompassLocateStructure(List<LootItemCondition> lootItemConditions, TagKey<Structure> destination, int searchRadius, boolean skipKnownStructrues) {
        super(lootItemConditions);
        this.destination = destination;
        this.searchRadius = searchRadius;
        this.skipKnownStructures = skipKnownStructrues;
    }

    @Override
    public LootItemFunctionType getType() {
        return BzLootFunctionTypes.HONEY_COMPASS_LOCATE_STRUCTURE.get();
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.ORIGIN);
    }

    @Override
    public ItemStack run(ItemStack itemStack, LootContext lootContext) {
        if (itemStack.is(BzItems.HONEY_COMPASS.get())) {
            Vec3 vec3 = lootContext.getParamOrNull(LootContextParams.ORIGIN);
            if (vec3 != null) {
                UUID searchId = UUID.randomUUID();
                BlockPos blockPos = BlockPos.containing(vec3);

                HoneyCompassStateData honeyCompassStateData = itemStack.get(BzDataComponents.HONEY_COMPASS_STATE_DATA.get());
                itemStack.set(BzDataComponents.HONEY_COMPASS_STATE_DATA.get(), new HoneyCompassStateData(
                        honeyCompassStateData.locked(),
                        Optional.of(searchId),
                        true,
                        honeyCompassStateData.isFailed(),
                        honeyCompassStateData.locatedSpecialStructure()
                ));

                HoneyCompassTargetData honeyCompassTargetData = itemStack.get(BzDataComponents.HONEY_COMPASS_TARGET_DATA.get());
                itemStack.set(BzDataComponents.HONEY_COMPASS_TARGET_DATA.get(), new HoneyCompassTargetData(
                        honeyCompassTargetData.targetBlock(),
                        Optional.of(destination.location().toString()),
                        honeyCompassTargetData.targetPos(),
                        honeyCompassTargetData.targetDimension()
                ));

                ResourceKey<Structure> structure = null;
                Registry<Structure> structureRegistry = lootContext.getLevel().registryAccess().registry(Registries.STRUCTURE).get();
                List<Structure> structuresList = structureRegistry
                        .getTag(destination)
                        .map(holders -> holders
                            .stream()
                            .map(Holder::value)
                            .toList()
                        ).orElseGet(ArrayList::new);

                if (!structuresList.isEmpty()) {
                    structure = structureRegistry.getResourceKey(structuresList.get(lootContext.getRandom().nextInt(structuresList.size()))).get();
                }

                WeakReference<ItemStack> weakRefItemStack = new WeakReference<>(itemStack);
                if (structure != null) {
                    ThreadExecutor.locate(lootContext.getLevel(), searchId, structure, blockPos, 100, false)
                            .thenOnServerThread(foundPos -> setCompassFoundLocationData(weakRefItemStack, lootContext, foundPos));
                }
            }
        }
        return itemStack;
    }

    private void setCompassFoundLocationData(WeakReference<ItemStack> itemStackWeakReference, LootContext lootContext, BlockPos blockPos) {
        ItemStack itemStack = itemStackWeakReference.get();
        if (itemStack != null && blockPos != null) {
            HoneyCompassStateData oldHoneyCompassStateData = itemStack.get(BzDataComponents.HONEY_COMPASS_STATE_DATA.get());
            HoneyCompassBaseData oldHoneyCompassBaseData = itemStack.get(BzDataComponents.HONEY_COMPASS_BASE_DATA.get());
            HoneyCompassTargetData oldHoneyCompassTargetData = itemStack.get(BzDataComponents.HONEY_COMPASS_TARGET_DATA.get());

            HoneyCompass.setCompassStateData(oldHoneyCompassStateData, oldHoneyCompassStateData.locked(), Optional.empty(), false, false, oldHoneyCompassStateData.locatedSpecialStructure(), itemStack);
            HoneyCompass.setCompassBaseData(oldHoneyCompassBaseData, "structure", itemStack);
            HoneyCompass.setCompassTargetData(oldHoneyCompassTargetData, Optional.empty(), Optional.empty(), Optional.of(blockPos), Optional.of(lootContext.getLevel().dimension()), itemStack);
        }
    }
}