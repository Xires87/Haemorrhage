package net.fryc.imbleeding.tags;

import net.fryc.imbleeding.ImBleeding;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public final class ModDamageTypeTags {

    public static TagKey<DamageType> DAMAGE_APPLY_BLEED = register("damage_apply_bleed");
    public static TagKey<DamageType> DAMAGE_APPLY_HEALTH_LOSS = register("damage_apply_health_loss");


    private ModDamageTypeTags(){
    }
    private static TagKey<DamageType> register(String id) {
        return TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(ImBleeding.MOD_ID, id));
    }
}
