package com.mojontwins.modloader;

import net.minecraft.client.physics.MovingObjectPosition;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.EntityLiving;
import net.minecraft.game.item.Item;
import net.minecraft.game.level.World;

public class EntityThrowableBottle extends ModEntitlyThrowableSimple {
	public Item itemBottle;

	public EntityThrowableBottle(World world, EntityLiving owner, float speedMultiplier, Item itemBottle) {
		super(world, owner, speedMultiplier);
		this.itemBottle = itemBottle;
	}

	protected String getEntityString() {
		return "throwable_bottle";
	}
	
	public void onImpact(MovingObjectPosition movingObjectPosition) {
		// Glass
		this.worldObj.playSoundAtPlayer(this.posX, this.posY, this.posZ, "random.glass", 1.0F, 1.0F);
		
		int itemID = itemBottle.shiftedIndex;
		int damage = 0;
		
		if (itemID == mod_PoisonLand.itemBottleEmpty.shiftedIndex) {
			damage = 2;
		} else if (itemID == mod_PoisonLand.itemBottleWater.shiftedIndex) {
			damage = 4;
		} else if (itemID == mod_PoisonLand.itemBottleAcid.shiftedIndex) {
			damage = 10;
		} else if (itemID == mod_PoisonLand.itemBottlePoison.shiftedIndex) {
			damage = 100;
		}
		
		// Splash
		if (itemID != mod_PoisonLand.itemBottleEmpty.shiftedIndex) {
			int splashes = 4 + this.rand.nextInt(4);
			for (int i = 0; i < splashes; i++) {
				this.worldObj.spawnParticle("splash", this.posX + rand.nextFloat()-0.5F, this.posY + rand.nextFloat()-0.5F, this.posZ + rand.nextFloat()-0.5F, 0.0F, 0.0F, 0.0F);
			}
		}
		
		// Hit entity
		Entity entity = movingObjectPosition.entityHit;
		if (entity != null) entity.attackEntityFrom(null, damage);
	}
}
