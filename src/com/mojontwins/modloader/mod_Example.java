package com.mojontwins.modloader;

import net.minecraft.client.Minecraft;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.Material;
import net.minecraft.game.entity.other.EntityItem;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemPickaxe;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.ItemSword;
import net.minecraft.game.level.World;
import net.minecraft.game.level.generator.LevelGenerator;

/*
 * This is a test mod which does stilly stuff for testing. Remove for release!
 */

public class mod_Example extends BaseMod {
	public static ModBlock blockStoneBricks;
	public static ModItem itemPebble;
	public static ItemSword itemSteelSword;
	public static ItemPickaxe itemSteelPickaxe;
	public static ModItem itemSteelIngot;
	public static ItemPickaxe itemSilkTouchGoldenPickaxe;
	
	public void load () throws Exception {
		blockStoneBricks = new BlockStoneBricks(ModLoader.getBlockId (), Material.rock).setBlockHardness(1.5F).setBlockResistance(1.5F).setName("block.stone_bricks");
		ModLoader.registerBlock(blockStoneBricks);
		blockStoneBricks.blockIndexInTexture = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_stone_bricks.png");
		
		itemPebble = new ModItem(ModLoader.getItemId()).setMaxStackSize(64).setName("item.pebble");
		itemPebble.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_pebble.png"));
		
		itemSteelSword = new ItemSteelSword(ModLoader.getItemId()).setName("item.steel_sword");
		itemSteelSword.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_steel_sword.png"));
		
		itemSteelPickaxe = new ItemSteelPickaxe(ModLoader.getItemId()).setName("item.steel_pickaxe");
		itemSteelPickaxe.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_steel_pickaxe.png"));
		
		itemSteelIngot = new ModItem(ModLoader.getItemId()).setName("item.steel_ingot");
		itemSteelIngot.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_steel_ingot.png"));
		
		itemSilkTouchGoldenPickaxe = new ItemSilkTouchGoldenPickaxe(ModLoader.getItemId()).setName("item.silk_touch_golden_pickaxe");
		itemSilkTouchGoldenPickaxe.setIconIndex(Item.pickaxeGold.getIconIndex());
		
		// Substitute the original golden pickaxe:
		Item.pickaxeGold = itemSilkTouchGoldenPickaxe;
		Item.itemsList[Item.pickaxeGold.shiftedIndex] = itemSilkTouchGoldenPickaxe;
		
		ModLoader.addRecipe(new ItemStack(blockStoneBricks, 4), new Object [] {
			"XX", "XX",
			'X', Block.stone
		});
		
		ModLoader.addRecipe(new ItemStack(Block.cobblestone, 1), new Object [] {
			"XXX", "XXX", "XXX",
			'X', itemPebble
		});
		
		ModLoader.addRecipe(new ItemStack(itemSteelSword,1), new Object [] {
			" # ", " # ", " X ",
			'#', itemSteelIngot,
			'X', Item.stick
		});

		ModLoader.addRecipe(new ItemStack(itemSteelPickaxe,1), new Object [] {
			"###", " X ", " X ",
			'#', itemSteelIngot,
			'X', Item.stick
		});
		
		ModLoader.addSmelting(Block.cobblestone.blockID, Block.stone.blockID);
		
		ModLoader.addSmelting(Item.ingotIron.shiftedIndex, itemSteelIngot.shiftedIndex);
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
		minecraft.thePlayer.inventory.setInventorySlotContents(5, new ItemStack(itemSteelSword, 1));
		minecraft.thePlayer.inventory.setInventorySlotContents(6, new ItemStack(itemSteelPickaxe, 1));
		minecraft.thePlayer.inventory.setInventorySlotContents(7, new ItemStack(Item.pickaxeGold, 1));
	}
	
	public boolean hookOnBlockHarvested (Minecraft minecraft, World world, int x, int y, int z, int blockID, int metadata) {
		ItemStack curItem = minecraft.thePlayer.inventory.getCurrentItem();
		if (curItem != null) {
			if (curItem.itemID == Item.pickaxeGold.shiftedIndex) {
				
				// This code is lifted from `Block.dropBlockAsItemWithChance`
				float px = world.random.nextFloat() * 0.7F + 0.15F;
                float py = world.random.nextFloat() * 0.7F + 0.15F;
                float pz = world.random.nextFloat() * 0.7F + 0.15F;
                EntityItem entityItem = new EntityItem(world, (float)x + px, (float)y + py, (float)z + pz, new ItemStack(blockID));
                entityItem.delayBeforeCanPickup = 10;
                world.spawnEntityInWorld(entityItem);
				
				return true;
			}
		}
		
		return false;
	}	
}
