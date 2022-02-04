package com.mojontwins.modloader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelSkeleton;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockFluidSource;
import net.minecraft.game.block.BlockStationary;
import net.minecraft.game.block.Material;
import net.minecraft.game.entity.monster.EntitySkeleton;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.level.World;
import net.minecraft.game.level.generator.LevelGenerator;

public class mod_PoisonLand extends BaseMod {
	
	// New blocks used in this mod
	public static ModBlock blockPodzol;
	public static ModBlock blockBigMushroomGreen;
	public static ModBlock blockBigMushroomBrown;
	public static ModBlock blockSkullHead;
	public static ModBlock blockPoison;
	public static ModBlock blockSoup;
	public static ModBlock blockGoo;
	
	// A new fluid!
	public static BlockAcidFlowing blockAcidFlowing;
	public static BlockStationary blockAcidStill;
	public static BlockFluidSource blockAcidSource;
	
	// Bottles
	public static ModItem itemBottleEmpty;
	public static ModItem itemBottleWater;
	public static ModItem itemBottlePoison;
	public static ModItem itemBottleAcid;
	public static ModItem itemBottleSoup;
	public static ModItem itemBottleGoo;
	
	// New theme
	public static int poisonLandThemeID;
	
	// Custom block renderers
	public static int blockSkullHeadRenderID;
	public static int blockCauldronRenderID;
	
	// Brew your poison!
	public static ModBlock blockCauldronEmpty;
	public static ModBlock blockCauldronWater;
	public static ModBlock blockCauldronAcid;
	public static ModBlock blockCauldronPoison;
	public static ModBlock blockCauldronSoup;
	public static ModBlock blockCauldronGoo;
	
	// New? mobs:
	public static int entityPoisonSkeletonMobID;
	public static int entityDiamondSkeletonMobID;
	
	// And the prize
	public static ModItem itemTalisman;
	
	public mod_PoisonLand() {
	}

