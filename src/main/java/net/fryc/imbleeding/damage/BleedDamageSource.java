package net.fryc.imbleeding.damage;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.text.Text;

public class BleedDamageSource extends DamageSource {


    public BleedDamageSource(String name) {
        super(name);
        this.setBypassesArmor();
        this.setBypassesProtection();
        this.setUnblockable();
    }

    @Override
    public Text getDeathMessage(LivingEntity killed) {
        return Text.of(killed.getEntityName() + " bled out");
    }
}
