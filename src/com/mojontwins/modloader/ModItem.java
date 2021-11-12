package com.mojontwins.modloader;

import net.minecraft.game.item.Item;

public class ModItem extends Item {
	public String name;

	public ModItem(int var1) {
		super(var1);
	}
	
	public ModItem setMaxStackSize(int var1) {
		maxStackSize = var1;
		return this;
	}
	
	public ModItem setMaxDamage(int var1) {
		maxDamage = var1;
		return this;
	}
    
    public ModItem setName(String name) {
    	this.name = name;
    	return this;
    }
}
