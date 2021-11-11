package com.mojontwins.modloader;

import java.util.ArrayList;
import java.util.List;

public class ModFurnaceRecipes {
	public static List<ModSmeltingRecipe> recipes;
	
	static {
		recipes = new ArrayList<ModSmeltingRecipe> ();
	}
	
	public ModFurnaceRecipes() {
	}

	public static void addSmeltingRecipe (int input, int output) {
		recipes.add(new ModSmeltingRecipe(input, output));
	}
	
	public static int smeltItem (int input) {
		for (int i = 0; i < recipes.size (); i ++) {
			ModSmeltingRecipe recipe = (ModSmeltingRecipe) recipes.get(i);
			if (recipe.input == input) return recipe.output;
		}
		
		return -1;
	}
}
