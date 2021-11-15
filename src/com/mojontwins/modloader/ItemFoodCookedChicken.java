package com.mojontwins.modloader;

import net.minecraft.game.entity.player.EntityPlayer;
import net.minecraft.game.item.ItemFood;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.level.World;

public class ItemFoodCookedChicken extends ItemFood {
	public String name;

	public ItemFoodCookedChicken(int itemID, int healAmount) {
		super(itemID, healAmount);
	}

    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
    	// Remove poisoned status
    	entityPlayer.removeStatusEffect(mod_Example.statusPoisoned.id);
    	
    	return super.onItemRightClick(itemStack, world, entityPlayer);
    }
	
    public ItemFoodCookedChicken setName (String name) {
    	this.name = name;
    	return this;
    }
}
