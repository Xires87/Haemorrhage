package net.fryc.imbleeding.util;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fryc.imbleeding.ImBleeding;
import net.fryc.imbleeding.attributes.ModEntityAttributes;
import net.fryc.imbleeding.effects.ModEffects;
import net.fryc.imbleeding.network.payloads.CreateBloodParticlePayload;
import net.fryc.imbleeding.tags.ModDamageTypeTags;
import net.fryc.imbleeding.tags.ModEntityTypeTags;
import net.fryc.imbleeding.tags.ModItemTags;
import net.fryc.imbleeding.util.mixin_interfaces.CanBleed;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

public class BleedingHelper {

    public static boolean shouldApplyDarkness(LivingEntity entity) {
        return entity instanceof Player;
    }

    public static void applyDarkness(LivingEntity entity){
        if(entity.getHealth() < 6 && ImBleeding.config.enableDarknessAtLowHp){
            if(entity.hasEffect(MobEffects.DARKNESS)){
                if(entity.getActiveEffectsMap().get(MobEffects.DARKNESS).getDuration() < 34){
                    entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 33 , 0, false, false, false));
                }
            }
            else entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 33 , 0, false, false, false));
        }
    }

    public static void applyBlindness(LivingEntity entity){
        if(entity.getHealth()<=1 && ImBleeding.config.enableBlindnessAtLowHp){
            if(entity.hasEffect(MobEffects.BLINDNESS)){
                if(entity.getActiveEffectsMap().get(MobEffects.BLINDNESS).getDuration() < 25) entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60 , 0, false, false, false));
            }
            else entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60 , 0, false, false, false));
        }
    }

    public static void applyBleeding(LivingEntity entity, DamageSource source, float amount){
        applyBleedingOrHealthLoss(entity, source, amount, false);
    }

    public static void applyHealthLoss(LivingEntity entity, DamageSource source, float amount){
        applyBleedingOrHealthLoss(entity, source, amount, true);
    }

    public static void applyBleedingOrHealthLoss(LivingEntity entity, DamageSource source, float amount, boolean healthLoss){
        int duration;
        double reduction = entity.getAttributeValue(ModEntityAttributes.GENERIC_BLEEDING_PROTECTION)/100;
        boolean melee = !source.is(DamageTypeTags.IS_PROJECTILE);
        boolean meleeWithTool = false;

        if(melee && source.getEntity() != null && source.getEntity() instanceof LivingEntity livingEntity){
            if(livingEntity.getMainHandItem().is(ModItemTags.WEAPONS_PIERCE_BLEEDING_PROTECTION)){
                reduction -= ImBleeding.config.sharpWeaponBleedProtPiercing;
                meleeWithTool = true;
            }
            else if(livingEntity.getMainHandItem().isDamageableItem()){
                meleeWithTool = true;
            }
        }

        if(reduction < 1){
            if(healthLoss){
                duration = (int) (ImBleeding.config.healthLossLength * amount);
            }
            else {
                if(!melee){
                    duration = (int) (ImBleeding.config.arrowBleedLength * amount);
                }
                else {
                    duration = (int) (ImBleeding.config.meleeBleedLength * amount);
                    if(meleeWithTool){
                        duration += (int) (duration * ImBleeding.config.anyWeaponBleedLengthIncrease);
                    }
                }
            }

            duration -= (int) (duration * reduction);

            if(duration > 19){
                Holder<MobEffect> effect = healthLoss ? ModEffects.HEALTH_LOSS : ModEffects.BLEED_EFFECT;
                BleedingHelper.applyOrExtendEffect(entity, duration, effect, source, amount);
            }
        }
    }

    public static void applyOrExtendEffect(LivingEntity entity, int duration, Holder<MobEffect> effect) {
        applyOrExtendEffect(entity, duration, effect, null, 0);
    }

    public static void applyOrExtendEffect(LivingEntity entity, int duration, Holder<MobEffect> effect, @Nullable DamageSource source, float amount){
        if(!entity.hasEffect(effect)){
            entity.addEffect(new MobEffectInstance(effect, duration, 0, false, false, true));
        }
        else{
            int amp = entity.getActiveEffectsMap().get(effect).getAmplifier();
            if(source != null && amount > 0) {
                int bleedingUpgradeChance;
                if(amp == 0){
                    bleedingUpgradeChance = (int)(ImBleeding.config.baseChanceToUpgradeBleedingOrHealthLoss * (amount + 1));
                }
                else{
                    bleedingUpgradeChance = (int)((1+((float) ImBleeding.config.baseChanceToUpgradeBleedingOrHealthLoss /10)) * (amount + 1));
                }
                if(amp < 3 && checkIfBleedingCanBeUpgraded(source)){
                    if(ThreadLocalRandom.current().nextInt(100) >= 100 - bleedingUpgradeChance){
                        amp++;
                    }
                }
            }

            if(entity.getActiveEffectsMap().get(effect).getDuration() > duration){
                duration -= (int) (duration * 0.75);
            }
            else{
                duration -= (int) (entity.getActiveEffectsMap().get(effect).getDuration() * 0.75);
            }
            duration += entity.getActiveEffectsMap().get(effect).getDuration();

            entity.addEffect(new MobEffectInstance(effect, duration, amp, false, false, true));
        }
    }

    private static boolean checkIfBleedingCanBeUpgraded(DamageSource source){
        if(source.is(DamageTypeTags.IS_PROJECTILE)) return ImBleeding.config.enableArrowEffectUpgrading;
        return ImBleeding.config.enableMeleeEffectUpgrading;
    }

    public static void increaseScorchedFactor(LivingEntity entity, float damageTaken, float modifier){
        // TODO zmiany ze scorchem
        if(damageTaken > 0) {
            if(entity.hasEffect(ModEffects.SCORCHED)) {
                modifier -= 0.06F * (entity.getEffect(ModEffects.SCORCHED).getAmplifier() + 1);
            }

            if(entity.isInWaterRainOrBubble()) {
                modifier *= 0.33F;
            }
            else if(entity.isInLava()) {
                modifier *= 1.8F;
            }

            if(entity.level().dimensionType().ultraWarm()) {
                modifier *= 1.2F;
            }

            if(modifier > 0) {
                ((CanBleed) entity).modifyScorchedFactor(damageTaken * modifier, false);
            }
        }
        /*
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

         */
    }

    public static void modifyStatusEffect(Holder<MobEffect> effect, LivingEntity entity, int durAdd, int ampAdd, boolean shouldRemove) {
        if(entity.hasEffect(effect)){
            MobEffectInstance instance = entity.getActiveEffectsMap().get(effect);

            int dur = instance.getDuration() + durAdd;
            int amp;
            if(dur > 0 || instance.isInfiniteDuration()) {
                amp = instance.getAmplifier() + ampAdd;
            }
            else {
                amp = -1;
                shouldRemove = true;
            }

            if(amp < 0) {
                if(shouldRemove) {
                    entity.removeEffect(instance.getEffect());
                    return;
                }

                amp = 0;
            }

            boolean ambient = instance.isAmbient();
            boolean showParticles = instance.isVisible();
            boolean showIcon = instance.showIcon();

            entity.removeEffect(instance.getEffect());
            entity.addEffect(new MobEffectInstance(effect, instance.isInfiniteDuration() ? -1 : dur, amp, ambient, showParticles, showIcon));
        }
    }

    public static boolean shouldIncreaseScorchedFactor(DamageSource source, Level world){
        // to make sure config option is checked on server side
        if(!world.isClientSide()){
            return source.is(DamageTypeTags.IS_FIRE) && ImBleeding.config.fireDamageLowersBleedingDuration;
        }
        return false;
    }

    public static boolean shouldStopFoodHealing(Player player){
        return ImBleeding.config.bleedingStopsFoodHealing && player.hasEffect(ModEffects.BLEED_EFFECT);
    }

    public static void applyBrokenEffect(LivingEntity entity, float damage){
        int duration;
        if(entity.hasEffect(ModEffects.BROKEN)){
            duration = entity.getActiveEffectsMap().get(ModEffects.BROKEN).getDuration() + (int)(ImBleeding.config.brokenLengthPerHealthPointLost*damage);
        }
        else {
            duration = ImBleeding.config.baseBrokenLength + (int)(ImBleeding.config.brokenLengthPerHealthPointLost*(damage-ImBleeding.config.minFallDamageTakenToGetBroken));
        }
        entity.addEffect(new MobEffectInstance(ModEffects.BROKEN, duration, 0, false, false, true));
    }

    public static boolean shouldApplyBrokenEffect(DamageSource source, float damage, LivingEntity entity){
        return source.is(DamageTypeTags.IS_FALL) && (damage > ImBleeding.config.minFallDamageTakenToGetBroken || entity.hasEffect(ModEffects.BROKEN));
    }

    public static boolean canGetBleeding(LivingEntity entity) {
        // second condition to avoid unnecessary calculations
        return entity.getType().is(ModEntityTypeTags.CAN_BLEED) && !entity.getType().is(ModEntityTypeTags.BLEED_RESISTANT_TO);
    }

    public static boolean canGetHealthLoss(LivingEntity entity) {
        return !entity.getType().is(ModEntityTypeTags.HEALTH_LOSS_RESISTANT_TO);
    }

    public static boolean canApplyBleeding(DamageSource source, float amount){
        if(amount >= 1){
            if(source.is(ModDamageTypeTags.DAMAGE_APPLY_BLEED)){
                if(source.is(DamageTypeTags.IS_PROJECTILE)){
                    if(source.getDirectEntity() != null){
                        return source.getDirectEntity().getType().is(ModEntityTypeTags.BLEEDING_PROJECTILES);
                    }
                    return false;
                }
                else if(source.getEntity() != null){
                    return !source.getEntity().getType().is(ModEntityTypeTags.NO_BLEEDING_APPLY_MOBS);
                }
                return true;
            }
        }
        return false;
    }

    public static boolean canApplyHealthLoss(DamageSource source){
        if(source.is(ModDamageTypeTags.DAMAGE_APPLY_HEALTH_LOSS)){
            if(source.getEntity() != null){
                return source.getEntity().getType().is(ModEntityTypeTags.MOBS_CAUSING_HEALTH_LOSS);
            }
            return false;
        }
        return false;
    }


    public static float modifyHealAmount(LivingEntity entity, float amount) {
        if(amount > 0 && entity.hasEffect(ModEffects.SCORCHED)) {
            amount *= Mth.clamp(1F - (entity.getEffect(ModEffects.SCORCHED).getAmplifier() + 1) * 0.2F, 0F, 1F);
        }

        return amount;
    }

    public static void spawnBloodParticlesServerSided(LivingEntity entity) {
        if(ImBleeding.config.enableBloodParticlesServerSided){
            if(entity.hasEffect(ModEffects.BLEED_EFFECT)){
                if(ThreadLocalRandom.current().nextInt(100) < 5 + entity.getActiveEffectsMap().get(ModEffects.BLEED_EFFECT).getAmplifier()*5){
                    Vec3 vec3d = entity.getDeltaMovement();
                    double x = (ThreadLocalRandom.current().nextFloat()/2) - 0.25f;
                    double y = ThreadLocalRandom.current().nextFloat() + 0.1f;
                    double z = (ThreadLocalRandom.current().nextFloat()/2) - 0.25f;
                    for (ServerPlayer pl : PlayerLookup.tracking(((ServerLevel) entity.level()), entity.chunkPosition())) {
                        ServerPlayNetworking.send(pl, new CreateBloodParticlePayload(
                                entity.getX() + x,
                                entity.getY() + y,
                                entity.getZ() + z,
                                vec3d.y()-0.05
                        ));
                    }
                }
            }
        }
    }

    public static void manageScorchedFactor(LivingEntity entity) {
        int amp;
        int dur;
        boolean shouldApply = false;
        if(!entity.hasEffect(ModEffects.SCORCHED)) {
            ((CanBleed) entity).modifyScorchedFactor(-0.0004F, false);
            amp = -1;
            dur = 0;
        }
        else {
            MobEffectInstance effect = entity.getEffect(ModEffects.SCORCHED);
            amp = effect.getAmplifier();
            dur = effect.getDuration();
        }

        while(((CanBleed) entity).getScorchedFactor() > 1F) {
            ((CanBleed) entity).modifyScorchedFactor(-1F, false);
            amp += 1;
            dur += 460;
            shouldApply = true;
        }

        if(shouldApply) {
            entity.addEffect(new MobEffectInstance(ModEffects.SCORCHED, dur, amp, false, false, true));
        }
    }

    public static void extendScorchedDuration(LivingEntity entity, float damageTaken) {
        if(entity.hasEffect(ModEffects.SCORCHED)) {
            // TODO przy dostawaniu damagea wydluzac czas scorcha (juz wstawilem do playerEntity i LivingEntity)
        }
    }
}
