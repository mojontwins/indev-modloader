package com.mojontwins.modloader;

import net.minecraft.game.block.Block;
import net.minecraft.game.level.World;
import net.minecraft.game.level.generator.LevelGenerator;

public class ThemeDesert extends ModLevelTheme {

	public ThemeDesert(String themeName) {
		super(themeName);
	}

	public int getSoilingBlockID (LevelGenerator levelGenerator, int y, int floorLevel, int fillLevel, int islandBottomLevel) {
		int blockID = 0;
        if (y <= floorLevel) {
            blockID = Block.sand.blockID;
        }

        if (y <= fillLevel) {
            blockID = Block.stone.blockID;
        }

        if (levelGenerator.floatingGen && y < islandBottomLevel) {
            blockID = 0;
        }
        
        return blockID;
    }
	
	public int getGrowingBlockID (LevelGenerator levelGenerator) {
		return Block.dirt.blockID;
	}
	
	public void setVisuals (LevelGenerator levelGenerator, World world) {
        world.skyColor = 0xCEBFA1;
        world.fogColor = 0xE2E1A6;
        world.cloudColor = 0xFFFED4;
        world.skylightSubtracted = 15;
        world.skyBrightness = 16;
    }
	
	public boolean overridePlanting (LevelGenerator levelGenerator, World world) {
		// Spawn cacti
		int cacti = world.width * world.length * world.height / 2000;
		WorldGenCactus worldGenCactus = new WorldGenCactus ();
		for (int i = 0; i < cacti; i ++) {
			int x = levelGenerator.rand.nextInt(world.width);
            int y = levelGenerator.rand.nextInt(world.height);
            int z = levelGenerator.rand.nextInt(world.length);
			worldGenCactus.generate(world, levelGenerator.rand, x,  y,  z);
		}
		
		// Spawn dead bushes
		int deadBushes = world.width * world.length * world.height / 500;
		for (int i = 0; i < deadBushes; i ++) {
			int x = levelGenerator.rand.nextInt(world.width);
            int y = levelGenerator.rand.nextInt(world.height);
            int z = levelGenerator.rand.nextInt(world.length);
            if (((BlockDeadBush)mod_DesertTheme.blockDeadBush).canBlockStay(world, x, y, z)) {
            	world.setBlock(x, y, z, mod_DesertTheme.blockDeadBush.blockID);
            }
		}
		
    	return true;
    }
}
