package net.fryc.imbleeding.effects;

import net.fryc.imbleeding.util.BleedingHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class ScorchedEffect extends MobEffect {

    protected ScorchedEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }

    public void onEffectStarted(LivingEntity entity, int amplifier) {
        BleedingHelper.modifyStatusEffect(ModEffects.BLEED_EFFECT, entity, getBleedingDurationReduction(amplifier), amplifier * -1, false);
    }

    private static int getBleedingDurationReduction(int amplifier) {
        return switch(amplifier) {
            case 0 -> -50;
            case 1 -> -120;
            case 2 -> -280;
            case 3 -> -840;
            default -> -2400;
        };
    }
}
