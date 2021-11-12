package com.mojontwins.modloader;

import net.minecraft.game.item.ItemSword;

public class ItemSteelSword extends ItemSword {
	public String name;
	
	public ItemSteelSword(int itemID) {
		super (itemID, 2);
		maxDamage = 384;
		weaponDamage = 12;
		maxStackSize = 1;
	}
	
    public ItemSteelSword setName(String name) {
    	this.name = name;
    	return this;
    }	
}
