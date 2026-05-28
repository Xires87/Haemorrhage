package net.fryc.imbleeding.effects;

import net.fryc.imbleeding.ImBleeding;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

import java.util.concurrent.ThreadLocalRandom;

public class WoundInfectionEffect extends StatusEffect {

    protected WoundInfectionEffect(StatusEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity pLivingEntity, int pAmplifier) {
        if (!pLivingEntity.getWorld().isClient()) {
            if(ThreadLocalRandom.current().nextFloat() < ImBleeding.config.poisonFromInfectionChance) {
                pLivingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, ThreadLocalRandom.current().nextInt(80, 260)));
            }
            else if(ThreadLocalRandom.current().nextFloat() < ImBleeding.config.hungerFromInfectionChance) {
                pLivingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, ThreadLocalRandom.current().nextInt(140, 640)));
            }
        }

        return super.applyUpdateEffect(pLivingEntity, pAmplifier);
    }

    @Override
    public boolean canApplyUpdateEffect(int pDuration, int pAmplifier) {
        int i;
        if (this == ModEffects.WOUND_INFECTION.value()) {
            i = 200 >> pAmplifier;
            if (i > 0) {
                return pDuration % i == 0;
            } else {
                return true;
            }
        }
        else return true;
    }
}
