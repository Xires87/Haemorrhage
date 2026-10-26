package net.fryc.imbleeding.effects;

import net.fryc.imbleeding.ImBleeding;
import net.fryc.imbleeding.damage.ModDamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class BleedoutEffect extends MobEffect {

    protected BleedoutEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (!pLivingEntity.level().isClientSide() && ImBleeding.config.bleedoutKills) {
            if(pLivingEntity.hasEffect(ModEffects.BLEED_EFFECT) && pLivingEntity.getActiveEffectsMap().get(ModEffects.BLEEDOUT).getDuration() > ImBleeding.config.bleedoutLength + ImBleeding.config.bleedoutLength/2) pLivingEntity.hurt(ModDamageTypes.of(pLivingEntity.level(), ModDamageTypes.BLEEDING_DAMAGE), 200F);
        }

        return super.applyEffectTick(pLivingEntity, pAmplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
        int i;
        if (this == ModEffects.BLEEDOUT.value()) {
            i = 60 >> pAmplifier;
            if (i > 0) {
                return pDuration % i == 0;
            } else {
                return true;
            }
        }
        else return true;
    }

    public void onEffectStarted(LivingEntity entity, int amplifier) {
        super.onEffectStarted(entity, amplifier);
        if (entity.getHealth() > entity.getMaxHealth()) {
            entity.setHealth(entity.getMaxHealth());
        }
    }

}
