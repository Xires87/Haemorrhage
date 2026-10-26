package net.fryc.imbleeding.actions;

import net.fryc.frycmua.action.ItemUseInstance;
import net.fryc.frycmua.action.registry.UseActionRegistries;
import net.fryc.imbleeding.ImBleeding;
import net.fryc.imbleeding.items.custom.BalmItem;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoneyBottleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;

public class ImBleedingActions {

    public static final ResourceLocation WOUND_DISINFECT_ID = ResourceLocation.fromNamespaceAndPath(ImBleeding.MOD_ID, "wound_disinfect");

    public static final ItemUseInstance WOUND_DISINFECT_HONEY_BOTTLE = new ItemUseInstance(
            HoneyBottleItem.class /* <-- targeted class */,
            true /* <-- allow subclasses */,
            (item, world, user, hand) -> {
                // Item#use equivalent
                return ItemUtils.startUsingInstantly(world, user, hand);
            },
            (world, user, stack, remainingUseTicks) -> {
                // Item#tickUsage equivalent
                stack.getItem().onUseTick(world, user, stack, remainingUseTicks);
            },
            (stack, world, user, remainingUseTicks) -> {
                // Item#onStoppedUsing equivalent
                stack.getItem().releaseUsing(stack, world, user, remainingUseTicks);
            },
            (stack, world, user) -> {
                stack.consume(1, user);
                if (user instanceof ServerPlayer serverPlayerEntity) {
                    CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
                    serverPlayerEntity.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                }

                // TODO dac tu heala a usunac w jedzeniu
                // TODO decrease wound infection chance

                if (stack.isEmpty()) {
                    return new ItemStack(Items.GLASS_BOTTLE);
                } else {
                    if (user instanceof Player) {
                        Player playerEntity = (Player)user;
                        if (!playerEntity.hasInfiniteMaterials()) {
                            ItemStack itemStack = new ItemStack(Items.GLASS_BOTTLE);
                            if (!playerEntity.getInventory().add(itemStack)) {
                                playerEntity.drop(itemStack, false);
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
                return UseAnim.NONE;
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
                stack.getItem().onUseTick(world, user, stack, remainingUseTicks);
            },
            (stack, world, user, remainingUseTicks) -> {
                // Item#onStoppedUsing equivalent
                stack.getItem().releaseUsing(stack, world, user, remainingUseTicks);
            },
            (stack, world, user) -> {
                return stack.getItem().finishUsingItem(stack, world, user);
            },
            (stack, user) -> {
                // Item#getMaxUseTime equivalent
                return stack.getItem().getUseDuration(stack, user);
            },
            stack -> {
                // Item#getUseAction equivalent
                return stack.getItem().getUseAnimation(stack);
            },
            stack -> {
                // Item#isUsedOnRelease equivalent
                return stack.getItem().useOnRelease(stack);
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
