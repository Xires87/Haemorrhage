package net.fryc.imbleeding.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class BrokenEffect extends MobEffect {
    protected BrokenEffect(MobEffectCategory category, int color) {
        super(category, color);
    }


    public void onEffectStarted(LivingEntity entity, int amplifier) {
        super.onEffectStarted(entity, amplifier);
        if(entity.isSprinting()){
            entity.setSprinting(false);
        }
    }

}
