package net.fryc.imbleeding.util;

import net.fryc.imbleeding.ImBleeding;
import net.fryc.imbleeding.effects.ModEffects;
import net.fryc.imbleeding.tags.ModDamageTypeTags;
import net.fryc.imbleeding.tags.ModEntityTypeTags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.world.World;

import java.util.concurrent.ThreadLocalRandom;

public class BleedingHelper {

    public static boolean shouldApplyDarkness(LivingEntity dys) {
        return dys instanceof PlayerEntity;
    }

    public static void applyDarkness(LivingEntity entity){
        if(entity.getHealth() < 6 && ImBleeding.config.enableDarknessAtLowHp){
            if(entity.hasStatusEffect(StatusEffects.DARKNESS)){
                if(entity.getActiveStatusEffects().get(StatusEffects.DARKNESS).getDuration() < 34){
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 33 , 0, false, false, false));
                }
            }
            else entity.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 33 , 0, false, false, false));
        }
    }

    public static void applyBlindness(LivingEntity entity){
        if(entity.getHealth()<=1 && ImBleeding.config.enableBlindnessAtLowHp){
            if(entity.hasStatusEffect(StatusEffects.BLINDNESS)){
                if(entity.getActiveStatusEffects().get(StatusEffects.BLINDNESS).getDuration() < 25) entity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60 , 0, false, false, false));
            }
            else entity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60 , 0, false, false, false));
        }
    }

    public static void applyBleeding(LivingEntity entity, DamageSource source, float amount){
        applyBleedingOrHealthLoss(entity, source, amount, false);
    }

    public static void applyHealthLoss(LivingEntity entity, DamageSource source, float amount){
        applyBleedingOrHealthLoss(entity, source, amount, true);
    }

    public static void applyBleedingOrHealthLoss(LivingEntity entity, DamageSource source, float amount, boolean healthLoss){
        float toughness = (int) (entity.getAttributes().getValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS));
        int armor = (int) (entity.getAttributes().getValue(EntityAttributes.GENERIC_ARMOR));
        int duration;
        // effect reduction in %
        float reduction = ((ImBleeding.config.armorBleedingProtection * armor) + (ImBleeding.config.toughnessBleedingProtection * toughness))/100;

        if(reduction < 1){
            if(healthLoss){
                duration = (int) (ImBleeding.config.healthLossLength * amount);
            }
            else {
                if(source.isIn(DamageTypeTags.IS_PROJECTILE)){
                    duration = (int) (ImBleeding.config.arrowBleedLength * amount);
                }
                else {
                    duration = (int) (ImBleeding.config.meleeBleedLength * amount);
                    if(source.getAttacker() != null && source.getAttacker() instanceof LivingEntity livingEntity){
                        if(livingEntity.getMainHandStack().isDamageable()){
                            duration += duration * 0.17;
                        }
                    }
                }
            }

            duration -= duration * reduction;

            if(duration > 19){
                StatusEffect effect = healthLoss ? ModEffects.HEALTH_LOSS : ModEffects.BLEED_EFFECT;
                BleedingHelper.applyOrExtendEffect(entity, duration, effect, source, amount);
            }
        }
    }

    public static void applyOrExtendEffect(LivingEntity entity, int duration, StatusEffect effect, DamageSource source, float amount){
        if(!entity.hasStatusEffect(effect)){
            entity.addStatusEffect(new StatusEffectInstance(effect, duration, 0, false, false, true));
        }
        else{
            int amp = entity.getActiveStatusEffects().get(effect).getAmplifier();
            int bleedingUpgradeChance;
            if(amp == 0){
                bleedingUpgradeChance = (int)(ImBleeding.config.baseChanceToUpgradeBleedingOrHealthLoss * (amount + 1));
            }
            else{
                bleedingUpgradeChance = (int)((1+(ImBleeding.config.baseChanceToUpgradeBleedingOrHealthLoss/10)) * (amount + 1));
            }
            if(amp < 3 && checkIfBleedingCanBeUpgraded(source)){
                if(ThreadLocalRandom.current().nextInt(100) >= 100 - bleedingUpgradeChance){
                    amp++;
                }
            }
            if(entity.getActiveStatusEffects().get(effect).getDuration() > duration){
                duration -= duration * 0.75;
            }
            else{
                duration -= entity.getActiveStatusEffects().get(effect).getDuration() * 0.75;
            }
            duration += entity.getActiveStatusEffects().get(effect).getDuration();

            entity.addStatusEffect(new StatusEffectInstance(effect, duration, amp, false, false, true));
        }
    }

    private static boolean checkIfBleedingCanBeUpgraded(DamageSource source){
        if(source.isIn(DamageTypeTags.IS_PROJECTILE)) return ImBleeding.config.enableArrowEffectUpgrading;
        return ImBleeding.config.enableMeleeEffectUpgrading;
    }

    public static void reduceBleedingWithFire(LivingEntity entity){
        if(entity.hasStatusEffect(ModEffects.BLEED_EFFECT)){
            int amp = entity.getActiveStatusEffects().get(ModEffects.BLEED_EFFECT).getAmplifier();
            int dur = entity.getActiveStatusEffects().get(ModEffects.BLEED_EFFECT).getDuration();
            if(amp == 0) dur -= 280;
            else if(amp == 1) dur -= 120;
            else dur -= 50;
            if(!entity.getWorld().isClient() && amp > 0){
                if(ThreadLocalRandom.current().nextInt(100) >= 100 - ImBleeding.config.chanceToLowerBleedingAmplifierWithFire){
                    amp--;
                }
            }
            entity.removeStatusEffect(entity.getActiveStatusEffects().get(ModEffects.BLEED_EFFECT).getEffectType());
            if(dur > 0){
                entity.addStatusEffect(new StatusEffectInstance(ModEffects.BLEED_EFFECT, dur, amp, false, false, true));
            }
        }
    }

    public static boolean shouldReduceBleedingWithFire(DamageSource source, World world){
        // to make sure config option is checked on server side
        if(!world.isClient()){
            return source.isIn(DamageTypeTags.IS_FIRE) && ImBleeding.config.fireDamageLowersBleedingDuration;
        }
        return false;
    }

    public static boolean shouldStopFoodHealing(PlayerEntity player){
        return player.hasStatusEffect(ModEffects.BLEED_EFFECT);
    }

    public static void applyBrokenEffect(LivingEntity entity, float damage){
        int duration;
        if(entity.hasStatusEffect(ModEffects.BROKEN)){
            duration = entity.getActiveStatusEffects().get(ModEffects.BROKEN).getDuration() + (int)(ImBleeding.config.brokenLengthPerHealthPointLost*damage);
        }
        else {
            duration = ImBleeding.config.baseBrokenLength + (int)(ImBleeding.config.brokenLengthPerHealthPointLost*(damage-ImBleeding.config.minFallDamageTakenToGetBroken));
        }
        entity.addStatusEffect(new StatusEffectInstance(ModEffects.BROKEN, duration, 0, false, false, true));
    }

    public static boolean shouldApplyBrokenEffect(DamageSource source, float damage, LivingEntity entity){
        return source.isIn(DamageTypeTags.IS_FALL) && (damage > ImBleeding.config.minFallDamageTakenToGetBroken || entity.hasStatusEffect(ModEffects.BROKEN));
    }

    public static boolean canGetBleeding(LivingEntity entity) {
        // second condition to avoid unnecessary calculations
        return entity.getType().isIn(ModEntityTypeTags.CAN_BLEED) && !entity.getType().isIn(ModEntityTypeTags.BLEED_RESISTANT_TO);
    }

    public static boolean canGetHealthLoss(LivingEntity entity) {
        return !entity.getType().isIn(ModEntityTypeTags.HEALTH_LOSS_RESISTANT_TO);
    }

    public static boolean canApplyBleeding(DamageSource source, float amount){
        if(amount >= 1){
            if(source.isIn(ModDamageTypeTags.DAMAGE_APPLY_BLEED)){
                if(source.isIn(DamageTypeTags.IS_PROJECTILE)){
                    if(source.getSource() != null){
                        return source.getSource().getType().isIn(ModEntityTypeTags.BLEEDING_PROJECTILES);
                    }
                    return false;
                }
                else if(source.getAttacker() != null){
                    return !source.getAttacker().getType().isIn(ModEntityTypeTags.NO_BLEEDING_APPLY_MOBS);
                }
                return true;
            }
        }
        return false;
    }

    public static boolean canApplyHealthLoss(DamageSource source){
        if(source.isIn(ModDamageTypeTags.DAMAGE_APPLY_HEALTH_LOSS)){
            if(source.getAttacker() != null){
                return source.getAttacker().getType().isIn(ModEntityTypeTags.MOBS_CAUSING_HEALTH_LOSS);
            }
            return false;
        }
        return false;
    }


}
