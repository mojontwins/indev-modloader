package com.mojontwins.modloader;

import net.minecraft.game.block.Material;
import net.minecraft.game.level.World;
import net.minecraft.game.level.generator.LevelGenerator;

public class mod_Example extends BaseMod {
	ModBlock blockStoneBricks;
	
	public void load () throws Exception {
		blockStoneBricks = new BlockStoneBricks(ModLoader.getBlockId (), Material.rock).setBlockHardness(1.5F).setBlockResistance(1.5F);
		ModLoader.registerBlock(blockStoneBricks);
		blockStoneBricks.blockIndexInTexture = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_stone_bricks.png");
	}
	
	public void generateStructures (LevelGenerator levelGenerator, World world) {
		world.setBlockWithNotify(world.xSpawn, world.ySpawn + 1, world.zSpawn - 3, blockStoneBricks.blockID);
	}
}
