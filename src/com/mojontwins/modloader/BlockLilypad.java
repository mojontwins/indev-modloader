package com.mojontwins.modloader;

import java.util.Random;

import net.minecraft.client.physics.AxisAlignedBB;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.Material;
import net.minecraft.game.level.World;

public class BlockLilypad extends ModBlock {

	public BlockLilypad(int id) {
		super(id, Material.plants);
		this.setTickOnLoad(true);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.015625F, 1.0F);
	}

    public final boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return this.canThisPlantGrowOnThisBlockID(world.getBlockId(x, y - 1, z));
    }

    protected boolean canThisPlantGrowOnThisBlockID(int blockID) {
        return blockID == Block.waterStill.blockID;
    }
    
    // Adapted from Flower:
    public final void onNeighborBlockChange(World world, int x, int y, int z, int blockID) {
        super.onNeighborBlockChange(world, x, y, z, blockID);
        this.checkFlowerChange(world, x, y, z);
    }

    public void updateTick(World world, int x, int y, int z, Random rand) {
        this.checkFlowerChange(world, x, y, z);
    }

    private void checkFlowerChange(World world, int x, int y, int z) {
        if (!this.canBlockStay(world, x, y, z)) {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z));
            world.setBlockWithNotify(x, y, z, 0);
        }

    }

    public boolean canBlockStay(World world, int x, int y, int z) {
        return this.canThisPlantGrowOnThisBlockID(world.getBlockId(x, y - 1, z));
    }
    
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
        return new AxisAlignedBB ((float)par2 + minX, (float)par3 + minY, (float)par4 + minZ, (float)par2 + maxX, (float)par3 + maxY, (float)par4 + maxZ);
    }
    
    public final boolean isOpaqueCube() {
        return false;
    }

    public final boolean renderAsNormalBlock() {
        return false;
    }

    public int getRenderType() {
        return mod_Example.blockLilypadRenderID;
    }
}
