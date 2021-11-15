package com.mojontwins.modloader;

import com.mojontwins.modloader.entity.status.StatusEffect;

import net.minecraft.game.entity.player.EntityPlayer;
import net.minecraft.game.item.ItemFood;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.level.World;

public class ItemFoodRawChicken extends ItemFood {
	public String name;

	public ItemFoodRawChicken(int itemID, int healAmount) {
		super(itemID, healAmount);
	}

    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        itemStack.stackSize --;

        // Add status `poisoned` to player which lasts 50 ticks - that means a total of 5 hearts less
        entityPlayer.addStatusEffect(new StatusEffect(mod_Example.statusPoisoned.id, 50, 1));
        
        return itemStack;
    }
    
    public ItemFoodRawChicken setName (String name) {
    	this.name = name;
    	return this;
    }
}
