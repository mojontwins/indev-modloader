package com.mojontwins.modloader;

import net.minecraft.game.block.Material;

public class mod_Caves extends BaseMod {
	
	// Some new blocks
	public ModBlock blockSeaLantern;

	// New theme
	public static int cavesThemeID;
	
	@Override
	public void load() throws Exception {
		blockSeaLantern = new ModBlock(ModLoader.getBlockId(), Material.glass).setBlockLightValue(1.0F).setBlockHardness(0.3F).setName("block.sea_lantern");
		blockSeaLantern.blockIndexInTexture = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_sea_lantern.png");
		ModLoader.registerBlock(blockSeaLantern);
		
		// Add the level theme
		
		cavesThemeID = ModLoader.registerWorldTheme(new ThemeCaves("Caves"));
	}

}
