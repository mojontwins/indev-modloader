# Basic stuff

## New blocks

## New Items

# Playing around

## Substituting an existing standard item

# Status effects

I've added status for `EntityLiving` objects and status effects. They work almost the same way as 1.0.0+ potions - but without potions. You can create as many statuses as you need, and inflict them to any `EntityLiving` instance in your game - most notably the `EntityPlayerSP`. 

To create a new status you have to create a new class which extends the `Status` class and then create an instance of such object. For example, let's create a status to represent poison, which will make the affected entity lose health over time:

```java
	package com.mojontwins.modloader.entity.status;

	import net.minecraft.game.entity.Entity;
	import net.minecraft.game.entity.EntityLiving;
	import net.minecraft.game.entity.monster.EntityZombie;

	public class StatusPoisoned extends Status {

		/*
		 * Constructor. needs a Status ID (which we'll be getting from a sequencer)
		 * and a isBadEffect bookean (not used for the moment)
		 */
		public StatusPoisoned(int id, boolean isBadEffect) {
			super(id, true);
		}

		/*
		 * So we can give this object a name, for the sequencer
		 */
	    public StatusPoisoned setName(String name) {
	    	this.name = name;
	    	return this;
	    }
		
		/*
		 * This method actually performs the effect this status represents
		 */
	    public void performEffect (EntityLiving entityLiving, int amplifier) {
	    	// Decrease half a heart
	    	if (entityLiving.health > 1) {
	    		entityLiving.attackEntityFrom((Entity)null, 1);
	    	}
	    }
		
		/*
		 * the effect will only be performed if this returns true
		 */	    
	    public boolean isReady (int tick, int amplifier) {
	    	// Run every 5 ticks
	    	return (tick % 5) == 0;
	    }

		/*
		 * This status will only applied if this method returns true
		 */
	    public boolean isApplicableTo (EntityLiving entityLiving) {
	    	// Zombies can't be poisoned
	    	return !(entityLiving instanceof EntityZombie);
	    }
	}
```

Once the new class has been added you need to create an object of that class in your `mod_XXX` (for example, `mod_Example`):

```java
	statusPoisoned = new StatusPoisoned(Status.getNewStatusId(), true).setName("status.poisoned");
```

By default, active statuses will spawn white particles. If you want the particles to be of a different colour, add

```java
	statusPoisoned.particleColor = 0x70B433;
```

or, if you don't want particles at all, add

```java
	statusPoisoned.showParticles = false;
```

Status are applied to `EntityLiving`s via `StatusEffect`s. For instance, we've created a new `ItemFoodRawChicken` which will inflict `StatusPoisoned` upon being eaten:

```java
	package com.mojontwins.modloader;

	import com.mojontwins.modloader.entity.status.StatusEffect;

	import net.minecraft.game.entity.player.EntityPlayer;
	import net.minecraft.game.item.ItemFood;
	import net.minecraft.game.item.ItemStack;
	import net.minecraft.game.level.World;

	public class ItemFoodRawChicken extends ItemFood {
		public String name;

		public ItemFoodRawChicken(int itemID, int healAmount) {
			super(itemID, healAmount);
		}

	    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
	        itemStack.stackSize --;

	        // Add status `poisoned` to player which lasts 50 ticks - that means a total of 5 hearts less
	        entityPlayer.addStatusEffect(new StatusEffect(mod_Example.statusPoisoned.id, 50, 1));
	        
	        return itemStack;
	    }
	    
	    public ItemFoodRawChicken setName (String name) {
	    	this.name = name;
	    	return this;
	    }
	}
```

This class overrides `ItemFood`'s default `onItemRightClick` with its own, which reduces the `ItemStack` size and inflicts the `StatusPoisoned` status. Note that it uses your instance in `mod_Example`.

There are other kinds of statuses which you only need to just wear out over time and which you can use alongside the defined code hooks to modify how the player or other `EntityLiving` behaves. For example, we can add a status which makes the player move faster for 100 ticks. As this doesn't need to override `Status`' methods, you can use the `Status` class directly in your `mod_XXXX`:

```java
	statusHyperSpeed = new Status (Status.getNewStatusId, false).setName("status.hyperSpeed");
	statusHyperSpeed.particleColor = 0x0000FF;
```

And activate the status effect when eating, for example, a Blue Pill

```java
	package com.mojontwins.modloader;

	import com.mojontwins.modloader.entity.status.StatusEffect;

	import net.minecraft.game.entity.player.EntityPlayer;
	import net.minecraft.game.item.ItemFood;
	import net.minecraft.game.item.ItemStack;
	import net.minecraft.game.level.World;

	public class ItemFoodBluePill extends ItemFood {
		public String name;

		public ItemFoodBluePill(int itemID, int healAmount) {
			super(itemID, healAmount);
		}

	    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
	        itemStack.stackSize --;

	        // Add status `hyperSpeed` to player which lasts 100 ticks
	        entityPlayer.addStatusEffect(new StatusEffect(mod_Example.statusHyperSpeed.id, 100, 1));
	        
	        return itemStack;
	    }
	    
	    public ItemFoodBluePill setName (String name) {
	    	this.name = name;
	    	return this;
	    }
	}
```

Then make the status work by implementing the hook `hookEntitySpeedModifier` in your mod:

```java
	public float hookEntitySpeedModifier (EntityLiving entityLiving) {
		if (entityLiving.isStatusActive (statusHyperSpeed)) {
			return 2.0F;
		}

		return 1.0F;
	}
```

# Hooks

I've added a number of hooks - modifications to the base classes to call methods from `BaseMod` (which you can override in your mod) to modify several game variables or add stuff. These are the hooks you can use from you mod.

