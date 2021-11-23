package com.mojontwins.modloader;

import net.minecraft.client.Minecraft;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.Material;
import net.minecraft.game.item.ItemStack;

public class mod_ClayStuff extends BaseMod {

	public static ModBlock blockClay;
	public static ModItem itemClayBall;
	public static ModItem itemBucketEmpty;
	public static ModItem itemBucketWater;
	
	@Override
	public void load() throws Exception {
		blockClay = new BlockClay(ModLoader.getBlockId(), Material.ground).setBlockHardness(0.5F).setName("block.clay");
		blockClay.blockIndexInTexture = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_clay.png");
		ModLoader.registerBlock(blockClay);
		
		itemClayBall = new ModItem(ModLoader.getItemId()).setMaxStackSize(64).setName("item.clay_ball");
		itemClayBall.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_clay_ball.png"));
		
		ModLoader.addRecipe(new ItemStack(blockClay), new Object [] {
			"XX", "XX",
			'X', itemClayBall
		});
		
		itemBucketEmpty = new ItemBucket(ModLoader.getItemId(), 0).setMaxStackSize(1).setName("item.bucket_empty");
		itemBucketEmpty.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_bucket_empty.png"));
		
		itemBucketWater = new ItemBucket(ModLoader.getItemId(), Block.waterStill.blockID).setMaxStackSize(1).setName("item.bucket_water");
		itemBucketWater.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_bucket_water.png"));
	}

	public void hookGameStart (Minecraft minecraft) {
		minecraft.thePlayer.inventory.setInventorySlotContents(4, new ItemStack(itemBucketEmpty, 1));
	}
}
