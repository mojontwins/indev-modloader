package com.mojontwins.modloader;

import net.minecraft.game.entity.monster.EntitySkeleton;
import net.minecraft.game.item.Item;
import net.minecraft.game.level.World;

public class EntityPoisonSkeleton extends EntitySkeleton {

	public EntityPoisonSkeleton(World var1) {
		super(var1);
		this.texture = "/mob/poison_skeleton.png";
	}

    protected String getEntityString() {
        return "PoisonSkeleton";
    }
    
    protected int scoreValue() {
        return (rand.nextInt(8) == 0) ? mod_PoisonLand.blockSkullHead.blockID : Item.arrow.shiftedIndex;
    }    
}
