package com.mojontwins.modloader;

import java.util.List;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.client.physics.AxisAlignedBB;
import net.minecraft.client.physics.MovingObjectPosition;
import net.minecraft.client.renderer.Vec3D;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.EntityLiving;
import net.minecraft.game.level.World;
import util.MathHelper;

public abstract class ModEntitlyThrowableSimple extends Entity {
    private int xTile = -1;
    private int yTile = -1;
    private int zTile = -1;
    private int inTile = 0;
    private boolean inGround = false;
    public int arrowShake = 0;
    private EntityLiving owner;
    private int ticksInAir = 0;
    
	public ModEntitlyThrowableSimple(World world, EntityLiving owner, float speedMultiplier) {
		super(world);
		
		this.owner = owner;
		this.setSize(0.25F, 0.25F);
		
		// Copy position and rotation from owner's
		this.setPositionAndRotation(owner.posX, owner.posY, owner.posZ, owner.rotationYaw, owner.rotationPitch);
		
		// Move it slightly to the center front of `owner`.
		this.posX -= MathHelper.cos(this.rotationYaw / 180.0F * 3.1415927F) * 0.16F;
        this.posY -= 0.1F;
        this.posZ -= MathHelper.sin(this.rotationYaw / 180.0F * 3.1415927F) * 0.16F;
        this.setPosition(this.posX, this.posY, this.posZ);
        
        // Initial orientation
        this.yOffset = 0.0F;
        this.motionX = -MathHelper.sin(this.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(this.rotationPitch / 180.0F * 3.1415927F);
        this.motionZ = MathHelper.cos(this.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(this.rotationPitch / 180.0F * 3.1415927F);
        this.motionY = -MathHelper.sin(this.rotationPitch / 180.0F * 3.1415927F);
        
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, speedMultiplier, 1.0F);
	}
	
	public void setThrowableHeading(float motionX, float motionY, float motionZ, float speedMultiplier, float max) {
		// Normalize
        float var6 = MathHelper.sqrt_float(motionX * motionX + motionY * motionY + motionZ * motionZ);
        motionX /= var6;
        motionY /= var6;
        motionZ /= var6;
        
        // Some randomness
        motionX = (float)((double)motionX + this.rand.nextGaussian() * 0.007499999832361937D * (double)max);
        motionY = (float)((double)motionY + this.rand.nextGaussian() * 0.007499999832361937D * (double)max);
        motionZ = (float)((double)motionZ + this.rand.nextGaussian() * 0.007499999832361937D * (double)max);
        
        // Speed multiplier
        motionX *= speedMultiplier;
        motionY *= speedMultiplier;
        motionZ *= speedMultiplier;
        
        // Store
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        
        // Adjust angles
        float var4 = MathHelper.sqrt_float(motionX * motionX + motionZ * motionZ);
        this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2((double)motionX, (double)motionZ) * 180.0D / 3.1415927410125732D);
        this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2((double)motionY, (double)var4) * 180.0D / 3.1415927410125732D);
	}
	
	public final void onEntityUpdate() {
		super.onEntityUpdate();
		
		if (this.inGround) {
            if (this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile) == this.inTile) {
                this.setEntityDead();
                return;
            }

            this.inGround = false;
            this.motionX *= this.rand.nextFloat() * 0.2F;
            this.motionY *= this.rand.nextFloat() * 0.2F;
            this.motionZ *= this.rand.nextFloat() * 0.2F;
            this.ticksInAir = 0;
        } else {
            ++this.ticksInAir;
        }
		
		Vec3D var1 = new Vec3D(this.posX, this.posY, this.posZ);
        Vec3D var2 = new Vec3D(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        
        // Raytrace movement for this frame
        MovingObjectPosition var3 = this.worldObj.rayTraceBlocks(var1, var2);
        
        var1 = new Vec3D(this.posX, this.posY, this.posZ);
        var2 = new Vec3D(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        
        // Adjust destination if there was a collision
        if (var3 != null) {
            var2 = new Vec3D(var3.hitVec.xCoord, var3.hitVec.yCoord, var3.hitVec.zCoord);
        }
        
        // Check collision with entities
        Entity entity = null;
        @SuppressWarnings("unchecked")
		List<Entity> collidedEntities = this.worldObj.entityMap.getEntitiesWithinAABBExcludingEntity(
				this, 
				this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0F, 1.0F, 1.0F)
		);
        float d = 0.0F;

        for(int i = 0; i < collidedEntities.size(); ++i) {
        	Entity entity1 = (Entity)collidedEntities.get(i);
        	
        	if (!entity1.canBeCollidedWith() || entity1 == owner && ticksInAir < 5) {
                continue;
            }
        	
        	float f4 = 0.3F;
            AxisAlignedBB axisalignedbb = entity1.boundingBox.expand(f4, f4, f4);
            MovingObjectPosition movingobjectposition1 = axisalignedbb.calculateIntercept(var1, var2);
            
            if (movingobjectposition1 == null) {
                continue;
            }
            
            float d1 = var1.distance(movingobjectposition1.hitVec);
            
            if (d1 < d || d == 0.0D) {
            	entity = entity1;
                d = d1;
            }
        }
        
        // If collided with an entity, adjust destination:
        if (entity != null) {
            var3 = new MovingObjectPosition(entity);
        }
        
        // Collided with entity!
        if (var3 != null) {
        	this.onImpact (var3);
        }
        
        // Move
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        
        // Adjust rotation to describe an arc
        float var10 = MathHelper.sqrt_float(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float)(Math.atan2((double)this.motionX, (double)this.motionZ) * 180.0D / 3.1415927410125732D);

        for(this.rotationPitch = (float)(Math.atan2((double)this.motionY, (double)var10) * 180.0D / 3.1415927410125732D); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
        }

        while(this.rotationPitch - this.prevRotationPitch >= 180.0F) { this.prevRotationPitch += 360.0F; }

        while(this.rotationYaw - this.prevRotationYaw < -180.0F) { this.prevRotationYaw -= 360.0F; }

        while(this.rotationYaw - this.prevRotationYaw >= 180.0F) { this.prevRotationYaw += 360.0F; }

        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
        
        // Slow down gradually (more in water)
        float var11 = 0.99F;
        
        if (this.handleWaterMovement()) {
            for(int var13 = 0; var13 < 4; ++var13) {
                this.worldObj.spawnParticle("bubble", this.posX - this.motionX * 0.25F, this.posY - this.motionY * 0.25F, this.posZ - this.motionZ * 0.25F, this.motionX, this.motionY, this.motionZ);
            }

            var11 = 0.8F;
        }

        this.motionX *= var11;
        this.motionY *= var11;
        this.motionZ *= var11;
        
        // Make fall
        this.motionY -= 0.03F;
        
        // Update position
        this.setPosition(this.posX, this.posY, this.posZ);
	}
	
    public final float getShadowSize() {
        return 0.25F;
    }

	public abstract void onImpact(MovingObjectPosition movingObjectPosition);
	
	@Override
	protected String getEntityString() {
		return "mod.throwablesimple";
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound var1) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound var1) {
		// TODO Auto-generated method stub

	}

}
