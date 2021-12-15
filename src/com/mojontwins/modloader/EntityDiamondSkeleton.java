package com.mojontwins.modloader;

import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.monster.EntitySkeleton;
import net.minecraft.game.entity.other.EntityArrow;
import net.minecraft.game.level.World;
import util.MathHelper;

public class EntityDiamondSkeleton extends EntitySkeleton {

	public EntityDiamondSkeleton(World var1) {
		super(var1);
		this.texture = "/mob/diamond_skeleton.png";
		this.health = 800;
	}

    protected String getEntityString() {
        return "DiamondSkeleton";
    }
    
    protected int scoreForSure() {
    	return mod_PoisonLand.itemTalisman.shiftedIndex;
    }
    
    protected void attackEntity(Entity var1, float var2) {
        if (var2 < 15.0F) {
            var2 = var1.posX - this.posX;
            float var3 = var1.posZ - this.posZ;
            if (this.attackTime == 0) {
                EntityArrow var4;
                ++(var4 = new EntityArrow(this.worldObj, this)).posY;
                float var6 = var1.posY - 0.2F - var4.posY;
                float var5 = MathHelper.sqrt_float(var2 * var2 + var3 * var3) * 0.2F;
                this.worldObj.playSoundAtEntity(this, "random.bow", 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));
                this.worldObj.spawnEntityInWorld(var4);
                var4.setArrowHeading(var2, var6 + var5, var3, 0.6F, 12.0F);
                this.attackTime = 30;
            }

            this.rotationYaw = (float)(Math.atan2((double)var3, (double)var2) * 180.0D / 3.1415927410125732D) - 90.0F;
            this.hasAttacked = true;
        }
    }
}
