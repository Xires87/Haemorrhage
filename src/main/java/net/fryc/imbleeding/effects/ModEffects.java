package net.fryc.imbleeding.effects;

import net.fryc.imbleeding.ImBleeding;
import net.fryc.imbleeding.attributes.ModEntityAttributes;
import net.fryc.imbleeding.attributes.StatusEffectAttributeModifier;
import net.fryc.imbleeding.util.FileHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import java.util.List;

public class ModEffects {
    public static Holder<MobEffect> BLEED_EFFECT;
    public static Holder<MobEffect> BLEEDOUT;
    public static Holder<MobEffect> HEALTH_LOSS;

    public static Holder<MobEffect> BROKEN;
    public static Holder<MobEffect> WOUND_INFECTION;
    public static Holder<MobEffect> SCORCHED;

    private static final String BLEED_ID = "bleed";
    private static final String BLEEDOUT_ID = "bleedout";
    private static final String HEALTH_LOSS_ID = "health_loss";
    private static final String BROKEN_ID = "broken";
    private static final String WOUND_INFECTION_ID = "wound_infection";
    private static final String SCORCHED_ID = "scorched";


    private static Holder<MobEffect> registerStatusEffect(String id, MobEffect statusEffect) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, ResourceLocation.fromNamespaceAndPath(ImBleeding.MOD_ID, id), addAllAttributeModifiers(id, statusEffect));
    }



    public static void registerEffects() {
        if(BLEED_EFFECT == null){
            BLEED_EFFECT = registerStatusEffect(BLEED_ID, new BleedEffect(MobEffectCategory.HARMFUL, 16262179));
            BLEEDOUT = registerStatusEffect(BLEEDOUT_ID , new BleedoutEffect(MobEffectCategory.HARMFUL, 16262179).addAttributeModifier(Attributes.MAX_HEALTH, ResourceLocation.fromNamespaceAndPath(ImBleeding.MOD_ID, "effect.bleedout"), -12.0, AttributeModifier.Operation.ADD_VALUE));
            HEALTH_LOSS = registerStatusEffect(HEALTH_LOSS_ID, new HealthLossEffect(MobEffectCategory.HARMFUL, 16262179).addAttributeModifier(Attributes.MAX_HEALTH, ResourceLocation.fromNamespaceAndPath(ImBleeding.MOD_ID, "effect.health_loss"), -2.0, AttributeModifier.Operation.ADD_VALUE));
            BROKEN = registerStatusEffect(BROKEN_ID, new BrokenEffect(MobEffectCategory.HARMFUL, 9154528).addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath(ImBleeding.MOD_ID, "effect.broken"), -0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            WOUND_INFECTION = registerStatusEffect(WOUND_INFECTION_ID, new WoundInfectionEffect(MobEffectCategory.HARMFUL, 7889189).addAttributeModifier(ModEntityAttributes.GENERIC_BLEEDING_PROTECTION, ResourceLocation.fromNamespaceAndPath(ImBleeding.MOD_ID, "effect.wound_infection"), -60, AttributeModifier.Operation.ADD_VALUE));
            SCORCHED = registerStatusEffect(SCORCHED_ID, new ScorchedEffect(MobEffectCategory.HARMFUL, 7889189));
        }
    }

    private static MobEffect addAllAttributeModifiers(String id, MobEffect effect) {
        if(!FileHelper.STATUS_EFFECT_MODIFIERS_MAP.containsKey(id)) {
            FileHelper.STATUS_EFFECT_MODIFIERS_MAP.put(id, getDefaultModifiers(id));
        }

        FileHelper.STATUS_EFFECT_MODIFIERS_MAP.get(id).forEach(modifier -> {
            Holder<Attribute> attribute = BuiltInRegistries.ATTRIBUTE.wrapAsHolder(BuiltInRegistries.ATTRIBUTE.get(ResourceLocation.parse(modifier.attributeId())));
            if(attribute != null) {
                effect.addAttributeModifier(attribute, ResourceLocation.fromNamespaceAndPath(ImBleeding.MOD_ID, modifier.effectId()), modifier.value(), getOperationFromName(modifier.operation()));
            }
            else {
                ImBleeding.LOGGER.error("Failed adding attribute modifier to " + id + ". Unknown attribute: " + modifier.attributeId());
            }
        });

        return effect;
    }

    private static List<StatusEffectAttributeModifier> getDefaultModifiers(String id) {
        return switch (id) {
            case BLEED_ID, SCORCHED_ID -> List.of();
            case BLEEDOUT_ID -> List.of(new StatusEffectAttributeModifier(Attributes.MAX_HEALTH.getRegisteredName(), "effect.bleedout", -0.6, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL.getSerializedName()));
            case HEALTH_LOSS_ID -> List.of(new StatusEffectAttributeModifier(Attributes.MAX_HEALTH.getRegisteredName(), "effect.health_loss", -2.0, AttributeModifier.Operation.ADD_VALUE.getSerializedName()));
            case BROKEN_ID -> List.of(new StatusEffectAttributeModifier(Attributes.MOVEMENT_SPEED.getRegisteredName(), "effect.broken", -0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL.getSerializedName()));
            case WOUND_INFECTION_ID -> List.of(new StatusEffectAttributeModifier(ModEntityAttributes.GENERIC_BLEEDING_PROTECTION.getRegisteredName(), "effect.wound_infection", -60.0, AttributeModifier.Operation.ADD_VALUE.getSerializedName()));
            default -> throw new IllegalArgumentException("Unexpected value: " + id);
        };
    }

    private static AttributeModifier.Operation getOperationFromName(String name) {
        if(name.equals(AttributeModifier.Operation.ADD_MULTIPLIED_BASE.getSerializedName())) {
            return AttributeModifier.Operation.ADD_MULTIPLIED_BASE;
        }
        else if(name.equals(AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL.getSerializedName())) {
            return AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
        }

        return AttributeModifier.Operation.ADD_VALUE;
    }
}
