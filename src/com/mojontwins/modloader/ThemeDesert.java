package com.mojontwins.modloader;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockFlower;
import net.minecraft.game.level.World;
import net.minecraft.game.level.generator.LevelGenerator;

public class ThemeDesert extends ModLevelTheme {

	public ThemeDesert(String themeName) {
		super(themeName);
	}
	
	/*
	 * In the "Soiling" stage, the heightmap and a couple of noise generators are used to fill
	 * the blocks array with block, one column at a time. This method is called for each "y"
	 * in each column to select which block ID to put to the block array. For our desert,
	 * we'll be filling with sand from the top `floorLevel` to `fillLevel` (note that `fillLevel`
	 * may go over `floorLevel` sometimes), and from `fillLevel` down with stone. If the
	 * level generator is `floatingGen`, the bottom (`islandBottomLevel` & below) is filled with zeroes.
	 */
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
	
	/*
	 * During the "Growing" stage the generator originally generated sand in some places
	 * We are generating dirt instead.
	 */
	public int getGrowingBlockID (LevelGenerator levelGenerator) {
		return Block.dirt.blockID;
	}
	
	/*
	 * Set yellowish sandy shades for the sky, fog and clouds. Set the general light
	 * quite bright.
	 */
	public void setVisuals (LevelGenerator levelGenerator, World world) {
        world.skyColor = 0xCEBFA1;
        world.fogColor = 0xE2E1A6;
        world.cloudColor = 0xFFFED4;
        world.skylightSubtracted = 15;
        world.skyBrightness = 16;
    }
	
	/*
	 * The main "planting" sections grows trees, flowers and mushrooms. We are
	 * overriding that and growing cacti and dead bushes. We are adding mushrooms
	 * as well, but below the water level, so they appear in caves underground.
	 */
	public boolean overridePlanting (LevelGenerator levelGenerator, World world) {
		int totalBlocks = world.width * world.length * world.height;
		
		// Spawn cacti
		int cacti = totalBlocks / 500;
		WorldGenCactus worldGenCactus = new WorldGenCactus ();
		for (int i = 0; i < cacti; i ++) {
			int x = levelGenerator.rand.nextInt(world.width);
            int y = levelGenerator.rand.nextInt(world.height);
            int z = levelGenerator.rand.nextInt(world.length);
			worldGenCactus.generate(world, levelGenerator.rand, x,  y,  z);
		}
		
		// Spawn dead bushes
		int deadBushes = totalBlocks / 50;
		for (int i = 0; i < deadBushes; i ++) {
			int x = levelGenerator.rand.nextInt(world.width);
            int y = levelGenerator.rand.nextInt(world.height);
            int z = levelGenerator.rand.nextInt(world.length);
            if (((BlockDeadBush)mod_DesertTheme.blockDeadBush).canBlockStay(world, x, y, z)) {
            	world.setBlock(x, y, z, mod_DesertTheme.blockDeadBush.blockID);
            }
		}
		
		// Grow shrooms underground
		int mushrooms = totalBlocks / 4000;

        for(int i = 0; i < mushrooms; ++i) {
			int x0 = levelGenerator.rand.nextInt(world.width);
            int y0 = levelGenerator.rand.nextInt(world.waterLevel > 0 ? world.waterLevel : 16);
            int z0 = levelGenerator.rand.nextInt(world.length);
            
            BlockFlower blockMushroom = levelGenerator.rand.nextBoolean() ? Block.mushroomBrown : Block.mushroomRed;

            for(int j = 0; j < 10; ++j) {
                int x = x0;
                int y = y0;
                int z = z0;

                for(int k = 0; k < 10; ++k) {
                    x += levelGenerator.rand.nextInt(4) - levelGenerator.rand.nextInt(4);
                    y += levelGenerator.rand.nextInt(2) - levelGenerator.rand.nextInt(2);
                    z += levelGenerator.rand.nextInt(4) - levelGenerator.rand.nextInt(4);
                    if (x >= 0 && y >= 0 && z > 0 && x < world.width && y < world.length && z < world.height && world.getBlockId(x, y, z) == 0 && blockMushroom.canBlockStay(world, x, y, z)) {
                    	world.setBlockWithNotify(x, y, z, blockMushroom.blockID);
                    }
                }
            }
        }
		
    	return true;
    }
}
