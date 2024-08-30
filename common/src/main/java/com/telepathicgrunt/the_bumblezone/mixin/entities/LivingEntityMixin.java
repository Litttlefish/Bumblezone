package com.telepathicgrunt.the_bumblezone.mixin.entities;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.telepathicgrunt.the_bumblezone.client.LocalPlayerParalyzedHandFix;
import com.telepathicgrunt.the_bumblezone.effects.ParalyzedEffect;
import com.telepathicgrunt.the_bumblezone.effects.WrathOfTheHiveEffect;
import com.telepathicgrunt.the_bumblezone.events.entity.BzFinishUseItemEvent;
import com.telepathicgrunt.the_bumblezone.modinit.BzEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntity.class, priority = 1200)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Shadow
    public abstract int getUseItemRemainingTicks();

    @Shadow
    public abstract boolean hasEffect(Holder<MobEffect> mobEffect);

    @Shadow
    public abstract double getAttributeValue(Holder<Attribute> attribute);

    @Shadow
    public abstract AttributeMap getAttributes();

    @Shadow
    protected abstract boolean isImmobile();

    @Shadow
    public float yHeadRot;

    @Shadow @Final private AttributeMap attributes;

    @ModifyReturnValue(method = "isImmobile()Z",
            at = @At(value = "RETURN"))
    private boolean bumblezone$isParalyzedCheck(boolean isImmobile) {
        return isImmobile || ParalyzedEffect.isParalyzed((LivingEntity)(Object)this);
    }

    @ModifyReturnValue(method = "getFlyingSpeed()F",
            at = @At(value = "RETURN"))
    private float bumblezone$flyingSpeedBeenergized(float flyingSpeed) {
        if(hasEffect(BzEffects.BEENERGIZED.holder()) && this.attributes.hasAttribute(Attributes.FLYING_SPEED)) {
            return ((float) (getAttributeValue(Attributes.FLYING_SPEED) / Attributes.FLYING_SPEED.value().getDefaultValue()) * flyingSpeed);
        }
        return flyingSpeed;
    }

    @Inject(method = "aiStep",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isImmobile()Z"),
            require = 0)
    private void bumblezone$fixHeadHandParalyzedRot(CallbackInfo ci) {
        if (this.isImmobile() && ParalyzedEffect.isParalyzed((LivingEntity)(Object)this)) {
            this.yHeadRot = this.getYRot();
            if (this.level().isClientSide) {
                LocalPlayerParalyzedHandFix.handleArms((LivingEntity) (Object) this);
            }
        }
    }

    @Inject(method = "onEffectRemoved(Lnet/minecraft/world/effect/MobEffectInstance;)V",
            at = @At(value = "TAIL"))
    private void bumblezone$runAtEffectRemoval(MobEffectInstance mobEffectInstance, CallbackInfo ci) {
        WrathOfTheHiveEffect.effectRemoval((LivingEntity) (Object) this, mobEffectInstance);
        ParalyzedEffect.effectRemoval((LivingEntity) (Object) this, mobEffectInstance);
    }

    @WrapOperation(
            method = "completeUsingItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;finishUsingItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack bumblezone$onCompleteUsingItem(ItemStack instance, Level level, LivingEntity livingEntity, Operation<ItemStack> operation) {
        ItemStack copy = instance.copy();
        BzFinishUseItemEvent.EVENT.invoke(new BzFinishUseItemEvent((LivingEntity) ((Object) this), copy, getUseItemRemainingTicks()));
        return operation.call(instance, level, livingEntity);
    }
}