package net.fryc.imbleeding.mixin;

import net.fryc.imbleeding.ImBleeding;
import net.fryc.imbleeding.util.BleedingHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    // PlayerEntity overrides applyDamage, so I have to mixin here too
    @Inject(method = "applyDamage(Lnet/minecraft/entity/damage/DamageSource;F)V", at = @At("TAIL"))
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

        if(BleedingHelper.shouldReduceBleedingWithFire(source, this.getWorld())){
            BleedingHelper.reduceBleedingWithFire(this);
        }

        if(BleedingHelper.shouldApplyBrokenEffect(source, amount, this)){
            BleedingHelper.applyBrokenEffect(this, amount);
        }

    }

    //Stops food healing while bleeding
    @Inject(method = "canFoodHeal()Z", at = @At("RETURN"), cancellable = true)
    public void noHealing(CallbackInfoReturnable<Boolean> ret) {
        PlayerEntity player = ((PlayerEntity) (Object) this);
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
