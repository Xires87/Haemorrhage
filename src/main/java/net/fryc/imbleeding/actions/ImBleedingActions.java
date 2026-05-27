package net.fryc.imbleeding.actions;

import net.fryc.frycmua.action.ItemUseInstance;
import net.fryc.frycmua.action.registry.UseActionRegistries;
import net.fryc.imbleeding.ImBleeding;
import net.fryc.imbleeding.items.custom.BalmItem;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;

public class ImBleedingActions {

    public static final Identifier WOUND_DISINFECT_ID = Identifier.of(ImBleeding.MOD_ID, "wound_disinfect");

    public static final ItemUseInstance WOUND_DISINFECT_HONEY_BOTTLE = new ItemUseInstance(
            HoneyBottleItem.class /* <-- targeted class */,
            true /* <-- allow subclasses */,
            (item, world, user, hand) -> {
                // Item#use equivalent
                return ItemUsage.consumeHeldItem(world, user, hand);
            },
            (world, user, stack, remainingUseTicks) -> {
                // Item#tickUsage equivalent
                stack.getItem().usageTick(world, user, stack, remainingUseTicks);
            },
            (stack, world, user, remainingUseTicks) -> {
                // Item#onStoppedUsing equivalent
                stack.getItem().onStoppedUsing(stack, world, user, remainingUseTicks);
            },
            (stack, world, user) -> {
                stack.decrementUnlessCreative(1, user);
                if (user instanceof ServerPlayerEntity serverPlayerEntity) {
                    Criteria.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
                    serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
                }

                // TODO dac tu heala a usunac w jedzeniu
                // TODO decrease wound infection chance

                if (stack.isEmpty()) {
                    return new ItemStack(Items.GLASS_BOTTLE);
                } else {
                    if (user instanceof PlayerEntity) {
                        PlayerEntity playerEntity = (PlayerEntity)user;
                        if (!playerEntity.isInCreativeMode()) {
                            ItemStack itemStack = new ItemStack(Items.GLASS_BOTTLE);
                            if (!playerEntity.getInventory().insertStack(itemStack)) {
                                playerEntity.dropItem(itemStack, false);
                            }
                        }
                    }

                    return stack;
                }
            },
            (stack, user) -> {
                // Item#getMaxUseTime equivalent
                return 70;
            },
            stack -> {
                // Item#getUseAction equivalent
                return UseAction.NONE;
            },
            stack -> {
                // Item#isUsedOnRelease equivalent
                return false;
            },
            (player, hand) -> {
                // this predicate is run on client side before launching the action
                // the action will be skipped if it returns false
                return true;
            }
    );

    // Item#use equivalent
    public static final ItemUseInstance WOUND_DISINFECT_HERBAL_BALM = new ItemUseInstance(
            BalmItem.class /* <-- targeted class */,
            true /* <-- allow subclasses */,
            Item::use,
            (world, user, stack, remainingUseTicks) -> {
                // Item#tickUsage equivalent
                stack.getItem().usageTick(world, user, stack, remainingUseTicks);
            },
            (stack, world, user, remainingUseTicks) -> {
                // Item#onStoppedUsing equivalent
                stack.getItem().onStoppedUsing(stack, world, user, remainingUseTicks);
            },
            (stack, world, user) -> {
                return stack.getItem().finishUsing(stack, world, user);
            },
            (stack, user) -> {
                // Item#getMaxUseTime equivalent
                return stack.getItem().getMaxUseTime(stack, user);
            },
            stack -> {
                // Item#getUseAction equivalent
                return stack.getItem().getUseAction(stack);
            },
            stack -> {
                // Item#isUsedOnRelease equivalent
                return stack.getItem().isUsedOnRelease(stack);
            },
            (player, hand) -> {
                // this predicate is run on client side before launching the action
                // the action will be skipped if it returns false
                return true;
            }
    );

    public static void registerImBleedingActions() {
        UseActionRegistries.registerUseAction(WOUND_DISINFECT_ID, WOUND_DISINFECT_HONEY_BOTTLE);
        UseActionRegistries.registerUseAction(WOUND_DISINFECT_ID, WOUND_DISINFECT_HERBAL_BALM);
    }
}
