package net.fryc.imbleeding.recipes;

import net.fryc.imbleeding.ImBleeding;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

public class ModRecipeSerializers {

    public static final RecipeSerializer<SoakedBandageRecipe> SOAKED_BANDAGE = (RecipeSerializer)Registry.register(BuiltInRegistries.RECIPE_SERIALIZER,
            ResourceLocation.fromNamespaceAndPath(ImBleeding.MOD_ID,"crafting_special_soaked_bandage"), new SimpleCraftingRecipeSerializer(SoakedBandageRecipe::new));

    public static void registerModRecipeSerializers(){
    }

}
