package com.mojontwins.modloader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.game.entity.Entity;

public class ModRenderThrowableSimple extends Render {
	
	public ModRenderThrowableSimple() {	
	}
	
	/*
	 * Override this method as fit
	 */
	public int getItemIconIndex(Entity entity) {
		return 0;
	}

	@Override
	public void doRender(Entity entity, float x, float y, float z, float rotationYaw, float rotationPitch) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        loadTexture("/gui/items.png");
        Tessellator tessellator = Tessellator.instance;

        drawItem (tessellator, getItemIconIndex(entity));
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
	}

	private void drawItem (Tessellator tessellator, int itemIconIndex) {
        float f = (float)((itemIconIndex % 16) * 16 + 0) / 256F;
        float f1 = (float)((itemIconIndex % 16) * 16 + 16) / 256F;
        float f2 = (float)((itemIconIndex / 16) * 16 + 0) / 256F;
        float f3 = (float)((itemIconIndex / 16) * 16 + 16) / 256F;
        float f4 = 1.0F;
        float f5 = 0.5F;
        float f6 = 0.25F;
        GL11.glRotatef(180F - renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        tessellator.startDrawingQuads();
        Tessellator.setNormal(0.0F, 1.0F, 0.0F);
        tessellator.addVertexWithUV(0.0F - f5, 0.0F - f6, 0.0F, f, f3);
        tessellator.addVertexWithUV(f4 - f5, 0.0F - f6, 0.0F, f1, f3);
        tessellator.addVertexWithUV(f4 - f5, f4 - f6, 0.0F, f1, f2);
        tessellator.addVertexWithUV(0.0F - f5, f4 - f6, 0.0F, f, f2);
        tessellator.draw();
	}
}