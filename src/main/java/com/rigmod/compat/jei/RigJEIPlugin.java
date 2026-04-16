package com.rigmod.compat.jei;

import com.rigmod.RigMod;
import com.rigmod.item.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class RigJEIPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(RigMod.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new RigWorkbenchCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<RigWorkbenchRecipe> recipes = new ArrayList<>();
        
        // --- HELMETS ---
        addRecipe(recipes, new ItemStack(ModItems.STANDARD_LEVEL_1_HELMET.get()), 
            new ItemStack(ModItems.TITANIUM_INGOT.get(), 5), new ItemStack(ModItems.BATTERY_LEVEL_1.get(), 2), new ItemStack(Items.ENDER_EYE, 1));
        addRecipe(recipes, new ItemStack(ModItems.ENGINEERING_LEVEL_2_HELMET.get()), 
            new ItemStack(ModItems.TITANIUM_INGOT.get(), 7), new ItemStack(ModItems.BATTERY_LEVEL_2.get(), 2), new ItemStack(Items.ENDER_EYE, 3));
        addRecipe(recipes, new ItemStack(ModItems.ENGINEERING_LEVEL_3_HELMET.get()), 
            new ItemStack(ModItems.TITANIUM_INGOT.get(), 10), new ItemStack(ModItems.BATTERY_LEVEL_3.get(), 1), new ItemStack(Items.BLAZE_ROD, 5), new ItemStack(Items.NETHERITE_INGOT, 3));
        
        // --- CHESTPLATES ---
        addRecipe(recipes, new ItemStack(ModItems.ENGINEERING_LEVEL_3_CHESTPLATE.get()), 
            new ItemStack(ModItems.ENGINEERING_LEVEL_2_CHESTPLATE.get(), 1), new ItemStack(ModItems.TITANIUM_INGOT.get(), 12), new ItemStack(ModItems.BATTERY_LEVEL_4.get(), 1), new ItemStack(Items.NETHERITE_INGOT, 4));
        addRecipe(recipes, new ItemStack(ModItems.ENGINEERING_LEVEL_2_CHESTPLATE.get()), 
            new ItemStack(ModItems.TITANIUM_INGOT.get(), 10), new ItemStack(Items.QUARTZ, 28), new ItemStack(ModItems.BATTERY_LEVEL_5.get(), 5), new ItemStack(Items.DIAMOND, 12), new ItemStack(Items.NETHERITE_INGOT, 3));
        addRecipe(recipes, new ItemStack(ModItems.STANDARD_LEVEL_1_CHEST_BRONZE.get()), 
            new ItemStack(ModItems.TITANIUM_INGOT.get(), 5), new ItemStack(Items.COPPER_INGOT, 7), new ItemStack(Items.REDSTONE, 5), new ItemStack(Items.QUARTZ, 5));
        addRecipe(recipes, new ItemStack(ModItems.STANDARD_LEVEL_1_CHEST_WHITE.get()), 
            new ItemStack(ModItems.TITANIUM_INGOT.get(), 5), new ItemStack(Items.QUARTZ, 12), new ItemStack(Items.REDSTONE, 7), new ItemStack(Items.DIAMOND, 3));
        
        // --- LEGGINGS ---
        addRecipe(recipes, new ItemStack(ModItems.ENGINEERING_LEVEL_3_LEGGINGS.get()), 
            new ItemStack(ModItems.ENGINEERING_LEVEL_2_LEGGINGS.get(), 1), new ItemStack(Items.NETHERITE_INGOT, 4), new ItemStack(ModItems.BATTERY_LEVEL_3.get(), 1), new ItemStack(Items.DIAMOND, 8));
        addRecipe(recipes, new ItemStack(ModItems.ENGINEERING_LEVEL_2_LEGGINGS.get()), 
            new ItemStack(ModItems.STANDARD_LEVEL_1_LEGGINGS.get(), 1), new ItemStack(Items.QUARTZ, 12), new ItemStack(ModItems.BATTERY_LEVEL_2.get(), 2), new ItemStack(Items.DIAMOND, 5), new ItemStack(Items.IRON_INGOT, 12));
        addRecipe(recipes, new ItemStack(ModItems.STANDARD_LEVEL_1_LEGGINGS.get()), 
            new ItemStack(ModItems.TITANIUM_INGOT.get(), 4), new ItemStack(Items.BLACK_DYE, 3), new ItemStack(Items.QUARTZ, 6));
        
        // --- BOOTS ---
        addRecipe(recipes, new ItemStack(ModItems.ENGINEERING_LEVEL_2_BOOTS.get()), 
            new ItemStack(ModItems.STANDARD_LEVEL_1_BOOTS.get(), 1), new ItemStack(ModItems.TITANIUM_INGOT.get(), 4), new ItemStack(ModItems.BATTERY_LEVEL_2.get(), 2), new ItemStack(Items.IRON_INGOT, 4));
        addRecipe(recipes, new ItemStack(ModItems.STANDARD_LEVEL_1_BOOTS.get()), 
            new ItemStack(Items.QUARTZ, 4), new ItemStack(Items.REDSTONE, 2));

        // --- BATTERIES ---
        addRecipe(recipes, new ItemStack(ModItems.BATTERY_LEVEL_1.get()), new ItemStack(ModItems.TITANIUM_INGOT.get(), 1), new ItemStack(Items.REDSTONE, 4));
        addRecipe(recipes, new ItemStack(ModItems.BATTERY_LEVEL_2.get()), new ItemStack(ModItems.TITANIUM_INGOT.get(), 2), new ItemStack(Items.REDSTONE, 8));
        addRecipe(recipes, new ItemStack(ModItems.BATTERY_LEVEL_3.get()), new ItemStack(ModItems.TITANIUM_INGOT.get(), 3), new ItemStack(Items.REDSTONE, 12));
        addRecipe(recipes, new ItemStack(ModItems.BATTERY_LEVEL_4.get()), new ItemStack(ModItems.TITANIUM_INGOT.get(), 4), new ItemStack(Items.REDSTONE, 16), new ItemStack(Items.QUARTZ, 1));
        addRecipe(recipes, new ItemStack(ModItems.BATTERY_LEVEL_5.get()), new ItemStack(ModItems.TITANIUM_INGOT.get(), 5), new ItemStack(Items.REDSTONE, 20), new ItemStack(Items.QUARTZ, 2));
        addRecipe(recipes, new ItemStack(ModItems.BATTERY_LEVEL_6.get()), new ItemStack(ModItems.TITANIUM_INGOT.get(), 6), new ItemStack(Items.REDSTONE, 24), new ItemStack(Items.QUARTZ, 3));
        addRecipe(recipes, new ItemStack(ModItems.BATTERY_LEVEL_7.get()), new ItemStack(ModItems.TITANIUM_INGOT.get(), 7), new ItemStack(Items.REDSTONE, 28), new ItemStack(Items.QUARTZ, 4));

        registration.addRecipes(RigWorkbenchCategory.TYPE, recipes);
    }

    private void addRecipe(List<RigWorkbenchRecipe> list, ItemStack output, ItemStack... inputs) {
        list.add(new RigWorkbenchRecipe(List.of(inputs), output));
    }
}