package com.mojontwins.modloader;

import com.mojontwins.util.TextureCoords;
import com.mojontwins.util.TupleFloat;
import com.mojontwins.util.TupleInt;

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
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.level.World;
import net.minecraft.game.level.generator.LevelGenerator;

public class mod_PoisonLand extends BaseMod {
	
	// New blocks used in this mod
	public static ModBlock blockPodzol;
	public static ModBlock blockBigMushroomGreen;
	public static ModBlock blockBigMushroomBrown;
	public static ModBlock blockSkullHead;
	
	// A new fluid!
	public static BlockAcidFlowing blockAcidFlowing;
	public static BlockStationary blockAcidStill;
	public static BlockFluidSource blockAcidSource;
	
	// Bottles
	public static ModItem itemBottleEmpty;
	public static ModItem itemBottleWater;
	public static ModItem itemBottlePoison;
	public static ModItem itemBottleAcid;
	
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
	
	// New? mobs:
	public static int entityPoisonSkeletonMobID;
	
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
		ModLoader.registerBlock(blockBigMushroomGreen);
		
		blockBigMushroomBrown = new BlockBigMushroom(ModLoader.getBlockId(), 0).setBlockHardness(0.25F).setName("block.big_mushroom_brown");
		blockBigMushroomBrown.stepSound = Block.soundWoodFootstep;
		blockBigMushroomBrown.blockIndexInTexture = blockBigMushroomGreen.blockIndexInTexture;
		((BlockBigMushroom) blockBigMushroomBrown).textureCap = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_mushroom_brown.png");
		((BlockBigMushroom) blockBigMushroomBrown).textureStem = ((BlockBigMushroom)blockBigMushroomGreen).textureStem;	
		ModLoader.registerBlock(blockBigMushroomBrown);
		
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
		
		itemBottlePoison = new ItemBottle(ModLoader.getItemId(), 9999).setName ("item.bottle_poison");
		itemBottlePoison.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_bottle_poison.png"));

		itemBottleAcid = new ItemBottle(ModLoader.getItemId(), blockAcidStill.blockID).setName ("item.bottle_acid");
		itemBottleAcid.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_bottle_acid.png"));
		
		// Associate a renderer to our EntityThrowableBottles:
		
		ModLoader.addEntityRenderer(EntityThrowableBottle.class, new RenderThrowableBottle());
		
		// Skull head blocks with a custom renderer
		
		blockSkullHead = new BlockMobHead(ModLoader.getBlockId(), Material.rock).setBlockHardness(1.5F).setBlockLightValue(1.0F).setName("block.skullhead");
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
		blockCauldronPoison.blockIndexInTexture = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_poison.png");
		ModLoader.registerBlock(blockCauldronPoison);

		// We'll use the same custom block renderer for all the cauldron instances
		blockCauldronRenderID = ModLoader.getUniqueBlockModelID(this, true);
		
		// New mobs:

		entityPoisonSkeletonMobID = ModLoader.getNewMobID();
		ModLoader.addEntityRenderer(EntityPoisonSkeleton.class, new RenderLiving(new ModelSkeleton (), 0.5F));
		
		// Add the level theme
		
