package com.mojontwins.modloader;

import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.game.block.Block;
import net.minecraft.game.level.World;

public class mod_DesertTheme extends BaseMod {
	public static ModBlock blockCactus;
	public static ModBlock blockDeadBush;
	
	public static int desertThemeID;
	public static int entityHuskMobID;
	
	public mod_DesertTheme() {
	}

	@Override
	public void load() throws Exception {
		
		// Add some extra blocks
		
		blockCactus = new BlockCactus(ModLoader.getBlockId ()).setBlockHardness(0.4F).setName("block.cactus");
		blockCactus.blockIndexInTexture = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_cactus.png");
		((BlockCactus)blockCactus).bottomTextureIndex = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_cactus_bottom.png");
		((BlockCactus)blockCactus).topTextureIndex = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_cactus_top.png");
		ModLoader.registerBlock(blockCactus);
		
		blockDeadBush = new BlockDeadBush(ModLoader.getBlockId()).setBlockHardness(0.1F).setName("block.dead_bush");
		blockDeadBush.blockIndexInTexture = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_dead_bush.png");
		ModLoader.registerBlock(blockDeadBush);
		
		// Add the level theme
		
		desertThemeID = ModLoader.registerWorldTheme(new ThemeDesert("Desert"));
		
		// Add husks
		
		entityHuskMobID = ModLoader.getNewMobID();
		ModLoader.addEntityRenderer(EntityHusk.class, new RenderLiving(new ModelZombie (), 0.5F));
		// Note how husks are NOT registered as monsters as we don't want the engine to auto-select them.

	}
	
    public int spawnerSelectMonsterBasedOnPosition (int entityID, World world, int x, int y, int z) {
    	// If it's a Zombie and it's been placed on sand...
    	if (entityID == 3 && (world.getBlockId(x, y, z) == Block.sand.blockID || world.getBlockId(x, y - 1, z) == Block.sand.blockID)) {
        	// It's now a husk!
        	entityID = entityHuskMobID; 
        }
        return entityID;
    }

    public Object spawnMonster (int entityID, World world) {    	
    	if (entityID == entityHuskMobID) return new EntityHusk(world);
    	
        return null;
    }
}