	@Override
	public void load() throws Exception {
		// New blocks 
		
		blockPodzol = new BlockPodzol(ModLoader.getBlockId()).setBlockHardness(0.5F).setName("block.podzol");
		blockPodzol.stepSound = Block.soundGrassFootstep;
		blockPodzol.blockIndexInTexture = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_podzol_side.png");
		((BlockPodzol) blockPodzol).topTextureIndex = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_podzol_top.png");
		((BlockPodzol) blockPodzol).bottomTextureIndex = Block.dirt.blockIndexInTexture;
		ModLoader.registerBlock(blockPodzol);
		
		blockBigMushroomGreen = new BlockBigMushroom(ModLoader.getBlockId(), 1).setBlockHardness(0.25F).setName("block.big_mushroom_green");
		blockBigMushroomGreen.setBlockLightValue(0.875F);
		blockBigMushroomGreen.stepSound = Block.soundWoodFootstep;
		blockBigMushroomGreen.blockIndexInTexture = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_mushroom_inside.png");
		((BlockBigMushroom) blockBigMushroomGreen).textureCap = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_mushroom_green.png");
		((BlockBigMushroom) blockBigMushroomGreen).textureStem = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_mushroom_trunk.png");
		ModLoader.registerBlock(blockBigMushroomGreen, ItemBigMushroom.class);
		
		blockBigMushroomBrown = new BlockBigMushroom(ModLoader.getBlockId(), 0).setBlockHardness(0.25F).setName("block.big_mushroom_brown");
		blockBigMushroomBrown.stepSound = Block.soundWoodFootstep;
		blockBigMushroomBrown.blockIndexInTexture = blockBigMushroomGreen.blockIndexInTexture;
		((BlockBigMushroom) blockBigMushroomBrown).textureCap = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_mushroom_brown.png");
		((BlockBigMushroom) blockBigMushroomBrown).textureStem = ((BlockBigMushroom)blockBigMushroomGreen).textureStem;	
		ModLoader.registerBlock(blockBigMushroomBrown, ItemBigMushroom.class);
		
		blockPoison = new ModBlock(ModLoader.getBlockId(), Material.water).setBlockHardness(0.2F).setName("block.poison");
		blockPoison.blockIndexInTexture = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_poison.png");
		ModLoader.registerBlock(blockPoison);
		
		blockSoup = new ModBlock(ModLoader.getBlockId(), Material.water).setBlockHardness(0.2F).setName("block.soup");
		blockSoup.blockIndexInTexture = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_soup.png");
		ModLoader.registerBlock(blockSoup);
		
		blockGoo = new ModBlock(ModLoader.getBlockId(), Material.water).setBlockHardness(0.2F).setName("block.goo");
		blockGoo.blockIndexInTexture = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_goo.png");
		ModLoader.registerBlock(blockGoo);
		
		// Blocks used for the new fluid
		
		int blockAcidFlowingID = ModLoader.getBlockId();
		int blockAcidStillID = ModLoader.getBlockId();
		
		blockAcidFlowing = (BlockAcidFlowing) new BlockAcidFlowing(blockAcidFlowingID, blockAcidStillID).setName("block.poison_flowing");
		blockAcidFlowing.blockIndexInTexture = ModLoader.addAnimation(EnumTextureAtlases.TERRAIN, "textures/block_acidwater.png", 1);
		ModLoader.registerBlock(blockAcidFlowing);
		
		blockAcidStill = (BlockStationary) new BlockStationary(blockAcidFlowingID, blockAcidStillID, Material.water).setName("block.poison_still");
		blockAcidStill.blockIndexInTexture = blockAcidFlowing.blockIndexInTexture;
		ModLoader.registerBlock(blockAcidStill);
		
		blockAcidSource = (BlockFluidSource) new BlockFluidSource(ModLoader.getBlockId(), blockAcidFlowingID).setName("block.poison_source");
		blockAcidSource.material = Material.water;
		blockAcidSource.blockIndexInTexture = blockAcidFlowing.blockIndexInTexture;
		ModLoader.registerBlock(blockAcidFlowing);
		
		// Register the new fluid
		
		ModLoader.registerFluid(blockAcidSource, blockAcidStill, blockAcidFlowing, 1, 0);
		
		// Bottle items
		
		itemBottleEmpty = new ItemBottle(ModLoader.getItemId(), 0).setName ("item.bottle_empty");
		itemBottleEmpty.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_bottle_empty.png"));
		
		itemBottleWater = new ItemBottle(ModLoader.getItemId(), Block.waterStill.blockID).setName ("item.bottle_water");
		itemBottleWater.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_bottle_water.png"));
		
		itemBottlePoison = new ItemBottle(ModLoader.getItemId(), blockPoison.blockID).setName ("item.bottle_poison");
		itemBottlePoison.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_bottle_poison.png"));

		itemBottleAcid = new ItemBottle(ModLoader.getItemId(), blockAcidStill.blockID).setName ("item.bottle_acid");
		itemBottleAcid.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_bottle_acid.png"));
		
		itemBottleSoup = new ItemBottle(ModLoader.getItemId(), blockSoup.blockID).setName ("item.bottle_soup");
		itemBottleSoup.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_bottle_soup.png"));

		itemBottleGoo = new ItemBottle(ModLoader.getItemId(), blockGoo.blockID).setName ("item.bottle_goo");
		itemBottleGoo.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_bottle_goo.png"));
		
		// And a way to craft them
		
		ModLoader.addRecipe(new ItemStack(itemBottleEmpty, 1), new Object [] {
			"#", "#",
			'#', Block.glass
		});
		
		// Associate a renderer to our EntityThrowableBottles:
		
		ModLoader.addEntityRenderer(EntityThrowableBottle.class, new RenderThrowableBottle());
		
		// Skull head blocks with a custom renderer
		
		blockSkullHead = new BlockMobHead(ModLoader.getBlockId(), Material.wood).setBlockHardness(1.0F).setBlockLightValue(1.0F).setName("block.skullhead");
		blockSkullHead.blockIndexInTexture = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_skeleton_head.png");
		ModLoader.registerBlock(blockSkullHead);
		blockSkullHeadRenderID = ModLoader.getUniqueBlockModelID(this, true);
		
		// Breed your poison with a custom tile renderer
		
		int cauldronTXZ = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_cauldron_xz.png");
		int cauldronTNS = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_cauldron_ns.png");
		int cauldronTW = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_cauldron_w.png");
		int cauldronTE = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_cauldron_e.png");
		
		blockCauldronEmpty = new BlockCauldron(ModLoader.getBlockId()).setBlockHardness(1.0F).setName("block.cauldron.empty");
		((BlockCauldron) blockCauldronEmpty).tXZ = cauldronTXZ;
		((BlockCauldron) blockCauldronEmpty).tNS = cauldronTNS;
		((BlockCauldron) blockCauldronEmpty).tW = cauldronTW;
		((BlockCauldron) blockCauldronEmpty).tE = cauldronTE;
		blockCauldronEmpty.blockIndexInTexture = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_empty.png");
		ModLoader.registerBlock(blockCauldronEmpty);

		blockCauldronWater = new BlockCauldron(ModLoader.getBlockId()).setBlockHardness(1.0F).setName("block.cauldron.water");
		((BlockCauldron) blockCauldronWater).tXZ = cauldronTXZ;
		((BlockCauldron) blockCauldronWater).tNS = cauldronTNS;
		((BlockCauldron) blockCauldronWater).tW = cauldronTW;
		((BlockCauldron) blockCauldronWater).tE = cauldronTE;
		blockCauldronWater.blockIndexInTexture = Block.waterMoving.blockIndexInTexture;
		ModLoader.registerBlock(blockCauldronWater);

		blockCauldronAcid = new BlockCauldron(ModLoader.getBlockId()).setBlockHardness(1.0F).setName("block.cauldron.acid");
		((BlockCauldron) blockCauldronAcid).tXZ = cauldronTXZ;
		((BlockCauldron) blockCauldronAcid).tNS = cauldronTNS;
		((BlockCauldron) blockCauldronAcid).tW = cauldronTW;
		((BlockCauldron) blockCauldronAcid).tE = cauldronTE;
		blockCauldronAcid.blockIndexInTexture = blockAcidFlowing.blockIndexInTexture;
		ModLoader.registerBlock(blockCauldronAcid);

		blockCauldronPoison = new BlockCauldron(ModLoader.getBlockId()).setBlockHardness(1.0F).setName("block.cauldron.poison");
		((BlockCauldron) blockCauldronPoison).tXZ = cauldronTXZ;
		((BlockCauldron) blockCauldronPoison).tNS = cauldronTNS;
		((BlockCauldron) blockCauldronPoison).tW = cauldronTW;
		((BlockCauldron) blockCauldronPoison).tE = cauldronTE;
		blockCauldronPoison.blockIndexInTexture = blockPoison.blockIndexInTexture;
		ModLoader.registerBlock(blockCauldronPoison);
		
		blockCauldronSoup = new BlockCauldron(ModLoader.getBlockId()).setBlockHardness(1.0F).setName("block.cauldron.soup");
		((BlockCauldron) blockCauldronSoup).tXZ = cauldronTXZ;
		((BlockCauldron) blockCauldronSoup).tNS = cauldronTNS;
		((BlockCauldron) blockCauldronSoup).tW = cauldronTW;
		((BlockCauldron) blockCauldronSoup).tE = cauldronTE;
		blockCauldronSoup.blockIndexInTexture = blockSoup.blockIndexInTexture;
		ModLoader.registerBlock(blockCauldronSoup);
		
		blockCauldronGoo = new BlockCauldron(ModLoader.getBlockId()).setBlockHardness(1.0F).setName("block.cauldron.goo");
		((BlockCauldron) blockCauldronGoo).tXZ = cauldronTXZ;
		((BlockCauldron) blockCauldronGoo).tNS = cauldronTNS;
		((BlockCauldron) blockCauldronGoo).tW = cauldronTW;
		((BlockCauldron) blockCauldronGoo).tE = cauldronTE;
		blockCauldronGoo.blockIndexInTexture = blockGoo.blockIndexInTexture;
		ModLoader.registerBlock(blockCauldronGoo);

		// We'll use the same custom block renderer for all the cauldron instances
		
		blockCauldronRenderID = ModLoader.getUniqueBlockModelID(this, true);
		
		// And a recipe for cauldrons
		
		ModLoader.addRecipe(new ItemStack(blockCauldronEmpty, 1), new Object [] {
			"# #", "# #", "###",
			'#', Item.ingotIron
		});
		
		// New mobs:

		entityPoisonSkeletonMobID = ModLoader.getNewMobID(EntityPoisonSkeleton.class);
		ModLoader.addEntityRenderer(EntityPoisonSkeleton.class, new RenderLiving(new ModelSkeleton (), 0.5F));
		
		entityDiamondSkeletonMobID = ModLoader.getNewMobID(EntityDiamondSkeleton.class);
		ModLoader.addEntityRenderer(EntityDiamondSkeleton.class, new RenderDiamondSkeleton(new ModelSkeleton (), 0.7F));
		
		// And the prize
		
		itemTalisman = new ModItem(ModLoader.getItemId()).setMaxStackSize(1).setName("item.poison_talisman");
		itemTalisman.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_talisman.png"));
		
		// Add the level theme
		
		poisonLandThemeID = ModLoader.registerWorldTheme(new ThemePoisonLand("Poison Land"));
	}

