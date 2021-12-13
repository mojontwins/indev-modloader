package com.mojontwins.modloader;

import java.util.Random;

import net.minecraft.game.block.Material;

public class BlockBigMushroom extends ModBlock {
	public int mushroomType;
	
	public int textureStem;
	public int textureCap;
	
	public BlockBigMushroom(int id, int type) {
		super(id, Material.wood);
		mushroomType = type;
	}

    public int getBlockTextureFromSideAndMetadata(int par1, int par2)
    {
    	// meta = 10: stem, par > 1: sides
        if (par2 == 10 && par1 > 1) {
            return textureStem;
        }

        // Bottom
        if (par2 >= 1 && par2 <= 9 && par1 == 1) {
            return textureCap;
        }

        // Side 2
        if (par2 >= 1 && par2 <= 3 && par1 == 2) {
            return textureCap;
        }

        // Side 3
        if (par2 >= 7 && par2 <= 9 && par1 == 3) {
            return textureCap;
        }

        // Side 4
        if ((par2 == 1 || par2 == 4 || par2 == 7) && par1 == 4) {
            return textureCap;
        }

        // Side 5
        if ((par2 == 3 || par2 == 6 || par2 == 9) && par1 == 5) {
            return textureCap;
        }

        // Whole
        if (par2 == 14) {
            return textureCap;
        }

        // All trunk
        if (par2 == 15) {
            return textureStem;
        }
        
        // Inside
        return blockIndexInTexture;
    }
    
    public int quantityDropped(Random par1Random) {
        int i = par1Random.nextInt(10) - 7;
        if (i < 0) i = 0;
        return i;
    }

    public int idDropped(int par1, Random par2Random, int par3) {
        return blockID;
    }
}
