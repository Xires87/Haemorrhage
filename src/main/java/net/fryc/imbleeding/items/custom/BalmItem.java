package net.fryc.imbleeding.items.custom;

import net.fryc.imbleeding.effects.ModEffects;
import net.fryc.imbleeding.util.BleedingHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class BalmItem extends Item {
    public BalmItem(Settings settings) {
        super(settings);
    }

    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        user.heal(3.0F);
        if(user.hasStatusEffect(ModEffects.BLEED_EFFECT)){
            BleedingHelper.modifyStatusEffect(
                    ModEffects.BLEED_EFFECT,
                    user,
                    -(int)(user.getActiveStatusEffects().get(ModEffects.BLEED_EFFECT).getDuration() * 0.2F),
                    -256,
                    false
            );
        }
        if(user.hasStatusEffect(ModEffects.HEALTH_LOSS)){
            BleedingHelper.modifyStatusEffect(
                    ModEffects.HEALTH_LOSS,
                    user,
                    -(int)(user.getActiveStatusEffects().get(ModEffects.HEALTH_LOSS).getDuration() * 0.5F),
                    -256,
                    false
            );
        }
        /* TODO odkomentowac jak zaimplementuje scorcha
        if(user.hasStatusEffect(ModEffects.SCORCHED)) {
            BleedingHelper.modifyStatusEffect(
                    ModEffects.SCORCHED,
                    user,
                    -(int)(user.getActiveStatusEffects().get(ModEffects.SCORCHED).getDuration() * 0.05F),
                    -1,
                    false
            );
        }
         */
        ItemStack itemStack = super.finishUsing(stack, world, user);
        return user instanceof PlayerEntity && ((PlayerEntity)user).getAbilities().creativeMode ? itemStack : new ItemStack(Items.BOWL);
    }

    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 25;
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }
}
