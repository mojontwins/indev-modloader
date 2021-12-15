package com.mojontwins.modloader;

import net.minecraft.client.physics.MovingObjectPosition;
import net.minecraft.game.block.Block;
import net.minecraft.game.entity.player.EntityPlayer;
import net.minecraft.game.item.ItemBlock;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.level.World;

public class ItemBigMushroom extends ItemBlock {
	public int mushroomType = 0;
	
	public ItemBigMushroom(int var1) {
		super(var1);
		Block block = Block.blocksList[blockID];
		if (block instanceof BlockBigMushroom) {
			mushroomType = ((BlockBigMushroom) block).mushroomType;
		}
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
            	// Empty cauldron: do nothing
            	if (blockID == mod_PoisonLand.blockCauldronEmpty.blockID) {
            		return itemStack;
            	}
            	
            	int newBlockID = blockID; 
            	
            	// Cauldron is not empty.
            	int cauldronContents = Block.blocksList[blockID].blockIndexInTexture;
            	            	
            	if (cauldronContents == Block.waterMoving.blockIndexInTexture) {
            		// Water, + brown = soup; + green = acid
            		newBlockID = this.mushroomType == 0 ? mod_PoisonLand.blockCauldronSoup.blockID : mod_PoisonLand.blockCauldronAcid.blockID;
            	} else if (cauldronContents == mod_PoisonLand.blockAcidFlowing.blockIndexInTexture) {
            		// Acid, + brown = water; + green = 50 % chance goo / poison
            		newBlockID = this.mushroomType == 0 ? mod_PoisonLand.blockCauldronWater.blockID : (rand.nextBoolean() ? mod_PoisonLand.blockCauldronGoo.blockID : mod_PoisonLand.blockCauldronPoison.blockID);
            	} else {
            		// Poison + ? = goo, 
            		// Soup + ? = goo,
            		// Goo + ? = goo.
            		newBlockID = mod_PoisonLand.blockCauldronGoo.blockID;
            	}
            	
            	// Replace cauldron
            	world.setBlockAndMetadataWithNotify(x, y, z, newBlockID, meta);
            		
            	// Decrease stack
            	itemStack.stackSize --;
            }
        }

        return itemStack;
    }	
}
