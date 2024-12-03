package com.telepathicgrunt.the_bumblezone.mixin.world;

import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.configs.BzGeneralConfigs;
import com.telepathicgrunt.the_bumblezone.entities.BeeDedicatedSpawning;
import com.telepathicgrunt.the_bumblezone.utils.GeneralUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerWorldMixin extends Level {
	protected ServerWorldMixin(WritableLevelData writableLevelData, ResourceKey<Level> levelResourceKey, RegistryAccess registryAccess, Holder<DimensionType> dimensionTypeHolder, Supplier<ProfilerFiller> profilerFillerSupplier, boolean b1, boolean b, long l, int i) {
		super(writableLevelData, levelResourceKey, registryAccess, dimensionTypeHolder, profilerFillerSupplier, b1, b, l, i);
	}

	@Unique
	private static final int bumblezone$updateInterval = 21;

	@Unique
	private static int bumblezone$counter = 0;

	@Inject(
			method = "tick(Ljava/util/function/BooleanSupplier;)V",
			at = @At(value = "HEAD")
	)
	private void bumblezone$countBzDimEntities(CallbackInfo ci) {
		if(dimension().location().equals(Bumblezone.MOD_DIMENSION_ID)){
			bumblezone$counter++;
			if(bumblezone$counter % bumblezone$updateInterval == 0){
				bumblezone$counter = 0;
				ServerLevel serverLevel = (ServerLevel) (Object) this;
				BeeDedicatedSpawning.updateEntityCount(serverLevel);
				if(BzGeneralConfigs.specialBeeSpawning && serverLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
					BeeDedicatedSpawning.specialSpawnBees(serverLevel);
				}
			}
		}
	}
}
