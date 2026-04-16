package com.rigmod.compat.jei;

import com.rigmod.RigMod;
import com.rigmod.item.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class RigWorkbenchCategory implements IRecipeCategory<RigWorkbenchRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(RigMod.MODID, "rig_workbench");
    public static final RecipeType<RigWorkbenchRecipe> TYPE = RecipeType.create(RigMod.MODID, "rig_workbench", RigWorkbenchRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public RigWorkbenchCategory(IGuiHelper helper) {
        this.background = helper.createBlankDrawable(160, 40);
        // Uses the Level 3 Helmet as the tab icon. Change to your Workbench block item if you prefer!
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.ENGINEERING_LEVEL_3_HELMET.get())); 
    }

    @Override
    public RecipeType<RigWorkbenchRecipe> getRecipeType() { 
        return TYPE; 
    }

    @Override
    public Component getTitle() { 
        return Component.literal("Rig Workbench"); 
    }

    @Override
    public IDrawable getBackground() { 
        return background; 
    }

    @Override
    public IDrawable getIcon() { 
        return icon; 
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RigWorkbenchRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.OUTPUT, 135, 12).addItemStack(recipe.output());
        
        int startX = 5;
        for (int i = 0; i < recipe.inputs().size(); i++) {
            builder.addSlot(RecipeIngredientRole.INPUT, startX + (i * 22), 12).addItemStack(recipe.inputs().get(i));
        }
    }
}