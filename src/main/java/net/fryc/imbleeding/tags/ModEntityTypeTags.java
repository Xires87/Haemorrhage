package net.fryc.imbleeding.tags;

import net.fryc.imbleeding.ImBleeding;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class ModEntityTypeTags {

    public static final TagKey<EntityType<?>> BLEEDING_PROJECTILES = register("projectiles_causing_bleeding");
    public static final TagKey<EntityType<?>> NO_BLEEDING_APPLY_MOBS = register("no_bleeding_apply_mobs");

    public static final TagKey<EntityType<?>> MOBS_CAUSING_HEALTH_LOSS = register("mobs_causing_health_loss");

    public static final TagKey<EntityType<?>> CAN_BLEED = register("can_bleed");
    public static final TagKey<EntityType<?>> BLEED_RESISTANT_TO = register("bleed_resistant_to");
    public static final TagKey<EntityType<?>> HEALTH_LOSS_RESISTANT_TO = register("health_loss_resistant_to");


    private ModEntityTypeTags(){
    }

    private static TagKey<EntityType<?>> register(String id) {
        return TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(ImBleeding.MOD_ID, id));
    }
}
