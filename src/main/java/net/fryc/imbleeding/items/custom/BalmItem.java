package net.fryc.imbleeding.items.custom;

import net.fryc.imbleeding.effects.ModEffects;
import net.fryc.imbleeding.util.BleedingHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class BalmItem extends Item {
    public BalmItem(Properties settings) {
        super(settings);
    }

    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        user.heal(3.0F);
        /*
        if(user.hasStatusEffect(ModEffects.BLEED_EFFECT)){
            BleedingHelper.modifyStatusEffect(
                    ModEffects.BLEED_EFFECT,
                    user,
                    -(int)(user.getActiveStatusEffects().get(ModEffects.BLEED_EFFECT).getDuration() * 0.2F),
                    -256,
                    false
            );
        }
         */
        if(user.hasEffect(ModEffects.HEALTH_LOSS)){
            BleedingHelper.modifyStatusEffect(
                    ModEffects.HEALTH_LOSS,
                    user,
                    -(int)(user.getActiveEffectsMap().get(ModEffects.HEALTH_LOSS).getDuration() * 0.5F),
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
        ItemStack itemStack = super.finishUsingItem(stack, world, user);
        return user instanceof Player && ((Player)user).getAbilities().instabuild ? itemStack : new ItemStack(Items.BOWL);
    }

    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 25;
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(world, user, hand);
    }
}
