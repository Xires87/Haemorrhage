package net.fryc.imbleeding.attributes;

import net.fryc.imbleeding.ImBleeding;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class ModEntityAttributes {

    public static final UUID BLEEDING_PROTECTION_MODIFIER_UUID = UUID.fromString("ca8d1f50-faf7-49ff-bb9e-6b185d99224a");

    public static final EntityAttribute GENERIC_BLEEDING_PROTECTION = register(
            "generic.bleeding_protection",
            (new ClampedEntityAttribute("hammersandtables.attribute.name.generic.bleeding_protection", 4.0, 0.0, 200.0)).setTracked(true)
    );


    public static void registerModEntityAttributes(){
    }


    private static EntityAttribute register(String id, EntityAttribute attribute) {
        return Registry.register(Registries.ATTRIBUTE, Identifier.of(ImBleeding.MOD_ID, id), attribute);
    }
}
