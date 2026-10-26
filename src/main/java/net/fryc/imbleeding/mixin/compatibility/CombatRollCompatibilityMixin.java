package net.fryc.imbleeding.mixin.compatibility;

import net.fryc.imbleeding.effects.ModEffects;
import net.fryc.imbleeding.util.ConfigHelper;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "net.combatroll.client.RollManager")
abstract class CombatRollCompatibilityMixin {


    @Inject(method = "isRollAvailable(Lnet/minecraft/entity/player/PlayerEntity;)Z", at = @At("HEAD"), cancellable = true)
    private void disableRollingWhenBroken(Player player, CallbackInfoReturnable<Boolean> ret) {
        if(ConfigHelper.isCombatRollCompatibilityEnabled(player.level())){
            if(player.hasEffect(ModEffects.BROKEN)){
                ret.setReturnValue(false);
            }
        }
    }


}
