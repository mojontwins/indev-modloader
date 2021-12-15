package com.mojontwins.modloader;

import net.minecraft.game.block.Block;
import net.minecraft.game.level.World;
import net.minecraft.game.level.generator.LevelGenerator;

public class ThemeCaves extends ModLevelTheme {

	public ThemeCaves(String themeName) {
		super(themeName);
	}

    public int getSoilingBlockID (LevelGenerator levelGenerator, int y, int floorLevel, int fillLevel, int islandBottomLevel) {
		int blockID = 0;
		
		int yy = 64 - y;
		 
		if (yy < fillLevel) blockID = Block.dirt.blockID;
		if (yy > islandBottomLevel || yy < floorLevel) blockID = Block.stone.blockID;
		        
        return blockID;
    }
    
	public void setVisuals (LevelGenerator levelGenerator, World world) {
        world.skyColor = 0x0B0C33;
        world.fogColor = 0x3AB14E;
        world.cloudColor = 0x4D4FA0;
        world.skylightSubtracted = 15;
        world.skyBrightness = 15;
        world.defaultFluid = mod_PoisonLand.blockAcidFlowing.blockID;
        world.groundLevel = world.waterLevel - 2;
    }	    
}
