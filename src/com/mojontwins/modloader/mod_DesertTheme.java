package com.mojontwins.modloader;

public class mod_DesertTheme extends BaseMod {
	public static ModBlock blockCactus;
	public static ModBlock blockDeadBush;
	
	public static int desertThemeID;
	
	public mod_DesertTheme() {
	}

	@Override
	public void load() throws Exception {
		blockCactus = new BlockCactus(ModLoader.getBlockId ()).setBlockHardness(0.4F).setName("block.cactus");
		blockCactus.blockIndexInTexture = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_cactus.png");
		((BlockCactus)blockCactus).bottomTextureIndex = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_cactus_bottom.png");
		((BlockCactus)blockCactus).topTextureIndex = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_cactus_top.png");
		ModLoader.registerBlock(blockCactus);
		
		blockDeadBush = new BlockDeadBush(ModLoader.getBlockId()).setBlockHardness(0.4F).setName("block.dead_bush");
		blockDeadBush.blockIndexInTexture = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_dead_bush.png");
		ModLoader.registerBlock(blockDeadBush);
		
		desertThemeID = ModLoader.registerWorldTheme(new ThemeDesert("Desert"));
	}

}
