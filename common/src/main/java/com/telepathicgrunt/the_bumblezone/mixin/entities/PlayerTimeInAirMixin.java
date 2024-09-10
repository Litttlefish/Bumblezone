package com.telepathicgrunt.the_bumblezone.mixin.entities;

import com.telepathicgrunt.the_bumblezone.blocks.HeavyAir;
import com.telepathicgrunt.the_bumblezone.entities.TemporaryPlayerData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerTimeInAirMixin extends LivingEntity implements TemporaryPlayerData {

    @Shadow public abstract boolean isCreative();

    @Shadow public abstract boolean isSpectator();

    @Unique
    private int bumblezone$ticksOffGroundInHeavyAir = 0;

    @Unique
    private boolean bumblezone$InHeavyAir = false;

    protected PlayerTimeInAirMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "tick()V", at = @At(value = "HEAD"))
    private void bumblezone$recordTicksOffGround(CallbackInfo ci) {
        if (this.onGround() || (this.getControlledVehicle() != null && this.getControlledVehicle().onGround())) {
            this.bumblezone$ticksOffGroundInHeavyAir = 0;
            bumblezone$InHeavyAir = HeavyAir.isInHeavyAir(this.level(), this.getBoundingBox());
        }
        else if (!this.isCreative() && !this.isSpectator()) {
            if (HeavyAir.isInHeavyAir(this.level(), this.getBoundingBox())) {
                this.bumblezone$ticksOffGroundInHeavyAir++;
                bumblezone$InHeavyAir = true;
            }
            else {
                bumblezone$InHeavyAir = false;
            }
        }
        else {
            bumblezone$InHeavyAir = false;
        }
    }

    @Override
    public int bumblezonePlayerTickOffGroundInHeavyAir() { return bumblezone$ticksOffGroundInHeavyAir; }

    @Override
    public boolean bumblezonePlayerInHeavyAir() { return bumblezone$InHeavyAir; }
}