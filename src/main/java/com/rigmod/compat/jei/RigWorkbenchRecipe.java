package com.rigmod.compat.jei;

import net.minecraft.world.item.ItemStack;
import java.util.List;

public record RigWorkbenchRecipe(List<ItemStack> inputs, ItemStack output) {}