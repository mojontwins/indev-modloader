package com.mojontwins.modloader;

import net.minecraft.game.level.World;
import net.minecraft.game.level.generator.LevelGenerator;

abstract class BaseMod {
	public abstract void load () throws Exception;
	
	public void modsLoaded () {	
	}
	
	public void generateStructures (LevelGenerator levelGenerator, World world) {
	}
}
