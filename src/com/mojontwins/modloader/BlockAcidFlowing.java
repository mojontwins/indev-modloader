package com.mojontwins.modloader;

import net.minecraft.game.block.BlockFlowing;
import net.minecraft.game.block.Material;

public class BlockAcidFlowing extends BlockFlowing {

	public BlockAcidFlowing(int blockID, int stillBlockID) {
		super (blockID, stillBlockID, Material.water);
	}

    public int getRenderBlockPass() {
        return 1;
    }
    
    public int getBlockTextureFromSide(int var1) {
    	return blockIndexInTexture;
    }
    
    public int tickRate() {
    	// Faster than lava, slower than water
        return 10;
    }
}
