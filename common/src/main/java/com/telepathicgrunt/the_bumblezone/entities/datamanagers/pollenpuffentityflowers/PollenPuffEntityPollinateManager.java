package com.telepathicgrunt.the_bumblezone.entities.datamanagers.pollenpuffentityflowers;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.utils.BzNbtPredicate;
import com.telepathicgrunt.the_bumblezone.utils.LenientUnboundedMapCodec;
import com.telepathicgrunt.the_bumblezone.utils.PlatformHooks;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.telepathicgrunt.the_bumblezone.Bumblezone.GSON;

public class PollenPuffEntityPollinateManager extends SimpleJsonResourceReloadListener {
    public static final PollenPuffEntityPollinateManager POLLEN_PUFF_ENTITY_POLLINATE_MANAGER = new PollenPuffEntityPollinateManager();

    public record EntryObject(BzNbtPredicate nbtPredicate, WeightedStateProvider weightedStateProvider) {
        public static final Codec<EntryObject> ENTRY_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                BzNbtPredicate.CODEC.fieldOf("nbt_match").orElse(BzNbtPredicate.ANY).forGetter(config -> config.nbtPredicate),
                WeightedStateProvider.CODEC.fieldOf("plants_to_spawn").forGetter(config -> config.weightedStateProvider)
        ).apply(instance, instance.stable(EntryObject::new)));
    }

    public static final Codec<Map<EntityType<?>, List<EntryObject>>> CODEC =
            new LenientUnboundedMapCodec<>(ResourceLocation.CODEC.comapFlatMap(r -> {
                Optional<EntityType<?>> entityTypeOptional = BuiltInRegistries.ENTITY_TYPE.getOptional(r);
                if (entityTypeOptional.isPresent()) {
                    return DataResult.success(entityTypeOptional.get());
                } else if (PlatformHooks.isModLoaded(r.getNamespace())) {
                    Bumblezone.LOGGER.error("Bz Pollination File Reading Error - Unknown EntityType:  " + r);
                    return DataResult.error(() -> "Bz Error - Unknown EntityType:  " + r + "  - ");
                } else {
                    return DataResult.error(() -> "Bz Error - Target mod not present");
                }
            }, BuiltInRegistries.ENTITY_TYPE::getKey), Codec.list(EntryObject.ENTRY_CODEC));

    public final Map<EntityType<?>, List<EntryObject>> mobToPlants = new Object2ObjectArrayMap<>();

    public PollenPuffEntityPollinateManager() {
        super(GSON, "bz_pollen_puff_entity_flowers");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> loader, ResourceManager manager, ProfilerFiller profiler) {
        mobToPlants.clear();
        loader.forEach((fileIdentifier, jsonElement) -> {
            try {
                DataResult<Map<EntityType<?>, List<EntryObject>>> mapDataResult = CODEC.parse(JsonOps.INSTANCE, jsonElement);
                mapDataResult.error().ifPresent(e -> {
                    if (!e.message().contains("Bz Error - Target mod not present")) {
                        Bumblezone.LOGGER.error("Bumblezone Error: Couldn't parse pollen puff entity to flower file {} - Error: {}", fileIdentifier, e);
                    }
                 });
                Map<EntityType<?>, List<EntryObject>> newMap = mapDataResult.resultOrPartial((s) -> {}).orElse(new HashMap<>());

                newMap.forEach((e, v) -> {
                    // Combine existing entries
                    if (mobToPlants.containsKey(e)) {
                        mobToPlants.get(e).addAll(v);
                    }
                    // Add new entries
                    else {
                        mobToPlants.put(e, v);
                    }
                });
            }
            catch (Exception e) {
                Bumblezone.LOGGER.error("Bumblezone Error: Couldn't parse pollen puff entity to flower file: {}", fileIdentifier, e);
            }
        });
    }

    public WeightedStateProvider getPossiblePlants(Entity entity) {
        if (this.mobToPlants.containsKey(entity.getType())) {
            for (EntryObject entryObject : mobToPlants.get(entity.getType())) {
                if (entryObject.nbtPredicate().matches(entity)) {
                    return entryObject.weightedStateProvider();
                }
            }
        }
        return null;
    }
}
