package net.fryc.imbleeding.items.custom;

import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;

public class SoakedBandageItem extends BandageItem {

    public static final float SOAKED_BANDAGE_DURATION_MULTIPLIER = 0.60f;
    public static final HashSet<Potion> SPECIAL_TRANSLATION_POTIONS = Sets.newHashSet(
            Potions.WATER.value(),
            Potions.NIGHT_VISION.value(),
            Potions.LONG_NIGHT_VISION.value(),
            Potions.INVISIBILITY.value(),
            Potions.LONG_INVISIBILITY.value(),
            Potions.LEAPING.value(),
            Potions.LONG_LEAPING.value(),
            Potions.STRONG_LEAPING.value(),
            Potions.FIRE_RESISTANCE.value(),
            Potions.LONG_FIRE_RESISTANCE.value(),
            Potions.SWIFTNESS.value(),
            Potions.LONG_SWIFTNESS.value(),
            Potions.STRONG_SWIFTNESS.value(),
            Potions.SLOWNESS.value(),
            Potions.LONG_SLOWNESS.value(),
            Potions.STRONG_SLOWNESS.value(),
            Potions.TURTLE_MASTER.value(),
            Potions.LONG_TURTLE_MASTER.value(),
            Potions.STRONG_TURTLE_MASTER.value(),
            Potions.WATER_BREATHING.value(),
            Potions.LONG_WATER_BREATHING.value(),
            Potions.HEALING.value(),
            Potions.STRONG_HEALING.value(),
            Potions.HARMING.value(),
            Potions.STRONG_HARMING.value(),
            Potions.POISON.value(),
            Potions.LONG_POISON.value(),
            Potions.STRONG_POISON.value(),
            Potions.REGENERATION.value(),
            Potions.LONG_REGENERATION.value(),
            Potions.STRONG_REGENERATION.value(),
            Potions.STRENGTH.value(),
            Potions.LONG_STRENGTH.value(),
            Potions.STRONG_STRENGTH.value(),
            Potions.WEAKNESS.value(),
            Potions.LONG_WEAKNESS.value(),
            Potions.LUCK.value(),
            Potions.SLOW_FALLING.value(),
            Potions.LONG_SLOW_FALLING.value(),
            Potions.OOZING.value(),
            Potions.WIND_CHARGED.value(),
            Potions.INFESTED.value(),
            Potions.WEAVING.value()
    );

    public SoakedBandageItem(Properties settings) {
        super(settings);
    }

    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        if (!world.isClientSide) {
            PotionContents potionContentsComponent = (PotionContents)stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            potionContentsComponent.forEachEffect((effect) -> {
                if (((MobEffect)effect.getEffect().value()).isInstantenous()) {
                    ((MobEffect)effect.getEffect().value()).applyInstantenousEffect(user, user, user, effect.getAmplifier(), 1.0);
                } else {
                    user.addEffect(new MobEffectInstance(effect.getEffect(), (int)(effect.getDuration()*SOAKED_BANDAGE_DURATION_MULTIPLIER), effect.getAmplifier(), effect.isAmbient(), effect.isVisible(), effect.showIcon()));
                }

            });
        }
        return super.finishUsingItem(stack, world, user);
    }

    public ItemStack getDefaultInstance() {
        return PotionContents.createItemStack(this, Potions.HEALING);
    }

    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        PotionContents potionContentsComponent = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        if(potionContentsComponent.potion().isPresent()){
            if(potionContentsComponent.potion().get() != Potions.WATER){
                potionContentsComponent.addPotionTooltip(tooltip::add, SOAKED_BANDAGE_DURATION_MULTIPLIER, context.tickRate());
            }
        }
    }

    public String getDescriptionId(ItemStack stack) {
        PotionContents potionContentsComponent = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        if(potionContentsComponent.potion().isPresent()){
            potionContentsComponent.potion().get().value();
            return SPECIAL_TRANSLATION_POTIONS.contains(potionContentsComponent.potion().get().value()) ? Potion.getName(potionContentsComponent.potion(), this.getDescriptionId() + ".effect.") : this.getDescriptionId();
        }
        return this.getDescriptionId();
    }

}
