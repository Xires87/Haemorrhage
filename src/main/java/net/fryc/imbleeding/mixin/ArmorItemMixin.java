package net.fryc.imbleeding.mixin;

import com.google.common.collect.ImmutableMultimap;
import net.fryc.imbleeding.attributes.ModEntityAttributes;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ArmorItem.class)
public class ArmorItemMixin {


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

        builder.put(ModEntityAttributes.GENERIC_BLEEDING_PROTECTION, new EntityAttributeModifier(
                ModEntityAttributes.BLEEDING_PROTECTION_MODIFIER_UUID,
                "Armor bleeding resistance",
                material.getProtection(type) * 3 + material.getToughness() * 4,
                EntityAttributeModifier.Operation.ADDITION)
        );

        return builder;
    }
}
