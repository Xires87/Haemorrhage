package net.fryc.imbleeding.util.mixin_interfaces;

public interface CanBleed {

    //for LivingEntity

    float getScorchedFactor();

    float getWoundInfectionFactor();

    void modifyScorchedFactor(float amount, boolean multiply);
    void modifyWoundInfectionFactor(float amount, boolean multiply);
}