		poisonLandThemeID = ModLoader.registerWorldTheme(new ThemePoisonLand("Poison Land"));
	}

	public void hookGameStart (Minecraft minecraft) {
		minecraft.thePlayer.inventory.setInventorySlotContents(4, new ItemStack(blockSkullHead, 1));
		minecraft.thePlayer.inventory.setInventorySlotContents(5, new ItemStack(itemBottleEmpty, 1));
		minecraft.thePlayer.inventory.setInventorySlotContents(6, new ItemStack(itemBottleEmpty, 1));
		minecraft.thePlayer.inventory.setInventorySlotContents(7, new ItemStack(itemBottlePoison, 1));
		minecraft.thePlayer.inventory.setInventorySlotContents(8, new ItemStack(Block.blockDiamond, 64));
	}

	public void renderInvBlock(RenderBlocks renderblocks, Block block, int renderType) {
		Tessellator tessellator = Tessellator.instance;

		if (renderType == blockSkullHeadRenderID) {
			tessellator.startDrawingQuads();
            Tessellator.setNormal(0.0F, -1.0F, 0.0F);
            tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
            this.renderBlockSkullHead(block, -0.5F, -0.5F, -0.5F, 3);
            tessellator.draw();
		} else if (renderType == blockCauldronRenderID) {
			tessellator.startDrawingQuads();
            Tessellator.setNormal(0.0F, -1.0F, 0.0F);
            RenderCauldron.renderBlock(0, -0.5F, -0.5F, -0.5F, ((BlockCauldron) block).tXZ, ((BlockCauldron) block).tNS, ((BlockCauldron) block).tW, ((BlockCauldron) block).tE, block.blockIndexInTexture);
            tessellator.draw();
		}
    }

	public boolean renderWorldBlock(RenderBlocks renderblocks, World world, int x, int y, int z, Block block, int renderType) {
		Tessellator tessellator = Tessellator.instance;
		
		if (renderType == blockSkullHeadRenderID) {
			float b = block.getBlockBrightness(world, x, y, z);
			tessellator.setColorOpaque_F(b, b, b);
	        
			return this.renderBlockSkullHead (block, (float)x, (float)y, (float)z, world.getBlockMetadata(x, y, z));
		}
		
		if (renderType == blockCauldronRenderID) {
			float b = block.getBlockBrightness(world, x, y, z);
			tessellator.setColorOpaque_F(b, b, b);
	        
			return RenderCauldron.renderBlock(world.getBlockMetadata(x, y, z), x, y, z, ((BlockCauldron) block).tXZ, ((BlockCauldron) block).tNS, ((BlockCauldron) block).tW, ((BlockCauldron) block).tE, block.blockIndexInTexture);    
		}
		
        return false;
    }	
	
	public boolean renderBlockSkullHead (Block block, float x, float y, float z, int meta) {
		float x1, y1, z1, x2, y2, z2;
		TupleFloat t;
		
        x1 = x + 0.25F;
        x2 = x1 + 0.5F;
        y1 = y;
        y2 = y + 0.5F;
        z1 = z + 0.25F;
        z2 = z1 + 0.5F;
        
		Tessellator tessellator = Tessellator.instance;
        int i = block.blockIndexInTexture;

        // These point to the 16x16 texture which is 4 8x8 blocks:
        // L  R
        // T  F
        
        int j = (i & 0xf) << 4;
        int k = i & 0xff0;
        
        // Draw top
        t = TextureCoords.subIndex2TextureCoords(2, j, k);
        
        tessellator.addVertexWithUV(x2, y2, z2, t.x2, t.y2);
        tessellator.addVertexWithUV(x2, y2, z1, t.x2, t.y1);
        tessellator.addVertexWithUV(x1, y2, z1, t.x1, t.y1);
        tessellator.addVertexWithUV(x1, y2, z2, t.x1, t.y2);
        
        // Orientation based upon meta
        TupleInt o = TextureCoords.orientationMeta2subIndexTuples (meta);
        
        // Draw front (north)
        t = TextureCoords.subIndex2TextureCoords(o.n, j, k);
        
        tessellator.addVertexWithUV(x1, y2, z1, t.x2, t.y1);
        tessellator.addVertexWithUV(x2, y2, z1, t.x1, t.y1);
        tessellator.addVertexWithUV(x2, y1, z1, t.x1, t.y2);
        tessellator.addVertexWithUV(x1, y1, z1, t.x2, t.y2);

        // Draw back (south)
        t = TextureCoords.subIndex2TextureCoords(o.s, j, k);
        
        tessellator.addVertexWithUV(x1, y2, z2, t.x1, t.y1);
        tessellator.addVertexWithUV(x1, y1, z2, t.x1, t.y2);
        tessellator.addVertexWithUV(x2, y1, z2, t.x2, t.y2);
        tessellator.addVertexWithUV(x2, y2, z2, t.x2, t.y1);
        
        // Draw left (west)
        t = TextureCoords.subIndex2TextureCoords(o.w, j, k);

        tessellator.addVertexWithUV(x1, y2, z1, t.x1, t.y1);
        tessellator.addVertexWithUV(x1, y1, z1, t.x1, t.y2);
        tessellator.addVertexWithUV(x1, y1, z2, t.x2, t.y2);
        tessellator.addVertexWithUV(x1, y2, z2, t.x2, t.y1);
        
        // Draw right (east)
        t = TextureCoords.subIndex2TextureCoords(o.e, j, k);
        
        tessellator.addVertexWithUV(x2, y2, z1, t.x2, t.y1);
        tessellator.addVertexWithUV(x2, y2, z2, t.x1, t.y1);
        tessellator.addVertexWithUV(x2, y1, z2, t.x1, t.y2);
        tessellator.addVertexWithUV(x2, y1, z1, t.x2, t.y2);
	
        return true;
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
		
		world.setBlockAndMetadataWithNotify(world.xSpawn - 2, world.ySpawn - 1, world.zSpawn - 2, blockCauldronAcid.blockID, 2);
		world.setBlockAndMetadataWithNotify(world.xSpawn, world.ySpawn - 1, world.zSpawn - 2, blockCauldronWater.blockID, 4);
		world.setBlockAndMetadataWithNotify(world.xSpawn + 2, world.ySpawn - 1, world.zSpawn - 2, blockCauldronPoison.blockID, 5);
		world.setBlockAndMetadataWithNotify(world.xSpawn, world.ySpawn - 1, world.zSpawn+ 1, blockCauldronEmpty.blockID, 3);

	}
}
