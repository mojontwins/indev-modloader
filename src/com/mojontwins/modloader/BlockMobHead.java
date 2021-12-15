package com.mojontwins.modloader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.physics.AxisAlignedBB;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.Material;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.item.Item;
import net.minecraft.game.level.World;
import util.MathHelper;

public class BlockMobHead extends ModBlock {

	public BlockMobHead(int id, Material material) {
		super(id, material);
		this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
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
        return mod_PoisonLand.blockSkullHeadRenderID;
    }
    
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, Entity par5EntityLiving) {
        int i = MathHelper.floor_double((double)((par5EntityLiving.rotationYaw * 4F) / 360F) + 0.5D) & 3;
        int meta = 0;
        
        if (i == 0) {
            meta = 2;
        } 

        if (i == 1) {
        	meta = 5;
        }

        if (i == 2) {
        	meta = 3;
        }

        if (i == 3) {
        	meta = 4;
        }
        
         par1World.setBlockMetadata(par2, par3, par4, meta);
    }
    
    public void onBlockPlaced(World world, int x, int y, int z, int side) {
    	if (this.blockID != mod_PoisonLand.blockSkullHead.blockID) return;
    	
    	// Detect if it's on top of two diamond blocks
    	// And there's 1 block surrounding to each direction
    	
    	for (int i = x - 1; i <= x + 1; i ++) {
    		for (int j = y - 2; j <= y; j ++) {
    			for (int k = z - 1; k <= z + 1; k ++)  {
    				int blockID = world.getBlockId(i, j, k);
    				if (j < y && i == x && k == z) {
    					if (blockID != Block.blockDiamond.blockID) return;
    				} else if (i != x || k != z) {
    					if (blockID != 0) return;
    				}    				
    			}
    		}
    	}
    	
    	// If we get to this point the condition is fulfilled.
    	
    	// Destroy the blocks
    	for (int j = y - 2; j <= y; j ++) {
    		Minecraft.effectRenderer.addBlockDestroyEffects(x, j, z);
    		world.setBlockWithNotify(x, j, z, 0);
    	}
    	
    	// Add the entity
    	Entity entityDiamondSkeleton = new EntityDiamondSkeleton (world);
    	entityDiamondSkeleton.setPositionAndRotation (x, y, z, 0.0F, 0.0F);
    	world.playSoundAtEntity(entityDiamondSkeleton, "random.explode", 0.5F, 1.0F);
		world.spawnEntityInWorld(entityDiamondSkeleton);
		
    }
}
