package net.fryc.imbleeding.effects;

import net.fryc.imbleeding.ImBleeding;
import net.fryc.imbleeding.attributes.ModEntityAttributes;
import net.fryc.imbleeding.attributes.StatusEffectAttributeModifier;
import net.fryc.imbleeding.util.FileHelper;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.List;

public class ModEffects {
    public static RegistryEntry<StatusEffect> BLEED_EFFECT;
    public static RegistryEntry<StatusEffect> BLEEDOUT;
    public static RegistryEntry<StatusEffect> HEALTH_LOSS;

    public static RegistryEntry<StatusEffect> BROKEN;
    public static RegistryEntry<StatusEffect> WOUND_INFECTION;

    private static final String BLEED_ID = "bleed";
    private static final String BLEEDOUT_ID = "bleedout";
    private static final String HEALTH_LOSS_ID = "health_loss";
    private static final String BROKEN_ID = "broken";
    private static final String WOUND_INFECTION_ID = "wound_infection";


    private static RegistryEntry<StatusEffect> registerStatusEffect(String id, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(ImBleeding.MOD_ID, id), addAllAttributeModifiers(id, statusEffect));
    }



    public static void registerEffects() {
        if(BLEED_EFFECT == null){
            BLEED_EFFECT = registerStatusEffect(BLEED_ID, new BleedEffect(StatusEffectCategory.HARMFUL, 16262179));
            BLEEDOUT = registerStatusEffect(BLEEDOUT_ID , new BleedoutEffect(StatusEffectCategory.HARMFUL, 16262179).addAttributeModifier(EntityAttributes.GENERIC_MAX_HEALTH, Identifier.of(ImBleeding.MOD_ID, "effect.bleedout"), -12.0, EntityAttributeModifier.Operation.ADD_VALUE));
            HEALTH_LOSS = registerStatusEffect(HEALTH_LOSS_ID, new HealthLossEffect(StatusEffectCategory.HARMFUL, 16262179).addAttributeModifier(EntityAttributes.GENERIC_MAX_HEALTH, Identifier.of(ImBleeding.MOD_ID, "effect.health_loss"), -2.0, EntityAttributeModifier.Operation.ADD_VALUE));
            BROKEN = registerStatusEffect(BROKEN_ID, new BrokenEffect(StatusEffectCategory.HARMFUL, 9154528).addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, Identifier.of(ImBleeding.MOD_ID, "effect.broken"), -0.15, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            WOUND_INFECTION = registerStatusEffect(WOUND_INFECTION_ID, new WoundInfectionEffect(StatusEffectCategory.HARMFUL, 7889189).addAttributeModifier(ModEntityAttributes.GENERIC_BLEEDING_PROTECTION, Identifier.of(ImBleeding.MOD_ID, "effect.wound_infection"), -60, EntityAttributeModifier.Operation.ADD_VALUE));
        }
    }

    private static StatusEffect addAllAttributeModifiers(String id, StatusEffect effect) {
        if(!FileHelper.STATUS_EFFECT_MODIFIERS_MAP.containsKey(id)) {
            FileHelper.STATUS_EFFECT_MODIFIERS_MAP.put(id, getDefaultModifiers(id));
        }

        FileHelper.STATUS_EFFECT_MODIFIERS_MAP.get(id).forEach(modifier -> {
            RegistryEntry<EntityAttribute> attribute = Registries.ATTRIBUTE.getEntry(Registries.ATTRIBUTE.get(Identifier.of(modifier.attributeId())));
            if(attribute != null) {
                effect.addAttributeModifier(attribute, Identifier.of(ImBleeding.MOD_ID, modifier.effectId()), modifier.value(), getOperationFromName(modifier.operation()));
            }
            else {
                ImBleeding.LOGGER.error("Failed adding attribute modifier to " + id + ". Unknown attribute: " + modifier.attributeId());
            }
        });

        return effect;
    }

    private static List<StatusEffectAttributeModifier> getDefaultModifiers(String id) {
        return switch (id) {
            case BLEED_ID -> List.of();
            case BLEEDOUT_ID -> List.of(new StatusEffectAttributeModifier(EntityAttributes.GENERIC_MAX_HEALTH.getIdAsString(), "effect.bleedout", -0.6, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL.asString()));
            case HEALTH_LOSS_ID -> List.of(new StatusEffectAttributeModifier(EntityAttributes.GENERIC_MAX_HEALTH.getIdAsString(), "effect.health_loss", -2.0, EntityAttributeModifier.Operation.ADD_VALUE.asString()));
            case BROKEN_ID -> List.of(new StatusEffectAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED.getIdAsString(), "effect.broken", -0.15, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL.asString()));
            case WOUND_INFECTION_ID -> List.of(new StatusEffectAttributeModifier(ModEntityAttributes.GENERIC_BLEEDING_PROTECTION.getIdAsString(), "effect.wound_infection", -60.0, EntityAttributeModifier.Operation.ADD_VALUE.asString()));
            default -> throw new IllegalArgumentException("Unexpected value: " + id);
        };
    }

    private static EntityAttributeModifier.Operation getOperationFromName(String name) {
        if(name.equals(EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE.asString())) {
            return EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE;
        }
        else if(name.equals(EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL.asString())) {
            return EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
        }

        return EntityAttributeModifier.Operation.ADD_VALUE;
    }
}
