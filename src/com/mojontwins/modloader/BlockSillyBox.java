package com.mojontwins.modloader;

import java.util.Random;

import net.minecraft.game.block.BlockContainer;
import net.minecraft.game.block.Material;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.entity.player.EntityPlayer;
import net.minecraft.game.level.World;

public class BlockSillyBox extends BlockContainer {
	public int topTextureIndex;
	public String name;
	
	public BlockSillyBox(int id) {
		super(id, Material.rock);
		this.setHardness(1.5F);
		this.setResistance(20.0F);
	}

	public final int getBlockTextureFromSide(int side) {
		return side == 1 ? topTextureIndex : blockIndexInTexture;
	}
	
	public int idDropped(int var1, Random var2) {
		// No matter what, drop the box EMPTY
		return mod_Example.blockSillyBoxEmpty.blockID;
    }
    
    public final boolean blockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer) {
    	TileEntitySillyBox tileEntity = (TileEntitySillyBox)world.getBlockTileEntity(x, y, z);
    	tileEntity.putItem(entityPlayer);
    	return true;
    }
    
    protected final TileEntity getBlockEntity() {
        return new TileEntitySillyBox();
    }
    
    public BlockSillyBox setName(String name) {
    	this.name = name;
    	return this;
    }
}
