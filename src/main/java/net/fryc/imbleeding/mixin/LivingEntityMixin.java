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
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
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

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    private float scorchedFactor = 0F;

    @Unique
    private float woundInfectionFactor = 0F;


    @Inject(method = "canHaveStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;)Z", at = @At("HEAD"), cancellable = true)
    private void undeadCantBleed(StatusEffectInstance effect, CallbackInfoReturnable<Boolean> ret) {
        LivingEntity dys = ((LivingEntity)(Object)this);
        if(effect.getEffectType() == ModEffects.BLEED_EFFECT || effect.getEffectType() == ModEffects.BLEEDOUT || effect.getEffectType() == ModEffects.WOUND_INFECTION){
            if(dys.getType().isIn(ModEntityTypeTags.BLEED_RESISTANT_TO)){
                ret.setReturnValue(false);
            }
        }
        else if(effect.getEffectType() == ModEffects.HEALTH_LOSS){
            if(dys.getType().isIn(ModEntityTypeTags.HEALTH_LOSS_RESISTANT_TO)){
                ret.setReturnValue(false);
            }
        }
        else if(effect.getEffectType() == ModEffects.SCORCHED){
            if(dys.getType().isFireImmune()){
                ret.setReturnValue(false);
            }
        }
    }

    @Inject(method = "setSprinting(Z)V", at = @At("HEAD"), cancellable = true)
    private void dontSprintWhenBroken(boolean sprinting, CallbackInfo info) {
        LivingEntity dys = ((LivingEntity)(Object)this);
        if(sprinting && dys.hasStatusEffect(ModEffects.BROKEN)){
            info.cancel();
        }
    }


    @Inject(method = "applyDamage(Lnet/minecraft/entity/damage/DamageSource;F)V", at = @At("TAIL"))
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

        if(BleedingHelper.shouldIncreaseScorchedFactor(source, dys.getWorld())){
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
        if(!dys.getWorld().isClient()){
            BleedingHelper.spawnBloodParticlesServerSided(dys);
            BleedingHelper.manageScorchedFactor(dys);
        }
    }


    @ModifyReturnValue(
            method = "createLivingAttributes()Lnet/minecraft/entity/attribute/DefaultAttributeContainer$Builder;",
            at = @At("RETURN")
    )
    private static DefaultAttributeContainer.Builder addBleedingProtectionToAttributes(DefaultAttributeContainer.Builder original) {
        return original.add(ModEntityAttributes.GENERIC_BLEEDING_PROTECTION, 4.0);
    }

    @Inject(method = "onStatusEffectRemoved(Lnet/minecraft/entity/effect/StatusEffectInstance;)V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/effect/StatusEffect;onRemoved(Lnet/minecraft/entity/attribute/AttributeContainer;)V"
    ))
    private void addSlownessAfterRemovingBroken(StatusEffectInstance effect, CallbackInfo info) {
        if(effect.getEffectType().equals(ModEffects.BROKEN)){
            ((LivingEntity) (Object) this).addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 600, 0, false, false, true));
        }
    }


    @ModifyVariable(method = "heal(F)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float modifyHealAmountWhenScorched(float amount) {
        LivingEntity dys = ((LivingEntity) (Object) this);
        return BleedingHelper.modifyHealAmount(dys, amount);
    }

    @Inject(method = "readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
    private void readFactorsFromNbt(NbtCompound nbt, CallbackInfo info) {
        if(nbt.contains("ScorchedFactor")) {
            this.scorchedFactor = nbt.getFloat("ScorchedFactor");
        }
        if(nbt.contains("WoundInfectionFactor")) {
            this.woundInfectionFactor = nbt.getFloat("WoundInfectionFactor");
        }
    }

    @Inject(method = "writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
    private void writeFactorsToNbt(NbtCompound nbt, CallbackInfo info) {
        if(this.scorchedFactor > 0) {
            nbt.putFloat("ScorchedFactor", this.scorchedFactor);
        }
        if(this.woundInfectionFactor > 0) {
            nbt.putFloat("WoundInfectionFactor", this.woundInfectionFactor);
        }
    }

    public void modifyScorchedFactor(float amount, boolean multiply) {
        this.scorchedFactor = MathHelper.clamp(
                multiply ? this.scorchedFactor * amount : this.scorchedFactor + amount,
                0F,
                4.99F
        );
    }

    public void modifyWoundInfectionFactor(float amount, boolean multiply) {
        this.woundInfectionFactor = MathHelper.clamp(
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
