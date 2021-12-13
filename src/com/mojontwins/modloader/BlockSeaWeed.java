package com.mojontwins.modloader;

import java.util.Random;

import net.minecraft.client.physics.AxisAlignedBB;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.Material;
import net.minecraft.game.level.World;

public class BlockSeaWeed extends ModBlock {

	public BlockSeaWeed(int id) {
		super(id, Material.water);
		this.setTickOnLoad(true);
	}

    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return world.getBlockId(x, y, z) == Block.waterStill.blockID 
        		&& world.getBlockId(x, y + 1, z) == Block.waterStill.blockID 
        		&& canThisPlantGrowOnThisBlockID(world.getBlockId(x, y - 1, z));
    }
    
    protected boolean canThisPlantGrowOnThisBlockID(int par1) {
        return par1 == blockID || par1 == Block.dirt.blockID 
        		|| par1 == Block.sand.blockID
        		|| par1 == Block.stone.blockID;
    }    
    
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlockID) {
        if (!canBlockStay(world, x, y, z)) {
            dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z));
            world.setBlockWithNotify(x, y, z, Block.waterStill.blockID);
        }
    }
    
    public void updateTick(World world, int x, int y, int z, Random rand) {
    	if (rand.nextInt (4) == 0) {
	    	if (world.getBlockId(x, y + 1, z) == Block.waterStill.blockID && world.getBlockId(x, y + 2, z) == Block.waterStill.blockID ) {
	    		world.setBlockWithNotify(x, y + 1, z, blockID);
	    	}
    	}
    }
    
    public boolean canBlockStay(World world, int x, int y, int z) {
        return canThisPlantGrowOnThisBlockID(world.getBlockId(x, y - 1, z));
    }
    
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int i) {
        return null;
    }
    
    public boolean isOpaqueCube() {
        return false;
    }
    
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    public int getRenderType() {
        return 1;
    }
}
