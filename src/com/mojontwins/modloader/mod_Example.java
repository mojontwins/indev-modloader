package com.mojontwins.modloader;

import net.minecraft.game.block.Material;

public class mod_Example extends BaseMod {
	ModBlock blockStoneBricks;
	
	public void load () throws Exception {
		blockStoneBricks = new BlockStoneBricks(ModLoader.getBlockId (), Material.rock).setBlockHardness(1.5F).setBlockResistance(1.5F);
		ModLoader.registerBlock(blockStoneBricks);
		blockStoneBricks.blockIndexInTexture = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "/stone_bricks.png");
	}
}
