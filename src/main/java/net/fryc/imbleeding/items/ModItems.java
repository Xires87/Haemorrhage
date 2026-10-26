package net.fryc.imbleeding.items;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fryc.imbleeding.ImBleeding;
import net.fryc.imbleeding.items.custom.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;

public class ModItems {

    //Item Group
    public static final ResourceKey<CreativeModeTab> HAEMORRHAGE = ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath(ImBleeding.MOD_ID, "haemorrhage_item_group"));

    public static final Item BANDAGE = registerItem("bandage" ,
            new BandageItem(new Item.Properties().stacksTo(16)));
    public static final Item HONEY_BANDAGE = registerItem("honey_bandage" ,
            new BandageItem(new Item.Properties().stacksTo(16)));
    public static final Item STICKY_BANDAGE = registerItem("sticky_bandage" ,
            new BandageItem(new Item.Properties().stacksTo(16)));

    public static final Item HERBAL_BALM = registerItem("herbal_balm" ,
            new BalmItem(new Item.Properties().stacksTo(1)));

    public static final Item SPLINT = registerItem("splint" ,
            new SplintItem(new Item.Properties().stacksTo(16)));

    public static final Item SOAKED_BANDAGE = registerItem("soaked_bandage" ,
            new SoakedBandageItem(new Item.Properties().stacksTo(16)));

    public static final Item LEATHER_STRIPS = registerItem("leather_strips" ,
            new LeatherStripsItem(new Item.Properties().stacksTo(16)));

    private static Item registerItem(String name, Item item){
        return Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(ImBleeding.MOD_ID, name), item);
    }
    public static void registerModItems(){
        //Item Group
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, HAEMORRHAGE, FabricItemGroup.builder()
                .icon(() -> new ItemStack(ModItems.BANDAGE))
                .title(Component.literal("Haemorrhage"))
                .displayItems((enabledFeatures, entries) -> {
                    entries.accept(ModItems.BANDAGE);
                    entries.accept(ModItems.HONEY_BANDAGE);
                    entries.accept(ModItems.STICKY_BANDAGE);
                    entries.accept(ModItems.HERBAL_BALM);
                    entries.accept(ModItems.SPLINT);
                    entries.accept(ModItems.LEATHER_STRIPS);
                    enabledFeatures.holders().lookup(Registries.POTION).ifPresent((wrapper) -> {
                        addPotions(entries, wrapper, ModItems.SOAKED_BANDAGE, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, enabledFeatures.enabledFeatures());
                    });
                })
                .build());

    }

    private static void addPotions(CreativeModeTab.Output entries, HolderLookup<Potion> registryWrapper, Item item, CreativeModeTab.TabVisibility visibility, FeatureFlagSet enabledFeatures) {
        registryWrapper.listElements().filter((potionEntry) -> {
            return ((Potion)potionEntry.value()).isEnabled(enabledFeatures);
        }).map((entry) -> {
            return PotionContents.createItemStack(item, entry);
        }).forEach((stack) -> {
            entries.accept(stack, visibility);
        });
    }
}
