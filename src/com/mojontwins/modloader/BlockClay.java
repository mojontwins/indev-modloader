package com.mojontwins.modloader;

import java.util.Random;

import net.minecraft.game.block.Material;

public class BlockClay extends ModBlock {

	public BlockClay(int id, Material material) {
		super(id, material);
	}

    public int quantityDropped(Random var1) {
        return 4;
    }

    public int idDropped(int var1, Random var2) {
        return mod_ClayStuff.itemClayBall.shiftedIndex;
    }	
}
