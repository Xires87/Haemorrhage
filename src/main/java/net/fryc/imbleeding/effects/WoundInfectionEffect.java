package net.fryc.imbleeding.effects;

import net.fryc.imbleeding.ImBleeding;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import java.util.concurrent.ThreadLocalRandom;

public class WoundInfectionEffect extends MobEffect {

    protected WoundInfectionEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (!pLivingEntity.level().isClientSide()) {
            if(ThreadLocalRandom.current().nextFloat() < ImBleeding.config.poisonFromInfectionChance) {
                pLivingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, ThreadLocalRandom.current().nextInt(80, 260)));
            }
            else if(ThreadLocalRandom.current().nextFloat() < ImBleeding.config.hungerFromInfectionChance) {
                pLivingEntity.addEffect(new MobEffectInstance(MobEffects.HUNGER, ThreadLocalRandom.current().nextInt(140, 640)));
            }
        }

        return super.applyEffectTick(pLivingEntity, pAmplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
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
