package net.fryc.imbleeding.items.custom;

import net.fryc.imbleeding.effects.ModEffects;
import net.fryc.imbleeding.util.BleedingHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class LeatherStripsItem extends Item {

    public LeatherStripsItem(Item.Properties settings) {
        super(settings);
    }

    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        super.finishUsingItem(stack, world, user);
        // TODO wound infection chance increase
        if (!world.isClientSide) {
            BleedingHelper.modifyStatusEffect(ModEffects.BLEED_EFFECT, user, 0, -1, true);
            if(!((Player)user).getAbilities().instabuild) stack.setCount(stack.getCount() - 1);
            user.level().playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 1.0F, 1.0F);
            ((Player) user).getCooldowns().addCooldown(this, 8);
        }
        return stack;
    }

    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 90;
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(world, user, hand);
    }
}
