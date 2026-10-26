package net.fryc.imbleeding.recipes;

import net.fryc.imbleeding.items.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class SoakedBandageRecipe extends CustomRecipe {

    private static final int BANDAGES_COUNT = 2;

    public SoakedBandageRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput recipeInput, Level world) {
        int bandagesPresent = 0;
        int potionsPresent = 0;
        for(ItemStack stack : recipeInput.items()){
            if(stack.is(ModItems.BANDAGE)){
                bandagesPresent++;
            }
            else if(stack.is(Items.LINGERING_POTION)){
                potionsPresent++;
            }
            else if(!stack.isEmpty()){
                return false;
            }
        }
        return bandagesPresent == BANDAGES_COUNT && potionsPresent == 1;
    }

    @Override
    public ItemStack assemble(CraftingInput recipeInput, HolderLookup.Provider wrapperLookup) {
        ItemStack potion = ItemStack.EMPTY;
        for(ItemStack stack : recipeInput.items()){
            if(stack.is(Items.LINGERING_POTION)){
                potion = stack;
            }
        }
        if(potion.isEmpty()) return potion;

        ItemStack bandages = new ItemStack(ModItems.SOAKED_BANDAGE, BANDAGES_COUNT);
        bandages.set(DataComponents.POTION_CONTENTS, (PotionContents)potion.get(DataComponents.POTION_CONTENTS));

        return bandages;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 2 && height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.SOAKED_BANDAGE;
    }
}
