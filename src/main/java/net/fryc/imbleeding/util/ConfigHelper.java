package net.fryc.imbleeding.util;

import net.fryc.imbleeding.ImBleeding;
import net.minecraft.world.level.Level;

public class ConfigHelper {

    public static boolean enableCombatRollCompatibility = ImBleeding.config.enableCombatRollCompatibility;

    public static boolean isCombatRollCompatibilityEnabled(Level world){
        if(world.isClientSide()){
            return enableCombatRollCompatibility;
        }
        return ImBleeding.config.enableCombatRollCompatibility;
    }
}
