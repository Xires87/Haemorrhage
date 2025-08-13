package net.fryc.imbleeding.mixin;

import com.google.common.collect.ImmutableMultimap;
import net.fryc.imbleeding.attributes.ArmorBleedProt;
import net.fryc.imbleeding.attributes.ModEntityAttributes;
import net.fryc.imbleeding.util.FileHelper;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.EnumMap;
import java.util.Optional;
import java.util.UUID;

@Mixin(ArmorItem.class)
public class ArmorItemMixin {

    @Shadow
    private static @Final EnumMap<ArmorItem.Type, UUID> MODIFIERS;

    @ModifyVariable(method = "<init>", at = @At(
            value = "INVOKE",
            target = "Lcom/google/common/collect/ImmutableMultimap$Builder;build()Lcom/google/common/collect/ImmutableMultimap;"
    ), remap = false)
    private ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> addBleedingProtection(
            ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder,
            ArmorMaterial material,
            ArmorItem.Type type,
            Item.Settings settings
    ) {
        Optional<ArmorBleedProt> optional = Optional.ofNullable(FileHelper.ARMOR_BLEED_PROT_MAP.putIfAbsent(material.getName(), new ArmorBleedProt(
                material.getProtection(ArmorItem.Type.HELMET) * 3 + material.getToughness() * 4,
                material.getProtection(ArmorItem.Type.CHESTPLATE) * 3 + material.getToughness() * 4,
                material.getProtection(ArmorItem.Type.LEGGINGS) * 3 + material.getToughness() * 4,
                material.getProtection(ArmorItem.Type.BOOTS) * 3 + material.getToughness() * 4
        )));
        ArmorBleedProt bleedProt = optional.orElseGet(() -> {
            return FileHelper.ARMOR_BLEED_PROT_MAP.get(material.getName());
        });
        double value = getBleedProtectionForSlot(bleedProt, type);

        if(value != 0){
            builder.put(ModEntityAttributes.GENERIC_BLEEDING_PROTECTION, new EntityAttributeModifier(
                    MODIFIERS.get(type),
                    "Armor bleeding resistance",
                    value,
                    EntityAttributeModifier.Operation.ADDITION)
            );
        }

        return builder;
    }

    private static double getBleedProtectionForSlot(ArmorBleedProt bleedProt, ArmorItem.Type type){
        return switch (type) {
            case HELMET -> bleedProt.helmet();
            case CHESTPLATE -> bleedProt.chestplate();
            case LEGGINGS -> bleedProt.leggings();
            case BOOTS -> bleedProt.boots();
        };
    }
}
