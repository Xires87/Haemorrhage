package net.fryc.imbleeding.mixin;

import net.fryc.imbleeding.ImBleeding;
import net.fryc.imbleeding.util.BleedingHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    // PlayerEntity overrides applyDamage, so I have to mixin here too
    @Inject(method = "actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V", at = @At("TAIL"))
    public void applyDamageEffectsOnPlayer(DamageSource source, float amount, CallbackInfo ci) {
        if(BleedingHelper.shouldApplyDarkness(this)){
            BleedingHelper.applyDarkness(this);
        }

        if(BleedingHelper.canGetBleeding(this)){
            if(BleedingHelper.canApplyBleeding(source, amount)){
                BleedingHelper.applyBleeding(this, source, amount);
            }
        }

        if(BleedingHelper.canGetHealthLoss(this)){
            if(BleedingHelper.canApplyHealthLoss(source)){
                BleedingHelper.applyHealthLoss(this, source, amount);
            }
        }

        if(BleedingHelper.shouldIncreaseScorchedFactor(source, this.level())){
            BleedingHelper.increaseScorchedFactor(this, amount, 0.38F);
            BleedingHelper.extendScorchedDuration(this, amount);
        }

        if(BleedingHelper.shouldApplyBrokenEffect(source, amount, this)){
            BleedingHelper.applyBrokenEffect(this, amount);
        }

    }

    //TODO
    //Stops food healing while bleeding
    @Inject(method = "canFoodHeal()Z", at = @At("RETURN"), cancellable = true)
    public void noHealing(CallbackInfoReturnable<Boolean> ret) {
        Player player = ((Player) (Object) this);
        if(ImBleeding.config.bleedingStopsFoodHealing && BleedingHelper.shouldStopFoodHealing(player)){
            ret.setReturnValue(false);
        }
    }



    //Gives blindness if player has 1 hp or lower
    @Inject(method = "tick()V", at = @At("TAIL"))
    public void setBlindness(CallbackInfo info){
        BleedingHelper.applyBlindness(this);
    }

}
