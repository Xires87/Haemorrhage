package net.fryc.imbleeding.mixin;

import net.fryc.imbleeding.effects.ModEffects;
import net.fryc.imbleeding.tags.ModEntityTypeTags;
import net.fryc.imbleeding.tags.ModItemTags;
import net.fryc.imbleeding.util.BleedingHelper;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity implements Attackable {


    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }



    //undead enemies cant get bleeding
    @Inject(method = "canHaveStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;)Z", at = @At("HEAD"), cancellable = true)
    private void undeadCantBleed(StatusEffectInstance effect, CallbackInfoReturnable<Boolean> ret) {
        LivingEntity dys = ((LivingEntity)(Object)this);
        if(dys.getType().isIn(ModEntityTypeTags.BLEED_RESISTANT_TO)){
            if(effect.getEffectType() == ModEffects.BLEED_EFFECT || effect.getEffectType() == ModEffects.BLEEDOUT){
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

}
