package com.mojontwins.modloader;

import net.minecraft.client.physics.MovingObjectPosition;
import net.minecraft.game.block.Block;
import net.minecraft.game.entity.player.EntityPlayer;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.level.World;

public class ItemBottle extends ModItem {
	// What's inside the bottle?
	int contents; 
	
	public ItemBottle(int var1, int contents) {
		super(var1);
		this.contents = contents;
		this.maxStackSize = 1;
	}

    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
    	// First we detect if we hit water
        MovingObjectPosition movingobjectposition = getMovingObjectPositionFromPlayer(world, entityPlayer, true);

        if (movingobjectposition != null && movingobjectposition.typeOfHit == 0) {
            int x = movingobjectposition.blockX;
            int y = movingobjectposition.blockY;
            int z = movingobjectposition.blockZ;

            int blockID = world.getBlockId(x, y, z);

            if (this.contents == 0) {
	            if (blockID == Block.waterStill.blockID) {
	            	world.playSoundAtPlayer((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F, "random.splash", world.random.nextFloat() * 0.25F + 0.75F,  world.random.nextFloat() + 0.5F);
	            	
	        		// Substitute the hit block with air
	        		world.setBlockWithNotify(x, y, z, 0);
	        		
	        		// Replace this item with a filled bottle
	        		return new ItemStack (mod_PoisonLand.itemBottleWater);
	            } 

	            if (blockID == mod_PoisonLand.blockAcidStill.blockID ) {
	            	world.playSoundAtPlayer((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F, "random.splash", world.random.nextFloat() * 0.25F + 0.75F,  world.random.nextFloat() + 0.5F);
	            	
	            	// Substitute the hit block with air
	        		world.setBlockWithNotify(x, y, z, 0);
	        		
	        		// Replace this item with a filled bottle
	        		return new ItemStack (mod_PoisonLand.itemBottleAcid);	            	
	            }
	        }
        }
        
		world.playSoundAtEntity(entityPlayer, "random.bow", 0.5F, 0.4F / (Item.rand.nextFloat() * 0.4F + 0.8F));
		world.spawnEntityInWorld(new EntityThrowableBottle(world, entityPlayer, 0.5F + Item.rand.nextFloat() * 0.5F, itemStack.getItem()));
		itemStack.stackSize--;
	
        return itemStack;
    }	
}
