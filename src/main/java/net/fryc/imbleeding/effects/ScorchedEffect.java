package net.fryc.imbleeding.effects;

import net.fryc.imbleeding.util.BleedingHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class ScorchedEffect extends StatusEffect {

    protected ScorchedEffect(StatusEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }

    public void onApplied(LivingEntity entity, int amplifier) {
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
