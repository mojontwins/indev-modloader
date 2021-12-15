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
        MovingObjectPosition movingobjectposition = getMovingObjectPositionFromPlayer(world, entityPlayer, true);

        if (movingobjectposition != null && movingobjectposition.typeOfHit == 0) {
            int x = movingobjectposition.blockX;
            int y = movingobjectposition.blockY;
            int z = movingobjectposition.blockZ;

            int blockID = world.getBlockId(x, y, z);
            int meta = world.getBlockMetadata(x, y, z);  
            
            // Let's check if we hit a cauldron
            if (blockID > 0 && Block.blocksList[blockID] != null && Block.blocksList[blockID] instanceof BlockCauldron) {
            	
            	// Empty cauldron, not empty bottle:
            	if (blockID == mod_PoisonLand.blockCauldronEmpty.blockID && this.contents != 0) {
            		// Fill cauldron
            		int newBlockID = blockID;
            		
            		if (this.contents == Block.waterStill.blockID) {
            			newBlockID = mod_PoisonLand.blockCauldronWater.blockID;
            		} else if (this.contents == mod_PoisonLand.blockPoison.blockID) {
            			newBlockID = mod_PoisonLand.blockCauldronPoison.blockID;
            		} else if (this.contents == mod_PoisonLand.blockAcidStill.blockID) {
            			newBlockID = mod_PoisonLand.blockCauldronAcid.blockID;
            		} else if (this.contents == mod_PoisonLand.blockSoup.blockID) {
            			newBlockID = mod_PoisonLand.blockCauldronSoup.blockID;
            		} else if (this.contents == mod_PoisonLand.blockGoo.blockID) {
            			newBlockID = mod_PoisonLand.blockCauldronGoo.blockID;
            		}
            		            		
            		world.setBlockAndMetadataWithNotify(x, y, z, newBlockID, meta);
            		
            		// Empty bottle
            		return new ItemStack (mod_PoisonLand.itemBottleEmpty);
            	}
            	
            	// Empty bottle, not empty cauldron:
            	if (blockID != mod_PoisonLand.blockCauldronEmpty.blockID && this.contents == 0) {
            		// Empty cauldron
            		int cauldronContents = Block.blocksList[blockID].blockIndexInTexture;
            		world.setBlockAndMetadataWithNotify(x, y, z, mod_PoisonLand.blockCauldronEmpty.blockID, meta);
            		
            		// Fill bottle
            		if (cauldronContents == Block.waterMoving.blockIndexInTexture) {
            			itemStack = new ItemStack (mod_PoisonLand.itemBottleWater);
            		} else if (cauldronContents == mod_PoisonLand.blockAcidFlowing.blockIndexInTexture) {
            			itemStack = new ItemStack (mod_PoisonLand.itemBottleAcid);
            		} else if (cauldronContents == mod_PoisonLand.blockPoison.blockIndexInTexture) {
            			itemStack = new ItemStack (mod_PoisonLand.itemBottlePoison);
            		} else if (cauldronContents == mod_PoisonLand.blockSoup.blockIndexInTexture) {
            			itemStack = new ItemStack (mod_PoisonLand.itemBottleSoup);
            		} else if (cauldronContents == mod_PoisonLand.blockGoo.blockIndexInTexture) {
            			itemStack = new ItemStack (mod_PoisonLand.itemBottleGoo);
            		} 
            		
            		return itemStack;
            	}
            	
            	// Case else:
            	return itemStack;
            }

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
        
        // Soup can be eaten
        if (this.contents == mod_PoisonLand.blockSoup.blockID) {
        	entityPlayer.removeStatusEffect(mod_Example.statusPoisoned.id);
        	entityPlayer.heal(4);
        	itemStack.stackSize--;
        	
            return itemStack;
        }
        
        // Otherwise, throw.
		world.playSoundAtEntity(entityPlayer, "random.bow", 0.5F, 0.4F / (Item.rand.nextFloat() * 0.4F + 0.8F));
		world.spawnEntityInWorld(new EntityThrowableBottle(world, entityPlayer, 0.5F + Item.rand.nextFloat() * 0.5F, itemStack.getItem()));
		itemStack.stackSize--;
	
        return itemStack;
    }	
}
