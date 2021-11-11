package com.mojontwins.modloader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.game.block.Block;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.recipe.CraftingRecipe;

public class ModCraftingManager {
	public static List<CraftingRecipe> recipes;
	
	static {
		recipes = new ArrayList<CraftingRecipe> ();
	}

	public ModCraftingManager() {
	}

    public static void addRecipe(ItemStack itemStack, Object obj []) {

        /*
         * "obj" parameters are a sequence that goes this way:
         * - up to three strings OR one string array
         * - then a sequence of char, [item|block]
         */

        int paramIndex = 0;

        /* 
         * First task is concatenate everything to a string and determine the size of the recipe
         */

        String recipeString = "";        
        int recipeWidth = 0;
        int recipeHeight = 0;
        
        if (obj[0] instanceof String[]) {
            // Recipe comes in an array of String

            ++paramIndex;

            String[] recipeArray = (String[])obj[0];
            recipeHeight = recipeArray.length;

            // Concatenate recipe:
            for(int i = 0; i < recipeHeight; ++i) {
                if (recipeWidth < recipeArray [i].length ()) recipeWidth = recipeArray [i].length ();
                recipeString = recipeString + recipeArray [i];
            }
        } else {
            // Recipe comes in separate String parameters

            while(obj[paramIndex] instanceof String) {
                String recipeRow = (String)obj[paramIndex++];
                ++recipeHeight;
                if (recipeWidth < recipeRow.length ()) recipeWidth = recipeRow.length();
                recipeString = recipeString + recipeRow;
            }
        }

        // Now read the list of ingredients

        HashMap<Character,Integer> ingredients = new HashMap<Character,Integer>();
        
        for(; paramIndex < obj.length; paramIndex += 2) {
            Character recipeChar = (Character)obj[paramIndex];
            int blockOrItemID = 0;
            if (obj[paramIndex + 1] instanceof Item) {
                blockOrItemID = ((Item)obj[paramIndex + 1]).shiftedIndex;
            } else if (obj[paramIndex + 1] instanceof Block) {
                blockOrItemID = ((Block)obj[paramIndex + 1]).blockID;
            }

            ingredients.put(recipeChar, blockOrItemID);
        }

        // And create an integer array ready to use

        int[] recipeIds = new int[recipeWidth * recipeHeight];

        for(int i = 0; i < recipeWidth * recipeHeight; ++i) {
            char c = recipeString.charAt(i);
            if (ingredients.containsKey(c)) {
                recipeIds[i] = (Integer)ingredients.get(c);
            } else {
                recipeIds[i] = -1;
            }
        }

        recipes.add(new CraftingRecipe(recipeWidth, recipeHeight, recipeIds, itemStack));
    }
    
    public static ItemStack findMatchingRecipe(int[] recipeIds) {
        for(int i = 0; i < recipes.size(); ++i) {
            CraftingRecipe var3 = (CraftingRecipe)recipes.get(i);
            if (var3.matchRecipe(recipeIds)) {
                return var3.createResult();
            }
        }

        return null;
    }
}