	public void renderInvBlock(RenderBlocks renderblocks, Block block, int renderType) {
		Tessellator tessellator = Tessellator.instance;

		if (renderType == blockSkullHeadRenderID) {
			tessellator.startDrawingQuads();
            RenderSkeletonSkull.renderBlock(null, block, 4, -0.5F, -0.5F, -0.5F, block.blockIndexInTexture);
            tessellator.draw();
		} else if (renderType == blockCauldronRenderID) {
			tessellator.startDrawingQuads();
            RenderCauldron.renderBlock(null, block, 4, -0.5F, -0.5F, -0.5F, ((BlockCauldron) block).tXZ, ((BlockCauldron) block).tNS, ((BlockCauldron) block).tW, ((BlockCauldron) block).tE, block.blockIndexInTexture);
            tessellator.draw();
		}
    }

	public boolean renderWorldBlock(RenderBlocks renderblocks, World world, int x, int y, int z, Block block, int renderType) {
		Tessellator tessellator = Tessellator.instance;
		int meta = world.getBlockMetadata(x, y, z);
		
		if (renderType == blockSkullHeadRenderID) {
			float b = block.getBlockBrightness(world, x, y, z);
			tessellator.setColorOpaque_F(b, b, b);
	        
			//return this.renderBlockSkullHead (block, (float)x, (float)y, (float)z, world.getBlockMetadata(x, y, z));
			return RenderSkeletonSkull.renderBlock(world, block, meta, x, y, z, block.blockIndexInTexture);
		}
		
		if (renderType == blockCauldronRenderID) {
			return RenderCauldron.renderBlock(world, block, meta, x, y, z, ((BlockCauldron) block).tXZ, ((BlockCauldron) block).tNS, ((BlockCauldron) block).tW, ((BlockCauldron) block).tE, block.blockIndexInTexture);    
		}
		
        return false;
    }	
	
