package net.fryc.imbleeding.effects;

import net.fryc.imbleeding.ImBleeding;
import net.fryc.imbleeding.damage.BleedDamageSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class BleedoutEffect extends StatusEffect {

    protected BleedoutEffect(StatusEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }

    @Override
    public void applyUpdateEffect(LivingEntity pLivingEntity, int pAmplifier) {
        if (!pLivingEntity.world.isClient() && ImBleeding.config.bleedoutKills) {
            if(pLivingEntity.hasStatusEffect(ModEffects.BLEED_EFFECT) && pLivingEntity.getActiveStatusEffects().get(ModEffects.BLEEDOUT).getDuration() > ImBleeding.config.bleedoutLength + ImBleeding.config.bleedoutLength/2) pLivingEntity.damage(new BleedDamageSource(pLivingEntity.getDamageSources().starve().getTypeRegistryEntry()), 200F);
        }

        super.applyUpdateEffect(pLivingEntity, pAmplifier);
    }

    @Override
    public boolean canApplyUpdateEffect(int pDuration, int pAmplifier) {
        int i;
        if (this == ModEffects.BLEEDOUT) {
            i = 25 >> pAmplifier;
            if (i > 0) {
                return pDuration % i == 0;
            } else {
                return true;
            }
        }
        else return true;
    }
}
