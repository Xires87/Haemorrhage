package net.fryc.imbleeding.mixin;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fryc.imbleeding.attributes.ArmorBleedProt;
import net.fryc.imbleeding.attributes.ModEntityAttributes;
import net.fryc.imbleeding.util.FileHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorItem.class)
public class ArmorItemMixin {

    @Shadow
    protected @Final ArmorItem.Type type;
    @Shadow
    protected @Final Holder<ArmorMaterial> material;

    @Unique
    private Supplier<ItemAttributeModifiers> attributeModifiersComponentSupplier;


    @Inject(method = "<init>(Lnet/minecraft/core/Holder;Lnet/minecraft/world/item/ArmorItem$Type;Lnet/minecraft/world/item/Item$Properties;)V", at = @At(value = "TAIL"))
    private void saveBleedingProtectionToMap(Holder<ArmorMaterial> material, ArmorItem.Type type, Item.Properties settings, CallbackInfo info) {
        ResourceLocation id = BuiltInRegistries.ARMOR_MATERIAL.getKey(material.value());
        if(id != null){
            FileHelper.ARMOR_BLEED_PROT_MAP.putIfAbsent(id.toString(), new ArmorBleedProt(
                    material.value().getDefense(ArmorItem.Type.HELMET) * 3 + material.value().toughness() * 4,
                    material.value().getDefense(ArmorItem.Type.CHESTPLATE) * 3 + material.value().toughness() * 4,
                    material.value().getDefense(ArmorItem.Type.LEGGINGS) * 3 + material.value().toughness() * 4,
                    material.value().getDefense(ArmorItem.Type.BOOTS) * 3 + material.value().toughness() * 4
            ));
        }
    }


    @ModifyReturnValue(
            method = "getDefaultAttributeModifiers()Lnet/minecraft/world/item/component/ItemAttributeModifiers;",
            at = @At("RETURN")
    )
    private ItemAttributeModifiers addBleedingProtection(ItemAttributeModifiers original) {
        if(this.attributeModifiersComponentSupplier == null){
            this.attributeModifiersComponentSupplier = Suppliers.memoize(() -> {
                ResourceLocation id = BuiltInRegistries.ARMOR_MATERIAL.getKey(this.material.value());
                if(id != null){
                    ArmorBleedProt prot = FileHelper.ARMOR_BLEED_PROT_MAP.get(id.toString());

                    if(prot != null){
                        double value = getBleedProtectionForSlot(prot, this.type);

                        if(value != 0){
                            ResourceLocation identifier = ResourceLocation.withDefaultNamespace("armor." + type.getName());

                            return original.withModifierAdded(ModEntityAttributes.GENERIC_BLEEDING_PROTECTION, new AttributeModifier(
                                    identifier,
                                    value,
                                    AttributeModifier.Operation.ADD_VALUE
                            ), EquipmentSlotGroup.bySlot(this.type.getSlot()));
                        }
                    }
                }

                return original;
            });
        }


        return this.attributeModifiersComponentSupplier.get();
    }

    @Unique
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
