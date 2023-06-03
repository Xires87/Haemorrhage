package net.fryc.imbleeding.tags;

import net.fryc.imbleeding.ImBleeding;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class ModEntityTypeTags {

    public static final TagKey<EntityType<?>> BLEEDING_PROJECTILES = register("projectiles_causing_bleeding");
    public static final TagKey<EntityType<?>> NO_BLEEDING_APPLY_MOBS = register("no_bleeding_apply_mobs");


    private ModEntityTypeTags(){
    }

    private static TagKey<EntityType<?>> register(String id) {
        return TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier(ImBleeding.MOD_ID, id));
    }
}
