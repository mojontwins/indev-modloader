package com.mojontwins.modloader;

import net.minecraft.client.physics.MovingObjectPosition;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.Material;
import net.minecraft.game.entity.player.EntityPlayer;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.level.World;

public class ItemBucket extends ModItem {
	// What's inside the bucket?
	public int contents = 0;	
	
	public ItemBucket(int var1, int contents) {
		super(var1);
		this.contents = contents;
	}

    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
    	// First we detect if we hit water
        MovingObjectPosition movingobjectposition = getMovingObjectPositionFromPlayer(world, entityPlayer, true);

        if (movingobjectposition == null) {
            return itemStack;
        }

        if (movingobjectposition.typeOfHit == 0) {
            int x = movingobjectposition.blockX;
            int y = movingobjectposition.blockY;
            int z = movingobjectposition.blockZ;
            int sideHit = movingobjectposition.sideHit;

            if (world.getBlockMaterial(x, y, z) == Material.water && contents == 0) {
        		// This is an empty bucket hitting water
        		
        		// Substitute the hit block with air
        		world.setBlockWithNotify(x, y, z, 0);
        		
        		// Replace this item with a water bucket
        		return new ItemStack (mod_ClayStuff.itemBucketWater);
            } else if (Block.blocksList[world.getBlockId(x, y, z)].isOpaqueCube() && contents == Block.waterStill.blockID) {
        		// This bucket is full of water

            	// Abuse ItemBlock.onItemUse, which puts a block in the world
            	ItemStack itemStackWaterMoving = new ItemStack (Block.waterMoving);
            	(itemStackWaterMoving).getItem().onItemUse (itemStackWaterMoving, entityPlayer, world, x, y, z, sideHit);
            	
        		// Replace this item with an empty bucket
        		return new ItemStack (mod_ClayStuff.itemBucketEmpty);
            }
        }

        return itemStack;
    }	
}
