package net.fryc.imbleeding.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class BrokenEffect extends StatusEffect {
    protected BrokenEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onApplied(entity, attributes, amplifier);
        if(entity.isSprinting()){
            entity.setSprinting(false);
        }
    }

}
