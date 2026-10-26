package net.fryc.imbleeding.attributes;

import net.fryc.imbleeding.ImBleeding;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class ModEntityAttributes {

    public static final Holder<Attribute> GENERIC_BLEEDING_PROTECTION = register(
            "generic.bleeding_protection",
            (new RangedAttribute("imbleeding.attribute.name.generic.bleeding_protection", 4.0, 0.0, 200.0)).setSyncable(true)
    );


    public static void registerModEntityAttributes(){
    }


    private static Holder<Attribute> register(String id, Attribute attribute) {
        return Registry.registerForHolder(BuiltInRegistries.ATTRIBUTE, ResourceLocation.fromNamespaceAndPath(ImBleeding.MOD_ID, id), attribute);
    }
}
