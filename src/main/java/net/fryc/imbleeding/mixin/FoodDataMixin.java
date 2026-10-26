package net.fryc.imbleeding.mixin;

import net.fryc.imbleeding.util.BleedingHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FoodData.class)
abstract class FoodDataMixin {

    @ModifyVariable(method = "tick(Lnet/minecraft/world/entity/player/Player;)V", at = @At("STORE"), ordinal = 0)
    private boolean preventFoodHealingWhileBleeding(boolean naturalRegen, Player player) {
        // return canFoodHeal
        return naturalRegen && !BleedingHelper.shouldStopFoodHealing(player);
    }
}
