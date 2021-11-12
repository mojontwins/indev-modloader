package com.mojontwins.modloader;

import net.minecraft.client.Minecraft;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.Material;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.level.World;
import net.minecraft.game.level.generator.LevelGenerator;

public class mod_Example extends BaseMod {
	public static ModBlock blockStoneBricks;
	public static ModItem itemPebble;
	
	public void load () throws Exception {
		blockStoneBricks = new BlockStoneBricks(ModLoader.getBlockId (), Material.rock).setBlockHardness(1.5F).setBlockResistance(1.5F).setName("block.stone_bricks");
		ModLoader.registerBlock(blockStoneBricks);
		blockStoneBricks.blockIndexInTexture = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_stone_bricks.png");
		
		itemPebble = new ModItem(ModLoader.getItemId()).setMaxStackSize(1);
		itemPebble.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_pebble.png"));
		
		ModLoader.addRecipe(new ItemStack(blockStoneBricks, 4), new Object [] {
			"XX", "XX",
			'X', Block.stone
		});
		
		ModLoader.addRecipe(new ItemStack(Block.cobblestone, 1), new Object [] {
			"XXX", "XXX", "XXX",
			'X', itemPebble
		});
		
		ModLoader.addSmelting(Block.cobblestone.blockID, Block.stone.blockID);
	}
	
	public void hookGenerateStructures (LevelGenerator levelGenerator, World world) {
		world.setBlockWithNotify(world.xSpawn, world.ySpawn + 1, world.zSpawn - 3, blockStoneBricks.blockID);
	}
	
	public void hookGameStart (Minecraft minecraft) {
		minecraft.thePlayer.inventory.setInventorySlotContents(0, new ItemStack(Block.stoneOvenIdle, 1));
		minecraft.thePlayer.inventory.setInventorySlotContents(1, new ItemStack(Block.workbench, 1));
		minecraft.thePlayer.inventory.setInventorySlotContents(2, new ItemStack(Item.coal, 64));
		minecraft.thePlayer.inventory.setInventorySlotContents(3, new ItemStack(Block.cobblestone, 64));
		minecraft.thePlayer.inventory.setInventorySlotContents(4, new ItemStack(itemPebble, 64));
	}
}
