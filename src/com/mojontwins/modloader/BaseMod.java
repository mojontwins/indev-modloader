package com.mojontwins.modloader;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.game.block.Block;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.EntityLiving;
import net.minecraft.game.level.World;
import net.minecraft.game.level.generator.LevelGenerator;

abstract class BaseMod {
	public abstract void load () throws Exception;
	
	public void modsLoaded () {	
	}
	
	/*
	 * Override to add your custom block renderers (when block is in the inventory)
	 */
    public void renderInvBlock(RenderBlocks renderblocks, Block block, int renderType) {    	
    }

	/*
	 * Override to add your custom block renderers (when block is in the world)
	 */
    public boolean renderWorldBlock(RenderBlocks renderblocks, World world, int x, int y, int z, Block block, int renderType) {
        return false;
    }
	
	/* 
	 * Called while generating the world.
	 * Runs after the player spawn point has been calculated and the house has been generated.
	 */
	public void hookGenerateStructures (LevelGenerator levelGenerator, World world) {
	}
	
	/* 
	 * Called while generating the world.
	 * Runs at the end of the `Planting` stage of level generation
	 */
    public void hookPlanting (LevelGenerator levelGenerator, World world, Random rand) {
    }   	
	
	/*
	 * Called right before the game starts
	 */
	public void hookGameStart (Minecraft minecraft) {		
	}
	
	/*
	 *  Called when block has been harvested
	 */
	public boolean hookOnBlockHarvested (Minecraft minecraft, World world, int x, int y, int z, int blockID, int metadata) {
		return false;
	}
	
	/*
	 *  Called to recalculate player hit strength vs. entity
	 */
	public int hookAttackStrengthModifier (EntityLiving entityLiving, Entity entityHit, int strength) {
		return strength;
	}
	
	/*
	 *  Called to recalculate player hit strength vs. block
	 */
	public float hookBlockHitStrengthModifier (EntityLiving entityLiving, Block block, float strength) {
		return strength;
	}
	
	/*
	 *  Called to calculate an entity speed modifier. Return 1.0F for no change! 
	 */
	public float hookEntitySpeedModifier (EntityLiving entityLiving) {
		return 1.0F;
	}
	
	/*
	 * Called before generating a new level to add custom mobs to the lists
	 * Place your calls to `registerMonsterEntity` & `registerAnimalEntity` here
	 */
	public void populateMobsHashMap (int levelType) {	
	}
	
	/*
	 * Called by the creature spawner. Must return maxCreatures
	 */
	public int spawnerSetMaxHostileMobs (int maxCreatures, World world) {
		return maxCreatures;
	}

	/*
	 * Called by the creature spawner. Must return maxCreatures
	 */
	public int spawnerSetMaxNonHostileMobs (int maxCreatures, World world) {
		return maxCreatures;
	}

	/*
	 * Called by the creature spawner. Must return entityId
	 */
	public int spawnerSelectMonster (int entityID) {
		return entityID;
	}
	
	/*
	 * Called by the creature spawner. Must return entityId
	 */
	public int spawnerSelectMonsterBasedOnPosition (int entityID, World world, int x, int y, int z) {
		return entityID;
	}

	/*
	 * Called by the creature spawner. Return a new entity object based on entityID
	 */
	public Object spawnMonster (int entityID, World world) {
		return null;
	}
	
	/*
	 * Called by the creature spawner. Must return entityId
	 */
	public int spawnerSelectAnimal (int entityID) {
		return entityID;
	}
	
	/*
	 * Called by the creature spawner. Must return entityId
	 */
	public int spawnerSelectAnimalBasedOnPosition (int entityID, World world, int x, int y, int z) {
		return entityID;
	}

	/*
	 * Called by the creature spawner. Return a new entity object based on entityID
	 */
	public  Object spawnAnimal (int entityID, World world) {
		return null;
	}
}
