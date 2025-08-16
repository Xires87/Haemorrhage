package net.fryc.imbleeding.mixin;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fryc.imbleeding.attributes.ArmorBleedProt;
import net.fryc.imbleeding.attributes.ModEntityAttributes;
import net.fryc.imbleeding.util.FileHelper;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorItem.class)
public class ArmorItemMixin {

    @Shadow
    protected @Final ArmorItem.Type type;
    @Shadow
    protected @Final RegistryEntry<ArmorMaterial> material;

    private Supplier<AttributeModifiersComponent> attributeModifiersComponentSupplier;


    @Inject(method = "<init>(Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/item/ArmorItem$Type;Lnet/minecraft/item/Item$Settings;)V", at = @At(value = "TAIL"))
    private void saveBleedingProtectionToMap(RegistryEntry<ArmorMaterial> material, ArmorItem.Type type, Item.Settings settings, CallbackInfo info) {
        Identifier id = Registries.ARMOR_MATERIAL.getId(material.value());
        if(id != null){
            FileHelper.ARMOR_BLEED_PROT_MAP.putIfAbsent(id.toString(), new ArmorBleedProt(
                    material.value().getProtection(ArmorItem.Type.HELMET) * 3 + material.value().toughness() * 4,
                    material.value().getProtection(ArmorItem.Type.CHESTPLATE) * 3 + material.value().toughness() * 4,
                    material.value().getProtection(ArmorItem.Type.LEGGINGS) * 3 + material.value().toughness() * 4,
                    material.value().getProtection(ArmorItem.Type.BOOTS) * 3 + material.value().toughness() * 4
            ));
        }
    }


    @ModifyReturnValue(
            method = "getAttributeModifiers()Lnet/minecraft/component/type/AttributeModifiersComponent;",
            at = @At("RETURN")
    )
    private AttributeModifiersComponent addBleedingProtection(AttributeModifiersComponent original) {
        if(this.attributeModifiersComponentSupplier == null){
            this.attributeModifiersComponentSupplier = Suppliers.memoize(() -> {
                Identifier id = Registries.ARMOR_MATERIAL.getId(this.material.value());
                if(id != null){
                    ArmorBleedProt prot = FileHelper.ARMOR_BLEED_PROT_MAP.get(id.toString());

                    if(prot != null){
                        double value = getBleedProtectionForSlot(prot, this.type);

                        if(value != 0){
                            Identifier identifier = Identifier.ofVanilla("armor." + type.getName());

                            return original.with(ModEntityAttributes.GENERIC_BLEEDING_PROTECTION, new EntityAttributeModifier(
                                    identifier,
                                    value,
                                    EntityAttributeModifier.Operation.ADD_VALUE
                            ), AttributeModifierSlot.forEquipmentSlot(this.type.getEquipmentSlot()));
                        }
                    }
                }

                return original;
            });
        }


        return this.attributeModifiersComponentSupplier.get();
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
