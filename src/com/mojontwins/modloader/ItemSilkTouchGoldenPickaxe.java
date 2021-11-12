package com.mojontwins.modloader;

import net.minecraft.game.block.Block;
import net.minecraft.game.item.ItemPickaxe;

public class ItemSilkTouchGoldenPickaxe extends ItemPickaxe {
	public String name;

	public ItemSilkTouchGoldenPickaxe(int itemID) {		
		super(itemID, 1);
		
		// Make it faster than stone
		efficiencyOnProperMaterial = 9.0F;
		
		// Only can stack 1 per slot
		maxStackSize = 1;
	}

	// Override canHarvestBlock so we can harvest anything
	public boolean canHarvestBlock (Block var1) {
		return true;
	}
	
	// Override getStrVsBlock so it's always as efficient
	public float getStrVsBlock(Block var1) {
		return efficiencyOnProperMaterial;
	}
	
    public ItemSilkTouchGoldenPickaxe setName(String name) {
    	this.name = name;
    	return this;
    }	
}
