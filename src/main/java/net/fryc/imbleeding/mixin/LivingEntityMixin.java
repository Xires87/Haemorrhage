package net.fryc.imbleeding.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fryc.imbleeding.ImBleeding;
import net.fryc.imbleeding.attributes.ModEntityAttributes;
import net.fryc.imbleeding.effects.ModEffects;
import net.fryc.imbleeding.network.payloads.CreateBloodParticlePayload;
import net.fryc.imbleeding.tags.ModEntityTypeTags;
import net.fryc.imbleeding.tags.ModItemTags;
import net.fryc.imbleeding.util.BleedingHelper;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity implements Attackable {

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }


    @Inject(method = "canHaveStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;)Z", at = @At("HEAD"), cancellable = true)
    private void undeadCantBleed(StatusEffectInstance effect, CallbackInfoReturnable<Boolean> ret) {
        LivingEntity dys = ((LivingEntity)(Object)this);
        if(effect.getEffectType() == ModEffects.BLEED_EFFECT || effect.getEffectType() == ModEffects.BLEEDOUT){
            if(dys.getType().isIn(ModEntityTypeTags.BLEED_RESISTANT_TO)){
                ret.setReturnValue(false);
            }
        }
        else if(effect.getEffectType() == ModEffects.HEALTH_LOSS){
            if(dys.getType().isIn(ModEntityTypeTags.HEALTH_LOSS_RESISTANT_TO)){
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

    /* It is done via unremovable effects now
    @Inject(method = "consumeItem()V", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/entity/LivingEntity;spawnConsumptionEffects(Lnet/minecraft/item/ItemStack;I)V", shift = At.Shift.AFTER))
    private void removeEffectsAfterUsingItem(CallbackInfo info) {
        LivingEntity dys = ((LivingEntity)(Object)this);
        if(!dys.getWorld().isClient()){
            if(dys.getActiveItem().isIn(ModItemTags.ITEMS_REMOVE_BLEEDING)){
                dys.removeStatusEffect(ModEffects.BLEED_EFFECT);
            }
            if(dys.getActiveItem().isIn(ModItemTags.ITEMS_REMOVE_HEALTH_LOSS)){
                dys.removeStatusEffect(ModEffects.HEALTH_LOSS);
            }
            if(dys.getActiveItem().isIn(ModItemTags.ITEMS_REMOVE_BROKEN)){
                if(dys.hasStatusEffect(ModEffects.BROKEN)){
                    int duration = dys.getActiveStatusEffects().get(ModEffects.BROKEN).getDuration();
                    dys.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, duration, 0, false, false, true));
                    dys.removeStatusEffect(ModEffects.BROKEN);
                }
            }
            if(dys.getActiveItem().isIn(ModItemTags.ITEMS_REMOVE_BLEEDOUT)){
                dys.removeStatusEffect(ModEffects.BLEEDOUT);
            }
        }
    }
     */


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

        if(BleedingHelper.shouldReduceBleedingWithFire(source, dys.getWorld())){
            BleedingHelper.reduceBleedingWithFire(dys);
        }

        if(BleedingHelper.shouldApplyBrokenEffect(source, amount, dys)){
            BleedingHelper.applyBrokenEffect(dys, amount);
        }

    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void spawnBloodParticles(CallbackInfo info) {
        LivingEntity dys = ((LivingEntity) (Object) this);
        if(!dys.getWorld().isClient()){
            if(ImBleeding.config.enableBloodParticlesServerSided){
                if(dys.hasStatusEffect(ModEffects.BLEED_EFFECT)){
                    if(ThreadLocalRandom.current().nextInt(100) < 5 + dys.getActiveStatusEffects().get(ModEffects.BLEED_EFFECT).getAmplifier()*5){
                        Vec3d vec3d = this.getVelocity();
                        for (ServerPlayerEntity pl : PlayerLookup.tracking(((ServerWorld) dys.getWorld()), dys.getChunkPos())) {
                            ServerPlayNetworking.send(pl, new CreateBloodParticlePayload(
                                    dys.getX(),
                                    dys.getY(),
                                    dys.getZ(),
                                    vec3d.getY()-0.05
                            ));
                        }
                    }
                }
            }
        }
    }


    @ModifyReturnValue(
            method = "createLivingAttributes()Lnet/minecraft/entity/attribute/DefaultAttributeContainer$Builder;",
            at = @At("RETURN")
    )
    private static DefaultAttributeContainer.Builder addBleedingProtectionToAttributes(DefaultAttributeContainer.Builder original) {
        return original.add(ModEntityAttributes.GENERIC_BLEEDING_PROTECTION, 4.0);
    }

}
