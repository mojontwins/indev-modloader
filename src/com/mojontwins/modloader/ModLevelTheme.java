package com.mojontwins.modloader;

import net.minecraft.game.level.World;
import net.minecraft.game.level.generator.LevelGenerator;

public class ModLevelTheme {
	public int waterLevelAdjust = 0;                // in blocks; no change
	public String themeName = "";

	/*
	 * Instantiate with the name which should appear in the "New Level" menu
	 */
	public ModLevelTheme(String themeName) {
		this.themeName = themeName;
	}
	
	/*
	 * Adjust the floorlevel just before it is converted to int and written to the height map
	 */
	public double adjustFloorLevel (LevelGenerator levelGenerator, double floorLevel) {
		return floorLevel;
	}
	
	/*
	 *  Adjust (integer) height map, which is 0-centered at this stage
	 */
    public void adjustHeightMap (LevelGenerator levelGenerator, int [] heightMap) {
    }

	/*
	 * Called each iteration to fill the empty block array with basic blocks
	 * Return -1 for the default generation.
	 */
    public int getSoilingBlockID (LevelGenerator levelGenerator, int y, int floorLevel, int fillLevel, int islandBottomLevel) {
        return -1;
    }
    
    /*
     * Normally beachLevel = levelGenerator.waterLevel -1. Leave unchanged or change it:
     */
    public int adjustBeachLevel (LevelGenerator levelGenerator, int beachLevel) {
    	return beachLevel;
    }
	
    /*
     * Called each iteration decide if sand is to be added to the world.
     * return shouldGrow unchanged for the default behaviour, which is:
     * noiseValue > -8.0D for islandGen, or
     * noiseValue > 8.0D  for other gens.
     */
	boolean shouldGrow (LevelGenerator levelGeneartor, double noiseValue, boolean shouldGrow) {
        return shouldGrow; 
    }
	
	/*
	 * Called each iteration to know which block to add while growing.
	 * Return -1 for the default generation which is sand (grass for hell theme).
	 */
	public int getGrowingBlockID (LevelGenerator levelGenerator) {
		return -1;
	}
	
	/*
	 * Use to select a custom BlockID for "water" (not much choice)
	 * Return -1 for the default generation which is Block.waterStill.BlockID;
	 */
	public int getWateringBlockID (LevelGenerator levelGenerator) {
		return -1;
	}
	
	/*
	 * Use this to modify any of these world values:
	 * `world.skyColor` - 0x99CCFF by default
	 * `world.fogColor` - 0xFFFFFF by default
	 * `world.cloudColor` - 0xFFFFFF by default
	 * `world.skylightSubtracted` Seems to be 15 by default. Skeletons & zombies only burn at day if it is > 7.
	 * `world.skyBrightness` 15 by default.
	 * `world.defaultFluid` - water or lava (for hell).
	 */
	public void setVisuals (LevelGenerator levelGenerator, World world) {
    }
	
	/*
	 * Do your planting and return true, 
	 * or return false to let the engine do its thing.
	 */
	public boolean overridePlanting (LevelGenerator levelGenerator, World world) {
    	return false;
    }	
}
