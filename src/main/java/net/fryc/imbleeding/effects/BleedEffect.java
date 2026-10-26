package net.fryc.imbleeding.effects;

import net.fryc.imbleeding.ImBleeding;
import net.fryc.imbleeding.damage.ModDamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class BleedEffect extends MobEffect {
    public BleedEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (!pLivingEntity.level().isClientSide()) {
            int multiplier = pAmplifier + 1;
            float damage = multiplier * ImBleeding.config.bleedingDamage;
            if(pLivingEntity.getHealth() > damage){
                pLivingEntity.hurt(ModDamageTypes.of(pLivingEntity.level(), ModDamageTypes.BLEEDING_DAMAGE), damage);
            }
            else if(pLivingEntity.getHealth() > 0.5F){
                pLivingEntity.hurt(ModDamageTypes.of(pLivingEntity.level(), ModDamageTypes.BLEEDING_DAMAGE), pLivingEntity.getHealth()-0.4F);
            }
            else if(!pLivingEntity.getActiveEffectsMap().containsKey(ModEffects.BLEEDOUT)){
                pLivingEntity.addEffect(new MobEffectInstance(ModEffects.BLEEDOUT, (ImBleeding.config.bleedoutLength/2) * multiplier, 0, false, false, true));
            }
            else{
                pLivingEntity.addEffect(new MobEffectInstance(ModEffects.BLEEDOUT, pLivingEntity.getActiveEffectsMap().get(ModEffects.BLEEDOUT).getDuration() + ImBleeding.config.bleedoutLength * multiplier, 0, false, false, true));
            }

        }

        return super.applyEffectTick(pLivingEntity, pAmplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
        int i;
        if (this == ModEffects.BLEED_EFFECT.value()) {
            i = ImBleeding.config.bleedingDamageFrequency;
            return pDuration % i == 0;
        }
        else return true;
    }
}
