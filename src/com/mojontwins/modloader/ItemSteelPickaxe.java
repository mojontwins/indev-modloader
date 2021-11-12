package com.mojontwins.modloader;

import net.minecraft.game.item.ItemPickaxe;

public class ItemSteelPickaxe extends ItemPickaxe {
	public String name;
	
	public ItemSteelPickaxe(int itemID) {
		super (itemID, 2);
		maxDamage = 384;
		efficiencyOnProperMaterial = 9.0F;
		maxStackSize = 1;
	}
	
    public ItemSteelPickaxe setName(String name) {
    	this.name = name;
    	return this;
    }	
}
