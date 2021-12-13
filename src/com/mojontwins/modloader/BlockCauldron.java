package com.mojontwins.modloader;

import net.minecraft.client.physics.AxisAlignedBB;
import net.minecraft.game.block.Material;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.level.World;
import util.MathHelper;

public class BlockCauldron extends ModBlock {
	// Textures used in the custom block renderer.
    // remember: blockIndexInTexture represents the contents!
	int tXZ, tNS, tW, tE;

	public BlockCauldron(int id) {
		super(id, Material.iron);
	}
    
    public final boolean renderAsNormalBlock() {
        return false;
    }
    
    public int getRenderType() {
        return mod_PoisonLand.blockCauldronRenderID;
    }
    
    public final boolean isOpaqueCube() {
        return false;
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
        
        System.out.println ("Angle " + i + ", meta = " + meta);
        par1World.setBlockMetadata(par2, par3, par4, meta);
    }
}
