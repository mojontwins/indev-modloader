package com.mojontwins.modloader;

import java.util.Random;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.Material;
import net.minecraft.game.level.World;

public class BlockPodzol extends ModBlock {
	public int bottomTextureIndex;
	public int topTextureIndex;
	
	public BlockPodzol(int id) {
		super(id, Material.ground);
		this.setTickOnLoad(true);
	}

	public int getBlockTextureFromSide (int var1) {
    	if (var1 == 0) return this.bottomTextureIndex;
    	if (var1 == 1) return this.topTextureIndex;
    	return this.blockIndexInTexture; 		
	}

	 public void updateTick(World world, int x, int y, int z, Random par5Random) {
        if (world.getBlockLightValue(x, y + 1, z) < 4 && Block.lightOpacity[world.getBlockId(x, y + 1, z)] > 2) {
            world.setBlockWithNotify(x, y, z, Block.dirt.blockID);
        } else if (world.getBlockLightValue(x, y + 1, z) >= 9) {
            for (int i = 0; i < 4; i++) {
                int xx = (x + par5Random.nextInt(3)) - 1;
                int yy = (y + par5Random.nextInt(5)) - 3;
                int zz = (z + par5Random.nextInt(3)) - 1;
                int belowBlockID = world.getBlockId(xx, yy + 1, zz);

                if (world.getBlockId(xx, yy, zz) == Block.dirt.blockID && world.getBlockLightValue(xx, yy + 1, zz) >= 4 && Block.lightOpacity[belowBlockID] <= 2) {
                    world.setBlockWithNotify(xx, yy, zz, blockID);
                }
            }
        }
    }

    public int idDropped(int par1, Random par2Random) {
        return Block.dirt.idDropped(0, par2Random);
    }
}
