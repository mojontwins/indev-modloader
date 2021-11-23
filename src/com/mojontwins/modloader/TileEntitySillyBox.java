package com.mojontwins.modloader;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.entity.other.EntityItem;
import net.minecraft.game.entity.player.EntityPlayer;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.level.World;

public class TileEntitySillyBox extends TileEntity {
	ItemStack contents = null;
	
	public TileEntitySillyBox() {
		// TODO Auto-generated constructor stub
	}

    public final void readFromNBT(NBTTagCompound var1) {
        super.readFromNBT(var1);
        this.contents = new ItemStack(var1);
    }

    public final void writeToNBT(NBTTagCompound var1) {
        super.writeToNBT(var1);
        contents.writeToNBT(var1);
    }
	
    public void updateEntity() {
    }
    
    public void putItem (EntityPlayer entityPlayer) {
    	World world = this.worldObj;
    	int x = this.xCoord, y = this.yCoord, z = this.zCoord;
    	
    	if (this.contents != null) {
        	// Give what's inside
    		
    		float px = world.random.nextFloat() * 0.7F + 0.15F;
	        float py = 1.0F;
	        float pz = world.random.nextFloat() * 0.7F + 0.15F;
	        EntityItem entityItem = new EntityItem(world, (float)x + px, (float)y + py, (float)z + pz, new ItemStack (this.contents.itemID, this.contents.stackSize, this.contents.itemDamage));
	        entityItem.delayBeforeCanPickup = 10;
	        world.spawnEntityInWorld(entityItem);
	        this.contents = null;

    	} else {
    	   	// Get new item
    	
    		ItemStack itemStack = entityPlayer.inventory.getCurrentItem();
    		if (itemStack != null) {
    			this.contents = new ItemStack (itemStack.itemID, itemStack.stackSize, itemStack.itemDamage);
    			entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
    		} else this.contents = null;

    	}
    	
    	// Now update the block in the world. As the new block is placed, a new TileEntity will be generated.
    	// We don't want this, so we preserve `this` and then reset it.
    	TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
    	
    	int blockID = contents == null ? mod_Example.blockSillyBoxEmpty.blockID : mod_Example.blockSillyBoxFull.blockID;
    	world.setBlockWithNotify(x, y, z, blockID);
    	world.setBlockTileEntity(x, y, z, tileEntity);
    }
    
    public void onTileEntityRemoved (World world, int x, int y, int z) {
    	if (world.getBlockId(x, y, z) == 0 && this.contents != null) {
			float px = world.random.nextFloat() * 0.7F + 0.15F;
	        float py = 1.0F;
	        float pz = world.random.nextFloat() * 0.7F + 0.15F;
	        EntityItem entityItem = new EntityItem(world, (float)x + px, (float)y + py, (float)z + pz, this.contents);
	        entityItem.delayBeforeCanPickup = 10;	
	        world.spawnEntityInWorld(entityItem);
    	}
	}
}