If you are thinking on a mod and need to do a base class edit just drop me an issue and I'll add a hook for you so you don't have to play with the base classes. This will help me making Indev Modloader more powerful.

## `hookGenerateStructures`

This hook is called during level generation, in `LevelGenerator.generate`, right after the player has been spawn in the world and the basic house has been created around it. You can use this to add stuff to the level, which at this point is almost complete.

```java
	/* 
	 * Called while generating the world.
	 * runs after the player spawn point has been calculated and the house has been generated.
	 */
	public void hookGenerateStructures (LevelGenerator levelGenerator, World world) {
	}
```

## `hookGameStart`

This hook is called right before a new game is about to start: the level has been generated and everything is in place. I've used this in the example to place stuff in the player's inventory so I can test things quickly:

```java
	/*
	 * Called right before the game starts
	 */
	public void hookGameStart (Minecraft minecraft) {		
	}
```

To add stuff to the player inventory you can use this method call:

```java
	minecraft.thePlayer.inventory.setInventorySlotContents(0, new ItemStack(itemOrBlock, quantity));
```

Examples:

```java
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
```

## `hookOnBlockHarvested`

This hook is called whenever the player harvests a block (for example, when you used the pickaxe on a cobblestone block and it has just broken) and is used to override the default action which is a call to the object's `dropBlockAsItem` method. To override the default action do your thing and then return `true`. If your thing *fails to happen* and you want the default action to be carried out then return `false`.

```java	
	/*
	 *  Called when block has been harvested
	 */
	public boolean hookOnBlockHarvested (Minecraft minecraft, World world, int x, int y, int z, int blockID, int metadata) {
		return false;
	}
```

I've used this to create a golden pickaxe with silk touch. Once the item has been created you give it the special ability by adding this code to your `hookOnBlockHarvested`:

```java
    public boolean hookOnBlockHarvested (Minecraft minecraft, World world, int x, int y, int z, int blockID, int metadata) {

    	// Get the item being held by the player:
        ItemStack curItem = minecraft.thePlayer.inventory.getCurrentItem();
        
        // If it's not null
        if (curItem != null) {

        	// and happens to be our special golden pickaxe
            if (curItem.itemID == Item.pickaxeGold.shiftedIndex) {
                
                // Spawn a new EntityItem representing the same block that has been harvested!
                // This code is lifted from `Block.dropBlockAsItemWithChance`
                float px = world.random.nextFloat() * 0.7F + 0.15F;
                float py = world.random.nextFloat() * 0.7F + 0.15F;
                float pz = world.random.nextFloat() * 0.7F + 0.15F;

                // this is: the same block --------------------------------------------------------------------------------+
                //                                                                                                         |
                EntityItem entityItem = new EntityItem(world, (float)x + px, (float)y + py, (float)z + pz, new ItemStack(blockID));
                entityItem.delayBeforeCanPickup = 10;
                world.spawnEntityInWorld(entityItem);
                
                // Return true to override the default action
                return true;
            }
        }
        
        // Generally, return false for the default action is performed
        return false;
    } 
```

Check the diary for more info about this example.

## `HookAttackStrengthModifier`

Modifies the player's attack strength. It receives an `EntityLiving` representing the player (to be expanded to any EntityLiving in the future), an `Entity` representing the entity being hit, and `strength` with the original strength value.

```java	
	/*
	 *  Called to recalculate player hit strength vs. entity
	 */
	public int HookAttackStrengthModifier (EntityLiving entityLiving, Entity entityHit, int strength) {
		return strength;
	}
```

You can use this to make the player stronger or weaker under certain circumstances, for instance you can check if the player or the hit entity has a certain `StatusEffect` and apply a modification upon its type, like this:

```java
	public int HookAttackStrengthModifier (EntityLiving entityLiving, Entity entityHit, int strength) {
		if (entityLiving.isStatusActive (statusWeak)) {
			strength = strength >> 1;
		}

		if (entityHit.isStatusActive (statusWeak) {
			strength = strength << 1;
		}

		return strength;
	}
```

Check the documentation section about status effects.

## `HookBlockHitStrengthModifier`

Modifies the player's strength when hitting a block. The original value depends on the tool and material. It receives an `EntityLiving` representing the player, a `Block` representing the block being hit, and the orginal `strength` as calculated by the engine. 

```java	
	/*
	 *  Called to recalculate player hit strength vs. block
	 */
	public float HookBlockHitStrengthModifier (EntityLiving entityLiving, Block block, float strength) {
		return strength;
	}
```

You can use this to make the player mine faster or slower under certain circumstances, depending on the block or maybe a certain `StatusEffect`:

```java
	public float HookBlockHitStrengthModifier (EntityLiving entityLiving, Block block, float strength) {
		if (entityLiving.isStatusActive (statusMineFaster)) {
			strength = strength * 1.5F;
		}

		return strength;
	}
```

Check the documentation section about status effects.

## `hookEntitySpeedModifier`

Can be used to modify the speed of a living entity (i.e. the player). It receives an `entityLiving` representing the entity to be modified, and a `speedModifier` which is `1.0F` for no change. The value you return will be multiplied with the base speed of the living entity.

```java	
	/*
	 *  Called to calculate an entity speed modifier. Return 1.0F for no change! 
	 */
	public float hookEntitySpeedModifier (EntityLiving entityLiving) {
		return 1.0F;
	}
```

For example:

```java
	public float hookEntitySpeedModifier (EntityLiving entityLiving) {
		if (entityLiving.isStatusActive (statusHyperSpeed)) {
			return 2.0F;
		}

		return 1.0F;
	}
```