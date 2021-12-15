package com.mojontwins.modloader;

import net.minecraft.game.block.Block;
import net.minecraft.game.level.World;
import net.minecraft.game.level.generator.LevelGenerator;

public class ThemePoisonLand extends ModLevelTheme {
	public int waterLevelAdjust = 2; 
	
	public ThemePoisonLand(String themeName) {
		super(themeName);
	}

	public double adjustFloorLevel (LevelGenerator levelGenerator, double floorLevel) {
		return floorLevel > 8.0F ? floorLevel + 12 : (floorLevel < 0.0F ? floorLevel * 2 : floorLevel);
	}

    public int getSoilingBlockID (LevelGenerator levelGenerator, int y, int floorLevel, int fillLevel, int islandBottomLevel) {
		int blockID = 0;
		
		if (y == floorLevel) {
			blockID = mod_PoisonLand.blockPodzol.blockID;
		}
		
        if (y < floorLevel) {
            blockID = Block.dirt.blockID;
        }

        if (y <= fillLevel) {
            blockID = Block.stone.blockID;
        }

        if (levelGenerator.floatingGen && y < islandBottomLevel) {
            blockID = 0;
        }
        
        return blockID;
    }
    
	public int getWateringBlockID (LevelGenerator levelGenerator, boolean inland) {
		return inland && levelGenerator.rand.nextBoolean() ? Block.waterStill.blockID : mod_PoisonLand.blockAcidStill.blockID;
	}
	
	public void setVisuals (LevelGenerator levelGenerator, World world) {
        world.skyColor = 0x0B0C33;
        world.fogColor = 0x3AB14E;
        world.cloudColor = 0x4D4FA0;
        world.skylightSubtracted = 7;
        world.skyBrightness = 7;
        world.defaultFluid = mod_PoisonLand.blockAcidFlowing.blockID;
        world.groundLevel = world.waterLevel - 2;
    }	
	
	public boolean overridePlanting (LevelGenerator levelGenerator, World world) {
		levelGenerator.populateFlowersAndMushrooms(world, Block.mushroomBrown, 500);
		
		int totalBlocks = world.width * world.length * world.height;
		
		// Spawn big mushrooms
		int bigMushrooms = totalBlocks / 5000;
		for (int i = 0; i < bigMushrooms; i ++) {
			int x = levelGenerator.rand.nextInt(world.width);
            int z = levelGenerator.rand.nextInt(world.length);
            int y = world.getHighestGround(x, z) + 1;
            if (y < world.waterLevel + 16) {
            	(new WorldGenBigMushroom (levelGenerator.rand.nextInt(2))).generate(world, levelGenerator.rand, x,  y,  z);
            }
		}
		
		// Spawn trees on high mesas
        int trees = totalBlocks / 10000;

        for(int i = 0; i < trees; ++i) {
            int x0 = levelGenerator.rand.nextInt(world.width);
            int z0 = levelGenerator.rand.nextInt(world.length);
            int y0 = world.getHighestGround(x0, z0);

            int x = x0;
            int y = y0;
            int z = z0;

            if (y0 > world.waterLevel + 16) for(int j = 0; j < 5; ++j) {
                
                x += levelGenerator.rand.nextInt(12) - levelGenerator.rand.nextInt(12);
                z += levelGenerator.rand.nextInt(12) - levelGenerator.rand.nextInt(12);
                
               if (world.getBlockId(x, y, z) != 0) {
                	world.setBlock(x, y, z, Block.dirt.blockID);
                    world.growTrees(x, y + 1, z);
                }
            }
        }
		
		return true;
	}
}
