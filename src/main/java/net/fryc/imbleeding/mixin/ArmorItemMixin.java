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
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
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

    @ModifyVariable(method = "<init>", at = @At(
            value = "INVOKE",
            target = "Lcom/google/common/collect/ImmutableMultimap$Builder;build()Lcom/google/common/collect/ImmutableMultimap;"
    ), remap = false)
    private ImmutableMultimap.Builder<RegistryEntry<EntityAttribute>, EntityAttributeModifier> addBleedingProtection(
            ImmutableMultimap.Builder<RegistryEntry<EntityAttribute>, EntityAttributeModifier> builder,
            RegistryEntry<ArmorMaterial> material,
            ArmorItem.Type type,
            Item.Settings settings
    ) {
        Identifier id = Registries.ARMOR_MATERIAL.getId(material.value());
        if(id == null){
            return builder;
        }

        Optional<ArmorBleedProt> optional = Optional.ofNullable(FileHelper.ARMOR_BLEED_PROT_MAP.putIfAbsent(id.toString(), new ArmorBleedProt(
                material.value().getProtection(ArmorItem.Type.HELMET) * 3 + material.value().toughness() * 4,
                material.value().getProtection(ArmorItem.Type.CHESTPLATE) * 3 + material.value().toughness() * 4,
                material.value().getProtection(ArmorItem.Type.LEGGINGS) * 3 + material.value().toughness() * 4,
                material.value().getProtection(ArmorItem.Type.BOOTS) * 3 + material.value().toughness() * 4
        )));
        ArmorBleedProt bleedProt = optional.orElseGet(() -> {
            return FileHelper.ARMOR_BLEED_PROT_MAP.get(id.toString());
        });
        double value = getBleedProtectionForSlot(bleedProt, type);

        Identifier identifier = Identifier.ofVanilla("armor." + type.getName());

        if(value != 0){
            builder.put(ModEntityAttributes.GENERIC_BLEEDING_PROTECTION, new EntityAttributeModifier(
                    identifier,
                    value,
                    EntityAttributeModifier.Operation.ADD_VALUE)
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
            case BODY -> 48;
        };
    }
}
