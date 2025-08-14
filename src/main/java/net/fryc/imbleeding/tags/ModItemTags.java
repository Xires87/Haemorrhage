package net.fryc.imbleeding.tags;

import net.fryc.imbleeding.ImBleeding;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class ModItemTags {

    public static TagKey<Item> TULIPS = register( "tulips");
    public static TagKey<Item> BANDAGES = register( "bandages");
    public static TagKey<Item> WEAPONS_PIERCE_BLEEDING_PROTECTION = register( "weapons_pierce_bleeding_protection");


    private ModItemTags(){
    }
    private static TagKey<Item> register(String id) {
        return TagKey.of(RegistryKeys.ITEM, Identifier.of(ImBleeding.MOD_ID, id));
    }
}
