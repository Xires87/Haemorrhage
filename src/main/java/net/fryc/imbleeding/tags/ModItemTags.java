package net.fryc.imbleeding.tags;

import net.fryc.imbleeding.ImBleeding;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class ModItemTags {

    public static TagKey<Item> TULIPS = register( "tulips");
    public static TagKey<Item> BANDAGES = register( "bandages");
    public static TagKey<Item> WEAPONS_PIERCE_BLEEDING_PROTECTION = register( "weapons_pierce_bleeding_protection");


    private ModItemTags(){
    }
    private static TagKey<Item> register(String id) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ImBleeding.MOD_ID, id));
    }
}