	public void populateMobsHashMap (int levelType) {	
		// Add poison skeletons instead of skeletons
		if (levelType == poisonLandThemeID) {
			System.out.println ("Replacing skeletons with poison skeletons!");
			ModLoader.removeMonsterEntity(EntitySkeleton.class);
			ModLoader.registerMonsterEntity (entityPoisonSkeletonMobID, EntityPoisonSkeleton.class);
		}
	}
	
	public void hookGenerateStructures (LevelGenerator levelGenerator, World world) {
		/*
		world.setBlockAndMetadataWithNotify(world.xSpawn - 1, world.ySpawn - 1, world.zSpawn - 1, blockCauldronAcid.blockID, 2);
		world.setBlockAndMetadataWithNotify(world.xSpawn, world.ySpawn - 1, world.zSpawn - 2, blockCauldronWater.blockID, 4);
		world.setBlockAndMetadataWithNotify(world.xSpawn + 1, world.ySpawn - 1, world.zSpawn - 1, blockCauldronPoison.blockID, 5);
		world.setBlockAndMetadataWithNotify(world.xSpawn, world.ySpawn - 1, world.zSpawn+ 1, blockCauldronEmpty.blockID, 3);
		*/
	}
	
	public void hookGameStart (Minecraft minecraft) {
		/*
		minecraft.thePlayer.inventory.setInventorySlotContents(4, new ItemStack(blockSkullHead, 1));
		minecraft.thePlayer.inventory.setInventorySlotContents(5, new ItemStack(itemBottleEmpty, 1));
		minecraft.thePlayer.inventory.setInventorySlotContents(6, new ItemStack(itemBottleEmpty, 1));
		minecraft.thePlayer.inventory.setInventorySlotContents(7, new ItemStack(blockBigMushroomBrown, 64));
		minecraft.thePlayer.inventory.setInventorySlotContents(8, new ItemStack(Block.blockDiamond, 64));
		*/
	}

}
