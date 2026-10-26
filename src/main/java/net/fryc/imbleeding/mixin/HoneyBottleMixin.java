package net.fryc.imbleeding.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.HoneyBottleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HoneyBottleItem.class)
abstract class HoneyBottleMixin extends Item {

    public HoneyBottleMixin(Properties settings) {
        super(settings);
    }

    //Honey bottle heals
    @Inject(at = @At("HEAD"), method = "finishUsingItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;")
    private void heal(ItemStack stack, Level world, LivingEntity user ,CallbackInfoReturnable<Object> info) {
        if (!world.isClientSide) {
            user.heal(1f);
        }
    }
}
