package com.mojontwins.modloader;

import net.minecraft.game.entity.monster.EntityMob;
import net.minecraft.game.item.Item;
import net.minecraft.game.level.World;

public class EntityHusk extends EntityMob {
    public EntityHusk(World var1) {
        super(var1);
        this.texture = "/mob/husk.png";
        this.moveSpeed = 0.7F;
        this.attackStrength = 7;
    }

    public final void onLivingUpdate() {
        // Nothing special for the moment
        super.onLivingUpdate();
    }

    public  final String getEntityString() {
        return "Husk";
    }

    protected final int scoreValue() {
        return Item.feather.shiftedIndex;
    }
    
    public boolean getCanSpawnHere(float var1, float var2, float var3) {
    	this.setPosition(var1, var2 + this.height / 2.0F, var3);
        return this.worldObj.checkIfAABBIsClear1(this.boundingBox) && this.worldObj.getCollidingBoundingBoxes(this.boundingBox).size() == 0 && !this.worldObj.getIsAnyLiquid(this.boundingBox);
    }
    
    protected String getLivingSound() {
        return "mob.zombie1";
    }

    protected String getHurtSound() {
        return "mob.zombiehurt1";
    }

    protected String getDeathSound() {
        return "mob.zombiedeath";
    }
}