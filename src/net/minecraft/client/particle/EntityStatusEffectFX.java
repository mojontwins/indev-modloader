package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.game.level.World;

public class EntityStatusEffectFX extends EntityFX {

	public EntityStatusEffectFX(World world, float x, float y, float z, float vx, float vy, float vz) {
		super(world, x, y, z, vx, vy, vz);
		
		motionY *= 0.20000000298023224D;
        if (vx == 0.0D && vz == 0.0D) {
            motionX *= 0.10000000149011612D;
            motionZ *= 0.10000000149011612D;
        }
        particleScale *= 0.75F;
        particleMaxAge = (int)(8D / (Math.random() * 0.80000000000000004D + 0.20000000000000001D));
        noClip = false;
	}

	public void setParticleColor (float r, float g, float b) {
		particleRed = r;
		particleGreen = g;
		particleBlue = b;
	}
	
    public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7) {
        float f = (((float)particleAge + par2) / (float)particleMaxAge) * 32F;
        if (f < 0.0F) f = 0.0F;
        if (f > 1.0F) f = 1.0F;
        
        super.renderParticle(par1Tessellator, par2, par3, par4, par5, par6, par7);
    }	
    
    public void onEntityUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        if (particleAge++ >= particleMaxAge) setEntityDead();

        particleTextureIndex = 128 + (7 - (particleAge * 8) / particleMaxAge);

        motionY += 0.0040000000000000001D;
        moveEntity(motionX, motionY, motionZ);

        if (posY == prevPosY) {
            motionX *= 1.1000000000000001D;
            motionZ *= 1.1000000000000001D;
        }

        motionX *= 0.95999997854232788D;
        motionY *= 0.95999997854232788D;
        motionZ *= 0.95999997854232788D;

        if (onGround) {
            motionX *= 0.69999998807907104D;
            motionZ *= 0.69999998807907104D;
        }
    }    
}
