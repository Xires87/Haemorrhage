package net.fryc.imbleeding.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fryc.imbleeding.ImBleeding;
import net.fryc.imbleeding.attributes.ModEntityAttributes;
import net.fryc.imbleeding.effects.ModEffects;
import net.fryc.imbleeding.network.payloads.CreateBloodParticlePayload;
import net.fryc.imbleeding.tags.ModEntityTypeTags;
import net.fryc.imbleeding.util.BleedingHelper;
import net.fryc.imbleeding.util.mixin_interfaces.CanBleed;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Attackable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity implements Attackable, CanBleed {

    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Unique
    private float scorchedFactor = 0F;

    @Unique
    private float woundInfectionFactor = 0F;


    @Inject(method = "canBeAffected(Lnet/minecraft/world/effect/MobEffectInstance;)Z", at = @At("HEAD"), cancellable = true)
    private void undeadCantBleed(MobEffectInstance effect, CallbackInfoReturnable<Boolean> ret) {
        LivingEntity dys = ((LivingEntity)(Object)this);
        if(effect.getEffect() == ModEffects.BLEED_EFFECT || effect.getEffect() == ModEffects.BLEEDOUT || effect.getEffect() == ModEffects.WOUND_INFECTION){
            if(dys.getType().is(ModEntityTypeTags.BLEED_RESISTANT_TO)){
                ret.setReturnValue(false);
            }
        }
        else if(effect.getEffect() == ModEffects.HEALTH_LOSS){
            if(dys.getType().is(ModEntityTypeTags.HEALTH_LOSS_RESISTANT_TO)){
                ret.setReturnValue(false);
            }
        }
        else if(effect.getEffect() == ModEffects.SCORCHED){
            if(dys.getType().fireImmune()){
                ret.setReturnValue(false);
            }
        }
    }

    @Inject(method = "setSprinting(Z)V", at = @At("HEAD"), cancellable = true)
    private void dontSprintWhenBroken(boolean sprinting, CallbackInfo info) {
        LivingEntity dys = ((LivingEntity)(Object)this);
        if(sprinting && dys.hasEffect(ModEffects.BROKEN)){
            info.cancel();
        }
    }


    @Inject(method = "actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V", at = @At("TAIL"))
    public void applyDamageEffects(DamageSource source, float amount, CallbackInfo ci) {
        LivingEntity dys = ((LivingEntity) (Object) this);

        if(BleedingHelper.shouldApplyDarkness(dys)){
            BleedingHelper.applyDarkness(dys);
        }

        if(BleedingHelper.canGetBleeding(dys)){
            if(BleedingHelper.canApplyBleeding(source, amount)){
                BleedingHelper.applyBleeding(dys, source, amount);
            }
        }

        if(BleedingHelper.canGetHealthLoss(dys)){
            if(BleedingHelper.canApplyHealthLoss(source)){
                BleedingHelper.applyHealthLoss(dys, source, amount);
            }
        }

        if(BleedingHelper.shouldIncreaseScorchedFactor(source, dys.level())){
            BleedingHelper.increaseScorchedFactor(dys, amount, 0.38F);
            BleedingHelper.extendScorchedDuration(dys, amount);
        }

        if(BleedingHelper.shouldApplyBrokenEffect(source, amount, dys)){
            BleedingHelper.applyBrokenEffect(dys, amount);
        }

    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void spawnBloodParticlesAndManageScorchedFactor(CallbackInfo info) {
        LivingEntity dys = ((LivingEntity) (Object) this);
        if(!dys.level().isClientSide()){
            BleedingHelper.spawnBloodParticlesServerSided(dys);
            BleedingHelper.manageScorchedFactor(dys);
        }
    }


    @ModifyReturnValue(
            method = "createLivingAttributes()Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;",
            at = @At("RETURN")
    )
    private static AttributeSupplier.Builder addBleedingProtectionToAttributes(AttributeSupplier.Builder original) {
        return original.add(ModEntityAttributes.GENERIC_BLEEDING_PROTECTION, 4.0);
    }

    @Inject(method = "onEffectRemoved(Lnet/minecraft/world/effect/MobEffectInstance;)V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/effect/MobEffect;removeAttributeModifiers(Lnet/minecraft/world/entity/ai/attributes/AttributeMap;)V"
    ))
    private void addSlownessAfterRemovingBroken(MobEffectInstance effect, CallbackInfo info) {
        if(effect.getEffect().equals(ModEffects.BROKEN)){
            ((LivingEntity) (Object) this).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600, 0, false, false, true));
        }
    }


    @ModifyVariable(method = "heal(F)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float modifyHealAmountWhenScorched(float amount) {
        LivingEntity dys = ((LivingEntity) (Object) this);
        return BleedingHelper.modifyHealAmount(dys, amount);
    }

    @Inject(method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
    private void readFactorsFromNbt(CompoundTag nbt, CallbackInfo info) {
        if(nbt.contains("ScorchedFactor")) {
            this.scorchedFactor = nbt.getFloat("ScorchedFactor");
        }
        if(nbt.contains("WoundInfectionFactor")) {
            this.woundInfectionFactor = nbt.getFloat("WoundInfectionFactor");
        }
    }

    @Inject(method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
    private void writeFactorsToNbt(CompoundTag nbt, CallbackInfo info) {
        if(this.scorchedFactor > 0) {
            nbt.putFloat("ScorchedFactor", this.scorchedFactor);
        }
        if(this.woundInfectionFactor > 0) {
            nbt.putFloat("WoundInfectionFactor", this.woundInfectionFactor);
        }
    }

    public void modifyScorchedFactor(float amount, boolean multiply) {
        this.scorchedFactor = Mth.clamp(
                multiply ? this.scorchedFactor * amount : this.scorchedFactor + amount,
                0F,
                4.99F
        );
    }

    public void modifyWoundInfectionFactor(float amount, boolean multiply) {
        this.woundInfectionFactor = Mth.clamp(
                multiply ? this.woundInfectionFactor * amount : this.woundInfectionFactor + amount,
                0F,
                1F
        );
    }

    public float getScorchedFactor() {
        return this.scorchedFactor;
    }

    public float getWoundInfectionFactor() {
        return this.woundInfectionFactor;
    }

}
