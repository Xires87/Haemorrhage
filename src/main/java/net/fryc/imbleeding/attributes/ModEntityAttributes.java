package net.fryc.imbleeding.attributes;

import net.fryc.imbleeding.ImBleeding;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModEntityAttributes {

    public static final RegistryEntry<EntityAttribute> GENERIC_BLEEDING_PROTECTION = register(
            "generic.bleeding_protection",
            (new ClampedEntityAttribute("imbleeding.attribute.name.generic.bleeding_protection", 4.0, 0.0, 200.0)).setTracked(true)
    );


    public static void registerModEntityAttributes(){
    }


    private static RegistryEntry<EntityAttribute> register(String id, EntityAttribute attribute) {
        return Registry.registerReference(Registries.ATTRIBUTE, Identifier.of(ImBleeding.MOD_ID, id), attribute);
    }
}
