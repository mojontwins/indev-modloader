package com.mojontwins.modloader;

import com.mojontwins.modloader.entity.status.Status;
import com.mojontwins.modloader.entity.status.StatusPoisoned;

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
	public static ModItemArmor itemSteelHelmet;
	public static ModItemArmor itemSteelChest;
	public static ModItemArmor itemSteelLeggins;
	public static ModItemArmor itemSteelBoots;
	public static Status statusPoisoned;
	public static ItemFoodRawChicken itemFoodRawChicken;
	public static ItemFoodCookedChicken itemFoodCookedChicken;
	
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
		
		int steelRenderType = ModLoader.addArmor("steel");
		
		itemSteelHelmet = new ModItemArmor(ModLoader.getItemId(), 4, 149, steelRenderType, 0);
		itemSteelHelmet.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_steel_helmet.png"));
		
		itemSteelChest = new ModItemArmor(ModLoader.getItemId(), 9, 216, steelRenderType, 1);
		itemSteelChest.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_steel_chest.png"));
		
		itemSteelLeggins = new ModItemArmor(ModLoader.getItemId(), 6, 202, steelRenderType, 2);
		itemSteelLeggins.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_steel_legs.png"));

		itemSteelBoots = new ModItemArmor(ModLoader.getItemId(), 3, 175, steelRenderType, 3);
		itemSteelBoots.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_steel_boots.png"));

		// Substitute the original golden pickaxe:
		Item.pickaxeGold = itemSilkTouchGoldenPickaxe;
		Item.itemsList[Item.pickaxeGold.shiftedIndex] = itemSilkTouchGoldenPickaxe;
		
		// New food
		itemFoodRawChicken = new ItemFoodRawChicken(ModLoader.getItemId(), 0);
		itemFoodRawChicken.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_chicken_raw.png"));
		
		itemFoodCookedChicken = new ItemFoodCookedChicken(ModLoader.getItemId(), 10);
		itemFoodCookedChicken.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_chicken_cooked.png"));
		
		ModLoader.addRecipe(new ItemStack(blockStoneBricks, 4), new Object [] {
			"XX", "XX",
			'X', Block.stone
		});
		
		ModLoader.addRecipe(new ItemStack(Block.cobblestone, 1), new Object [] {
			"XXX", "XXX", "XXX",
			'X', itemPebble
		});
		
		ModLoader.addRecipe(new ItemStack(itemSteelSword, 1), new Object [] {
			" # ", " # ", " X ",
			'#', itemSteelIngot,
			'X', Item.stick
		});

		ModLoader.addRecipe(new ItemStack(itemSteelPickaxe, 1), new Object [] {
			"###", " X ", " X ",
			'#', itemSteelIngot,
			'X', Item.stick
		});
		
		ModLoader.addRecipe(new ItemStack(itemSteelHelmet, 1), new Object [] {
			"###", "# #", "   ",
			'#', itemSteelIngot
		});
			
		ModLoader.addRecipe(new ItemStack(itemSteelChest, 1), new Object [] {
			"# #", "###", "###",
			'#', itemSteelIngot
		});
		
		ModLoader.addRecipe(new ItemStack(itemSteelLeggins, 1), new Object [] {
			"###", "# #", "# #",
			'#', itemSteelIngot
		});
			
		ModLoader.addRecipe(new ItemStack(itemSteelBoots, 1), new Object [] {
			"# #", "# #",
			'#', itemSteelIngot
		});
		
		ModLoader.addSmelting(Block.cobblestone.blockID, Block.stone.blockID);
		
		ModLoader.addSmelting(Item.ingotIron.shiftedIndex, itemSteelIngot.shiftedIndex);
		
		ModLoader.addSmelting(itemFoodRawChicken.shiftedIndex, itemFoodCookedChicken.shiftedIndex);
		
		statusPoisoned = new StatusPoisoned(Status.getNewStatusId(), true);
	}
	
	public void hookGenerateStructures (LevelGenerator levelGenerator, World world) {
		world.setBlockWithNotify(world.xSpawn, world.ySpawn + 1, world.zSpawn - 3, blockStoneBricks.blockID);
	}
	
	public void hookGameStart (Minecraft minecraft) {
		minecraft.thePlayer.inventory.setInventorySlotContents(0, new ItemStack(Block.stoneOvenIdle, 1));
		minecraft.thePlayer.inventory.setInventorySlotContents(1, new ItemStack(Block.workbench, 1));
		minecraft.thePlayer.inventory.setInventorySlotContents(2, new ItemStack(Item.coal, 64));
		minecraft.thePlayer.inventory.setInventorySlotContents(3, new ItemStack(itemFoodRawChicken, 10));
		minecraft.thePlayer.inventory.setInventorySlotContents(9, new ItemStack(Block.cobblestone, 64));
		minecraft.thePlayer.inventory.setInventorySlotContents(10, new ItemStack(itemPebble, 64));
		minecraft.thePlayer.inventory.setInventorySlotContents(11, new ItemStack(itemSteelSword, 1));
		minecraft.thePlayer.inventory.setInventorySlotContents(12, new ItemStack(itemSteelPickaxe, 1));
		minecraft.thePlayer.inventory.setInventorySlotContents(13, new ItemStack(Item.pickaxeGold, 1));
		minecraft.thePlayer.inventory.setInventorySlotContents(14, new ItemStack(itemSteelIngot, 64));
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
