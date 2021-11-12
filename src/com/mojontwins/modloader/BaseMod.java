package com.mojontwins.modloader;

import net.minecraft.client.Minecraft;
import net.minecraft.game.level.World;
import net.minecraft.game.level.generator.LevelGenerator;

abstract class BaseMod {
	public abstract void load () throws Exception;
	
	public void modsLoaded () {	
	}
	
	public void hookGenerateStructures (LevelGenerator levelGenerator, World world) {
	}
	
	public void hookGameStart (Minecraft minecraft) {		
	}
	
	public boolean hookOnBlockHarvested (Minecraft minecraft, World world, int x, int y, int z, int blockID, int metadata) {
		return false;
	}
}
